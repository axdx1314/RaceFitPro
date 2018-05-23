package com.exalpme.bozhilun.android.siswatch.utils;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.exalpme.bozhilun.android.w30s.views.CommomDialog;
import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.net.OkHttpObservable;
import com.exalpme.bozhilun.android.rxandroid.CommonSubscriber;
import com.exalpme.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.exalpme.bozhilun.android.util.ToastUtil;
import com.exalpme.bozhilun.android.view.PromptDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2017/9/28.
 */

public class UpdateManager {

    private Context mContext;
    private String url;
    private CommonSubscriber commonSubscriber;


    public UpdateManager(Context mContext, String url) {
        this.mContext = mContext;
        this.url = url;

    }

    public void setUpdate(final String upUrl) {
        final PromptDialog pd = new PromptDialog(mContext);
        pd.show();
        pd.setTitle(mContext.getResources().getString(R.string.prompt));
        pd.setContent(mContext.getResources().getString(R.string.newversion));
        pd.setleftText("NO");
        pd.setrightText("YES");
        pd.setListener(new PromptDialog.OnPromptDialogListener() {
            @Override
            public void leftClick(int code) {
                pd.dismiss();
            }

            @Override
            public void rightClick(int code) {
                pd.dismiss();
                Intent intent = new Intent(mContext.getApplicationContext(), UpdateWebViewActivity.class);
                intent.putExtra("updateUrl", upUrl);
                mContext.startActivity(intent);
            }
        });
    }


    public void setUpdate2() {
        synchronized (mContext) {
            CommomDialog commomDialog = new CommomDialog(mContext, R.style.alertDialog);
            boolean showing = commomDialog.isShowing();
            Log.d("========show==", showing + "");
            if (!showing) {
                commomDialog.setTitle(mContext.getResources().getString(R.string.newversion));
                commomDialog.setContent(mContext.getResources().getString(R.string.string_updata));
                commomDialog.setListener(new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            // 开始现在----通知栏
                            showDownloadDialog();
                        }
                        dialog.dismiss();
                    }
                });
                //点击window外的区域 是否消失
                commomDialog.setCanceledOnTouchOutside(false);
                commomDialog.show();
            }
        }
    }

    public void checkForUpdate(final boolean isTrue) {
        if (url != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("version", WatchUtils.getVersionCode(mContext));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String result) {
                    Log.e("update", "---result---" + result);
                    if (!WatchUtils.isEmpty(result)) {
                        try {
                            JSONObject jsono = new JSONObject(result);
                            if (jsono.getString("resultCode").equals("001")) {
                                String verStr = jsono.getString("versionInfo");
                                if (!WatchUtils.isEmpty(verStr)) {
                                    JSONObject versionInfo = new JSONObject(verStr);
                                    int version = versionInfo.getInt("version");
                                    if (version > WatchUtils.getVersionCode(mContext)) {
                                        //setUpdate(versionInfo.getString("url"));
                                        setUpdate2();
                                    } else {
                                        if (isTrue) {//latest_version
                                            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.latest_version));
                                        }
                                    }
                                } else {
                                    if (isTrue) {
                                        ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.latest_version));
                                    }

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }, mContext);
            OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObject.toString());

        }
    }


    private DownloadManager mDownloadManager;
    private long downloadId;

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        try {
            File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "RaceFitPro.apk");
            if (apkFile!=null&&!WatchUtils.isEmpty(apkFile.getPath())){
                rmoveFile(apkFile.getPath());
            }
            //应用宝下载链接
            String downPath = "http://imtt.dd.qq.com/16891/F2348496960C064EC9B355F194BEE1D8.apk?fsname=com.example.bozhilun.android_3.6.0.6_44.apk";//下载路径 根据服务器返回的apk存放路径
            mContext.registerReceiver(mReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            //使用系统下载类
            mDownloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(downPath);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedOverRoaming(false);

            //创建目录下载
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "RaceFitPro.apk");
            // 把id保存好，在接收者里面要用
            downloadId = mDownloadManager.enqueue(request);
            //设置允许使用的网络类型，这里是移动网络和wifi都可以
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            //机型适配
            request.setMimeType("application/vnd.android.package-archive");
            //通知栏显示
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("Download");
            request.setDescription("Downloading ...");
            request.setVisibleInDownloadsUi(true);
        }catch (Exception e){
            e.getMessage();
        }
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    checkStatus();
                    break;
            }

        }
    };


    /**
     * 检查下载状态
     */
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    installAPK();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "download failure", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        cursor.close();
    }

    /**
     * 7.0兼容
     */
    private void installAPK() {
        File apkFile =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "RaceFitPro.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }



    public File getPathFile(String path){
        String apkName = path.substring(path.lastIndexOf("/"));
        File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), apkName);
        return outputFile;
    }

    public void rmoveFile(String path){
        File file = getPathFile(path);
        file.delete();
    }

}

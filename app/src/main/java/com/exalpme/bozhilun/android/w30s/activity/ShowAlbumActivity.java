package com.exalpme.bozhilun.android.w30s.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bm.library.PhotoView;
import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.siswatch.WatchBaseActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;


/**
 * Created by Administrator on 2018/3/31.
 */

public class ShowAlbumActivity extends WatchBaseActivity {

    PhotoView showPhotoView;
    FloatingActionButton fb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showalbum);

        showPhotoView = (PhotoView) findViewById(R.id.showPhotoView);
        fb = (FloatingActionButton) findViewById(R.id.albumDeleteFloat);

        final String url = getIntent().getStringExtra("imgUrl");
        if (url != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            showPhotoView.setImageBitmap(bitmap);
            showPhotoView.enable();
        }

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ShowAlbumActivity.this).title(R.string.prompt)
                        .content(getResources().getString(R.string.deleda))
                        .positiveText(R.string.confirm)
                        .negativeText(R.string.cancle)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                deleteFile(new File(url));
                                finish();
                            }
                        }).show();
            }
        });

    }

    //自定义ViewPager的Adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<String> imgList;

        public MyViewPagerAdapter(Context mContext, List<String> imgList) {
            this.mContext = mContext;
            this.imgList = imgList;
        }

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(mContext);
            photoView.enable();     //设置开启缩放
            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(photoView);
            //Glide加载本地的图片
            Glide.with(ShowAlbumActivity.this)
                    .load(imgList.get(position))
                    .into(photoView);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    public void deleteFile(File file) {
        // TODO Auto-generated method stub
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                DeleteFile(f);
            }
            file.delete();
        }
    }

    private void DeleteFile(File f) {
        // TODO Auto-generated method stub
        f.delete();
    }
}

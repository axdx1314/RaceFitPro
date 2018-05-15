package com.exalpme.bozhilun.android.w30s.carema.news;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.R;
import com.linj.FileOperateUtil;
import com.linj.album.view.AlbumGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class W30SAlbumAty extends AppCompatActivity {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.rela_layout_title)
    RelativeLayout relaLayoutTitle;
    @BindView(R.id.my_albumview)
    AlbumGridView myAlbumview;
    @BindView(R.id.delete)
    Button delete;
    @BindView(R.id.btn_staue)
    LinearLayout btnStaue;
    private String mSaveRoot = "W30sPic";
    List<String> paths = new ArrayList<String>();
    AlbumGridView.AlbumViewAdapter albumViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30_salbum_aty);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.take_Album));
        myAlbumview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (myAlbumview.getEditable()) return;
                //Intent intent = new Intent(W30SAlbumAty.this, ShowAlbumActivity.class);
                Intent intent = new Intent(W30SAlbumAty.this, AlbumItemAty.class);
                intent.putExtra("path", view.getTag().toString());
                startActivity(intent);
            }
        });
        myAlbumview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (myAlbumview.getEditable()) return true;

                delete.setEnabled(false);
                btnStaue.setVisibility(View.VISIBLE);
                myAlbumview.setEditable(true, new AlbumGridView.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(Set<String> set) {
                        if (set.size() > 0) {
                            delete.setEnabled(true);
                        } else {
                            Toast.makeText(W30SAlbumAty.this, "Not selected!", Toast.LENGTH_SHORT).show();
                            delete.setEnabled(false);
                        }
                    }
                });
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        loadAlbum(mSaveRoot, ".jpg");
        super.onResume();
    }

    /**
     * @param rootPath
     * @param format
     */
    public void loadAlbum(String rootPath, String format) {

        String thumbFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, rootPath);
        List<File> files = FileOperateUtil.listFiles(thumbFolder, format);
        if (paths != null) paths.clear();
        if (files != null && files.size() > 0) {
            for (File file : files) {
                paths.add(file.getAbsolutePath());
            }
        }
        if (albumViewAdapter == null) {
            albumViewAdapter = myAlbumview.new AlbumViewAdapter(paths);
        }
        myAlbumview.setAdapter(albumViewAdapter);
        albumViewAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.image_back, R.id.delete, R.id.no_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.delete:
                showDeleteDialog();
                break;
            case R.id.no_delete:
                leaveEdit();
                break;
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.deleda))
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Set<String> items = myAlbumview.getSelectedItems();
                        for (String path : items) {
                            boolean flag = FileOperateUtil.deleteThumbFile(path, W30SAlbumAty.this);
                            if (!flag) Log.i("===", path);
                        }
                        loadAlbum(mSaveRoot, ".jpg");
                        leaveEdit();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void leaveEdit() {
        loadAlbum(mSaveRoot, ".jpg");
        myAlbumview.setEditable(false);
        delete.setEnabled(false);
        btnStaue.setVisibility(View.GONE);
    }
}

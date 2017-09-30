package com.zfs;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.zxinglib.decode.QrCodeDecoder;
import com.zxinglib.decode.ResultCallback;
import com.zxinglib.encode.CodeCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_FROM_ALBUM = 1;
    EditText et;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.etUrl);
        iv = (ImageView) findViewById(R.id.ivQr);
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                generate(true);               
                return true;
            }
        });
    }
    
    /**
     * 保存bitmap到文件
     * @param photoFile 文件
     */
    public static void saveBitmapToFile(Bitmap bitmap, File photoFile, int quality){
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (bitmap != null) {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (Exception e) {
            photoFile.delete();
            e.printStackTrace();
        } finally{
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void generate(boolean save) {
        String s = et.getText().toString().trim();
        if (!TextUtils.isEmpty(s)) {
            try {
                Bitmap bitmap = CodeCreator.createQRCode(s);
                if (bitmap != null) {
                    if (save) {
                        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        File file = new File(directory, "qrcode.jpg");
                        saveBitmapToFile(bitmap, file, 100);
                        if (file.exists()) {
                            Toast.makeText(MainActivity.this, "二维码已保存至：" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        iv.setImageBitmap(bitmap);
                    }                    
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }      
    }
    
    public void generate(View view) {
        generate(false);        
    }

    public void scan(View view) {
        startActivity(new Intent(this, CaptrueActivity.class));
    }

    public void selectPic(View view) {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, REQUEST_SELECT_FROM_ALBUM);
    }

    /**
     * 根据Uri获取图片路径
     */
    public static String getImagePath(Context context, Uri uri) {
        String s = Uri.decode(uri.toString());
        if (s.startsWith("content://")) {
            CursorLoader cursorLoader = new CursorLoader(context, uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);// 图片在的路径
        } else if (s.startsWith("file://")) {
            return s.substring(7);
        }
        return null;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_SELECT_FROM_ALBUM:
                    QrCodeDecoder.decodeQRCode(getImagePath(this, data.getData()), new ResultCallback() {
                        @Override
                        public void onResult(Result result) {
                            if (result != null) {
                                Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "解析失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            		break;
            }
        }
    }
}

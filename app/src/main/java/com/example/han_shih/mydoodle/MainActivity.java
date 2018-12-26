package com.example.han_shih.mydoodle;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private PaintView mpaintView;
    private ColorPickerDialog mColorDialog;
    private Dialog mDialog;
    private ListView mFileListView;
    private File mpath;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //can not get widget before the view being created
        mpaintView = (PaintView)findViewById(R.id.paintView);

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        //mtoolbar.setTitle("Toolbar Demo");
        mtoolbar.setSubtitle("Toolbar Demo");
        setSupportActionBar(mtoolbar);
        /*
        mtoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            //must after "setSupportActionBar"
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                String msg = "";
                switch (item.getItemId()) {
                    case R.id.action_exit:
                        msg += "Click exit";
                        break;
                    case R.id.action_about:
                        msg += "Click about";
                        break;
                    case R.id.action_settings:
                        msg += "Click setting";
                        break;
                }

                if(!msg.equals("")) Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        */
        mtoolbar.setNavigationIcon(R.drawable.ic_android_black_24dp);
        //mtoolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);  //must after "setSupportActionBar"



    }
    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear(); //Clear view of previous menu
        MenuInflater inflater = getMenuInflater();
        if(condition_true)
            inflater.inflate(R.menu.menu_one, menu);
        else
            inflater.inflate(R.menu.menu_two, menu);
        return super.onPrepareOptionsMenu(menu);
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("In switch", "I'm here~");

        switch (item.getItemId()) {
            case R.id.action_exit:
                //mtv.setText("About");
                break;
            case R.id.action_about:
                //mtv.setText("Exit");
                break;
            case R.id.action_reset:
                //mtv.setText("Setting");
                reset(mpaintView);
                break;
        }

        //if(!msg.equals("")) Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

        return true;
    }

    public Bitmap getBitmap(ConstraintLayout layout){
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false);
        return bmp;
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //@Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.imageButton :                //msg += "color select";
                mColorDialog = new ColorPickerDialog(this, mpaintView.getPaintColor(),
                        new ColorPickerDialog.OnColorChangedListener() {
                            @Override
                            public void colorChanged(int color) {
                                mpaintView.setPaintColor(String.format("#%06X", (0xFFFFFF & color)));
                            }
                        });
                mColorDialog.setCancelable(true);
                mColorDialog.setCanceledOnTouchOutside(true);
                mColorDialog.show();
                break;

            case R.id.imageButton2 :                //msg += "brush size";
                final Dialog brushDialog = new Dialog(this);
                //brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.size_layout);
                brushDialog.setCancelable(true);
                brushDialog.setCanceledOnTouchOutside(true);
                final SeekBar seekBar = (SeekBar) brushDialog.findViewById(R.id.seekBar);
                seekBar.setProgress(mpaintView.getBrushSize()/2);

                Button button = (Button) brushDialog.findViewById(R.id.cancel);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        brushDialog.dismiss();
                    }
                });

                button = (Button) brushDialog.findViewById(R.id.confirm);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mpaintView.setBrushSize(seekBar.getProgress()*2);
                        brushDialog.dismiss();
                    }
                });

                brushDialog.show();
                break;

            case R.id.imageButton3 :                //msg += "background color";
                mColorDialog = new ColorPickerDialog(this, mpaintView.getPaintColor(),
                        new ColorPickerDialog.OnColorChangedListener() {
                            @Override
                            public void colorChanged(int color) {
                                findViewById(R.id.paintView).setBackgroundColor(color);
                            }
                        });
                mColorDialog.setCancelable(true);
                mColorDialog.setCanceledOnTouchOutside(true);
                mColorDialog.show();
                break;

            case R.id.imageButton4 :                //msg += "save to...";
                bitmap = viewToBitmap(findViewById(R.id.paintView));
                checkPermission();

                try {
                    mpath=new File("/sdcard/Download","file.jpg");
                    Log.d("Print the path", "/file.png");
                    FileOutputStream output = new FileOutputStream(mpath);

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.close();
                } catch (FileNotFoundException e) {
                    Log.d("FileNotFoundException", "/file.png");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("IOException", "/file.png");
                    e.printStackTrace();
                }
                break;

            case R.id.imageButton5 :                //msg += "load from...";
                checkPermission();
                bitmap = null;
                mpath=new File("/sdcard/Download","file.jpg");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(mpath), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                BitmapDrawable bd = new BitmapDrawable(mpaintView.getContext().getResources(), bitmap);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mpaintView.setBackground(bd);
                } else {
                    mpaintView.setBackgroundDrawable(bd);
                }

                break;
        }
        //if(!msg.equals("")) Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public  boolean isPermissionGrantedForStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    // permission was granted! Do the contacts-related task you need.
                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Provide permission for external storage?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS: app-defined int constant.
                // The callback method gets the result of the request.
            }
        }
    }

    void reset(PaintView view){
        view.drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        view.setBackgroundColor(0xFF000000);

    }


}

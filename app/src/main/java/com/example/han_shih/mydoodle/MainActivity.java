package com.example.han_shih.mydoodle;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private PaintView mpaintView;

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
        //TextView mtv = (TextView) findViewById(R.id.textview);
        Log.d("In switch", "I'm here~");

        String msg = "";
        switch (item.getItemId()) {
            case R.id.action_exit:
                //mtv.setText("About");
                msg += "Click exit";
                break;
            case R.id.action_about:
                //mtv.setText("Exit");
                msg += "Click about";
                break;
            case R.id.action_settings:
                //mtv.setText("Setting");
                msg += "Click setting";
                break;
        }

        if(!msg.equals("")) Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

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
        //String msg = "";
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        ColorPickerDialog dialog;
        switch (v.getId()){
            case R.id.imageButton :                //msg += "color select";
                dialog = new ColorPickerDialog(this, mpaintView.getPaintColor(),
                        new ColorPickerDialog.OnColorChangedListener() {
                            @Override
                            public void colorChanged(int color) {
                                mpaintView.setPaintColor(String.format("#%06X", (0xFFFFFF & color)));
                            }
                        });

                dialog.show();
                break;

            case R.id.imageButton2 :                //msg += "brush size";
                inflater.inflate(R.menu.menu_size, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getApplicationContext(),
                                item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show();
                break;

            case R.id.imageButton3 :                //msg += "background color";
                inflater.inflate(R.menu.menu_color, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        findViewById(R.id.paintView).setBackgroundColor(Color.parseColor(item.getTitleCondensed().toString()));
                        return true;
                    }
                });
                popup.show();
                break;


            case R.id.imageButton4 :                //msg += "save to...";
                Bitmap bitmap = viewToBitmap(findViewById(R.id.paintView));

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

                try {
                    File mpath=new File("/sdcard/Download","file.jpg");
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

            case R.id.imageButton5 :
                //msg += "load from...";
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Provide permission for external storage?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
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

}

package com.computer.service.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.computer.service.Config;
import com.computer.service.R;
import com.computer.service.utilities.DBHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class ActivityMenuDetail extends AppCompatActivity {

    ImageView imgPreview;
    TextView txtText, txtSubText;
    CoordinatorLayout coordinatorLayout;
    ProgressBar prgLoading;
    TextView txtAlert;
    WebView txtDescription;
    public static DBHelper dbhelper;
    String Menu_image, Menu_name, Menu_serve, Menu_description;
    double Menu_price;
    int Menu_quantity;
    long Menu_ID;
    String MenuDetailAPI;
    int IOConnect = 0;

    DecimalFormat formatData = new DecimalFormat("#.##");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");

        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        txtText = (TextView) findViewById(R.id.txtText);
        txtSubText = (TextView) findViewById(R.id.txtSubText);

        //txtDescription = (WebView) findViewById(R.id.txtDescription);
        txtDescription = (WebView) findViewById(R.id.txtDescription);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);

        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMenuDetail.this, ActivityMenuDescription.class);
                i.putExtra("desc", Menu_description);
                startActivity(i);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog();
            }
        });

        com.github.clans.fab.FloatingActionButton fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.cart);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityCart.class));
            }
        });

        com.github.clans.fab.FloatingActionButton fab3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.checkout);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityCheckout.class));
            }
        });

        com.github.clans.fab.FloatingActionButton fab4 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.save);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new SaveTask(ActivityMenuDetail.this)).execute(Config.ADMIN_PANEL_URL + "/" + Menu_image);
            }
        });

        //imageLoader = new ImageLoader(ActivityMenuDetail.this);
        dbhelper = new DBHelper(this);

        // get menu id that sent from previous page
        Intent iGet = getIntent();
        Menu_ID = iGet.getLongExtra("menu_id", 0);

        // Menu detail API url
        MenuDetailAPI = Config.ADMIN_PANEL_URL + "/api/get-menu-detail.php" + "?accesskey=" + Config.AccessKey + "&menu_id=" + Menu_ID;

        // call asynctask class to request data from server
        new getDataTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;

            case R.id.buy:
                inputDialog();
                return true;

            case R.id.cart:
                startActivity(new Intent(getApplicationContext(), ActivityCart.class));
                return true;

            case R.id.checkout:
                startActivity(new Intent(getApplicationContext(), ActivityCheckout.class));
                return true;

            case R.id.save:
                (new SaveTask(ActivityMenuDetail.this)).execute(Config.ADMIN_PANEL_URL + "/" + Menu_image);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // method to show number of order form
    void inputDialog() {

        // open database first
        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.order);
        alert.setMessage(R.string.number_order);
        alert.setCancelable(false);
        final EditText edtQuantity = new EditText(this);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(edtQuantity);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String temp = edtQuantity.getText().toString();
                int quantity = 0;

                // when add button clicked add menu to order table in database
                if (!temp.equalsIgnoreCase("")) {
                    quantity = Integer.parseInt(temp);
                    Toast.makeText(getApplicationContext(), "Success add product to cart", Toast.LENGTH_SHORT).show();

                    if (dbhelper.isDataExist(Menu_ID)) {
                        dbhelper.updateData(Menu_ID, quantity, (Menu_price * quantity));
                    } else {
                        dbhelper.addData(Menu_ID, Menu_name, quantity, (Menu_price * quantity));
                    }
                } else {
                    dialog.cancel();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // when cancel button clicked close dialog
                dialog.cancel();
            }
        });

        alert.show();
    }

    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void> {

        // show progressbar first
        getDataTask() {
            if (!prgLoading.isShown()) {
                prgLoading.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            // parse json data from server in background
            parseJSONData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // when finish parsing, hide progressbar
            prgLoading.setVisibility(View.GONE);
            // if internet connection and data available show data
            // otherwise, show alert text
            if ((Menu_name != null) && IOConnect == 0) {
                coordinatorLayout.setVisibility(View.VISIBLE);

                Picasso.with(getApplicationContext()).load(Config.ADMIN_PANEL_URL + "/" + Menu_image).placeholder(R.drawable.loading).into(imgPreview, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

                txtText.setText(Menu_name);
                txtSubText.setText("Price : " + Menu_price + " " + ActivityMenuList.Currency + "\n" + "Status : " + Menu_serve + "\n" + "Stock : " + Menu_quantity);
              /*  txtDescription.loadDataWithBaseURL("", Menu_description, "text/html", "UTF-8", "");
                txtDescription.setBackgroundColor(Color.parseColor("#ffffff"));

                txtDescription.getSettings().setDefaultTextEncodingName("UTF-8");
                WebSettings webSettings = txtDescription.getSettings();
                Resources res = getResources();
                int fontSize = res.getInteger(R.integer.font_size);
                webSettings.setDefaultFontSize(fontSize);
*/
            } else {
                txtAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    // method to parse json data from server
    public void parseJSONData() {

        try {
            // request data from menu detail API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(MenuDetailAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();


            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            String str = "";
            while ((line = in.readLine()) != null) {
                str += line;
            }

            // parse json data and store into tax and currency variables
            JSONObject json = new JSONObject(str);
            JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part

            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                JSONObject menu = object.getJSONObject("Menu_detail");

                Menu_image = menu.getString("Menu_image");
                Menu_name = menu.getString("Menu_name");
                Menu_price = Double.valueOf(formatData.format(menu.getDouble("Price")));
                Menu_serve = menu.getString("Serve_for");
                Menu_description = menu.getString("Description");
                Menu_quantity = menu.getInt("Quantity");

            }


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            IOConnect = 1;
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // close database before back to previous page
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        dbhelper.close();
        finish();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        //imageLoader.clearCache();
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }

    public class SaveTask extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public SaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Downloading Image ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            String as[] = null;
            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/Ecommerce Android App/");
                dir.mkdirs();
                String fileName = "Image_" + "_" + idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                as = new String[1];
                as[0] = file.toString();

                MediaScannerConnection.scanFile(ActivityMenuDetail.this, as, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String s1, Uri uri) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            Toast.makeText(getApplicationContext(), "Image Saved Succesfully", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }

}

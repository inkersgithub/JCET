package in.inkers.jcet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    WebView webView;
    String url;
    Boolean exit;
    NavigationView navigationView;

    ProgressBar pbLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        pbLoading = findViewById(R.id.pbLoading);


        if(!isConnected(MainActivity.this)) {  //checks internet connection
            startActivity(new Intent(this,ErrorActivity.class));
            finish();
        }

        exit =false;
        url="http://jawaharlalcolleges.com/launch/";

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.layoutDrawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView.setWebViewClient(new JcetWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.clearCache(true);
        webView.loadUrl(url);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_item_home:
                url = "http://jawaharlalcolleges.com/launch/";
                webView.loadUrl(url);
                break;

            case R.id.menu_item_admissions:
                url = "http://jawaharlalcolleges.com/app/adms.html";
                webView.loadUrl(url);
                break;

            case R.id.menu_item_contactus:
                url = "http://jawaharlalcolleges.com/app/contact.html";
                webView.loadUrl(url);
                break;

            case R.id.menu_item_announcements:
                url = "http://jawaharlalcolleges.com/app/not.html";
                webView.loadUrl(url);
                break;

            case R.id.menu_item_events:
                url = "http://jawaharlalcolleges.com/blog/";
                webView.loadUrl(url);
                break;

            case R.id.menu_item_virtualtour:
                url = "http://jawaharlalcolleges.com/app/360.html";
                webView.loadUrl(url);

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    public class JcetWebViewClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            pbLoading.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            webView.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            startActivity(new Intent(MainActivity.this,ErrorActivity.class));
            finish();
        }

    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        }
        else
            return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //to load last page when
        if (event.getAction() == KeyEvent.ACTION_DOWN) {    //when physical back button pressed
            switch (keyCode) {                              //instead of closing app
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        if(exit){
                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory( Intent.CATEGORY_HOME );
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                        }
                        else{
                            Toast.makeText(this,"Press BACK again to exit", Toast.LENGTH_SHORT).show();
                            this.exit=true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    exit=false;
                                }
                            },3*1000);
                        }
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void aboutClick(MenuItem item){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.credit_dialog_layout,null);
        dialogBuilder.setView(view);

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }
}

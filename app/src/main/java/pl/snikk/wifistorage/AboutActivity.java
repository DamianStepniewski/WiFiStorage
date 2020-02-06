package pl.snikk.wifistorage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class AboutActivity extends Activity {

    private static final String LICENSES = "licenses.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        try {
            AssetManager assetManager = getAssets();
            InputStream input = assetManager.open(LICENSES);
            Scanner s = new Scanner(input);
            s.useDelimiter("\\A");
            String html = s.hasNext() ? s.next() : "";
            s.close();

            WebView webview = (WebView) findViewById(R.id.webView);
            webview.setBackgroundColor(0x00000000);
            webview.loadData(html, "text/html", "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView version = (TextView) findViewById(R.id.txtAboutVersion);
            version.setText(pInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}


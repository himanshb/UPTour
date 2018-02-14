package com.example.pc.uptour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Googlectivity extends AppCompatActivity {

    WebView hotelWebView;
    String cityName,placeType;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlectivity);
        placeType=this.getIntent().getStringExtra("place_type");
        cityName=this.getIntent().getStringExtra("city_name");
        String cityName2=cityName.substring(0,1).toUpperCase() + cityName.substring(1).toLowerCase();
        switch (placeType){
            case "place":
                url="https://www.google.co.in/destination/map/topsights?q=top+sights+"+cityName+"&rlz=1C1CHBD_enIN768IN768&site=search&output=search&dest_mid=/m/067jn5&sa=X&ved=0ahUKEwjOoK6D-6DZAhUE448KHf9wCmIQ6tEBCC4oBTAA";
                break;
            case "hotel":
                url="https://www.google.co.in/travel/hotels/"+cityName2+"?q=hotels%20in%20"+cityName+
                        "&hl=en&gl=in&ictx=1&ved=0ahUKEwiIgYz9waDZAhVFL48KHet_DF4QvDEI1AE&hrf=CgUI-FUQACIDSU5SKhYKBwjiDxACGAwSBwjiDxACGA0YASgAkgECIAE";
                break;
            case "food":
                url="https://www.google.co.in/search?q=restaurants+in+"+cityName+"&oq=restaurants+in+m&aqs=chrome.1.69i57j0l3.5930j0j7&client=ms-android-xiaomi&sourceid=chrome-mobile&ie=UTF-8#istate=lrl:xpd";
                break;
        }
        hotelWebView = findViewById(R.id.hotel_web_view);
        hotelWebView.getSettings().setJavaScriptEnabled(true);
        hotelWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(Googlectivity.this, "Loading Error", Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().hide();
        hotelWebView.loadUrl(
            url
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (hotelWebView.canGoBack()) {
                        hotelWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.turkcell.gelecegiyazanlar.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.splunk.mint.Mint;
import com.turkcell.gelecegiyazanlar.R;
import com.turkcell.gelecegiyazanlar.Configuration.AppController;
import com.turkcell.gelecegiyazanlar.Configuration.GYConfiguration;
import com.turkcell.gelecegiyazanlar.Utility.YuklenmeEkran;

import org.json.JSONArray;
import org.json.JSONException;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogIcerikAcitivity extends AppCompatActivity {

    String url;
    String avatar = "";
    RequestQueue queue;
    ImageRequest imageRequest;
    //JsonObjectRequest jsonObjectRequest;
    JsonArrayRequest stringRequest;
    String txtBaslik = "";
    YuklenmeEkran ekran;
    String nodeID;
    CircleImageView circleImageView;

    TextView yazar, yazarID, yorum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_icerik_acitivity);

        Mint.initAndStartSession(BlogIcerikAcitivity.this, GYConfiguration.SPLUNK_ID);

        url = GYConfiguration.getDomain() + "article_content/retrieve?nodeID=";
        ekran = new YuklenmeEkran(BlogIcerikAcitivity.this);
        yazar = (TextView) findViewById(R.id.yazarIsim);
        yazarID = (TextView) findViewById(R.id.yazarID);
        yorum = (TextView) findViewById(R.id.txtYorum);

        Bundle exras = getIntent().getExtras();
        nodeID = exras.getString("blogID");
        Log.d("xx:", nodeID);


        yorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BlogIcerikAcitivity.this, YorumActivity.class);
                i.putExtra("yorumID", nodeID);
                startActivity(i);
            }
        });

        if (GYConfiguration.checkInternetConnectionShowDialog(BlogIcerikAcitivity.this)) {
            ekran.surecBasla();
        }


        stringRequest = new JsonArrayRequest(Request.Method.GET, url + nodeID, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d("json:", response.toString());
                WebView webView = (WebView) findViewById(R.id.blogDetay);
                webView.getSettings().setJavaScriptEnabled(true);
                ekran.surecDurdur();
                try {

                    //Blog ba�l��� ba�lang��
                    TextView baslik = (TextView) findViewById(R.id.txtBaslik);
                    txtBaslik = response.getJSONObject(0).getString("title");
                    baslik.setText(txtBaslik);

                    String yazarIsim = response.getJSONObject(0).getString("adSoyad");
                    String authorID = response.getJSONObject(0).getString("authorID");

                    yazar.setText(yazarIsim);
                    yazarID.setText(authorID);
                    //Avatar ba�lang��
                    circleImageView = (CircleImageView) findViewById(R.id.avatar);
                    avatar = response.getJSONObject(0).getString("authorAvatarUrl");
                    AvatarYukle(avatar);

                    //Avatar biti�


                    webView.setWebViewClient(new WebViewClient() {

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            //Toast.makeText(getApplicationContext(), "Sayfa y�klendi", Toast.LENGTH_SHORT).show();

                        }

                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            Toast.makeText(getApplicationContext(), "Bir Hata Olu�tu", Toast.LENGTH_SHORT).show();

                        }
                    });


                    //Blog i�erik ba�lang��
                    webView.getSettings().setBuiltInZoomControls(true); //zoom yap�lmas�na izin verir
                    webView.getSettings().setSupportZoom(true);
                    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                    webView.getSettings().setAllowFileAccess(true);
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setUseWideViewPort(true);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.getSettings().setDefaultFontSize(40);

                    Log.d("TAG", response.getJSONObject(0).getString("content"));
                    webView.loadData(response.getJSONObject(0).getString("content")
                            , "text/html; charset=utf-8", null);
                    //Blog i�erik biti�


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("hata:", "hata:");
                    }
                });

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void AvatarYukle(String avatar) {
        imageRequest = new ImageRequest(avatar, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                circleImageView.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(imageRequest);


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yazarID.getText() != null) {
                    Intent i = new Intent(BlogIcerikAcitivity.this, ProfilActivity.class);
                    i.putExtra("id", yazarID.getText());
                    startActivity(i);
                }
            }
        });
    }

}

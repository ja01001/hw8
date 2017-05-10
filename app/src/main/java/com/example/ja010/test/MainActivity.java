package com.example.ja010.test;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    EditText e1;
    WebView web;
    ListView lv;
    LinearLayout ll;
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> nurl = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Animation ani;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e1 = (EditText)findViewById(R.id.et1);
        lv = (ListView)findViewById(R.id.lv) ;
        web = (WebView)findViewById(R.id.web);
        e1.setText("http://www.naver.com");
        names.add("ad");
        ll = (LinearLayout)findViewById(R.id.ll);
        nurl.add("http://www.google.com");
        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        lv.setAdapter(adapter);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loadding...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }
        });
        web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress >= 100){dialog.dismiss();}
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });
        web.loadUrl("http://m.naver.com"); // 초기 네이버
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
        web.addJavascriptInterface(new JavaScriptMethod(), "app");
        // set list click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                web.loadUrl(nurl.get(i));
                e1.setText(nurl.get(i));
                Toast.makeText(getApplicationContext(),""+nurl.get(i),Toast.LENGTH_SHORT).show();
                lv.setVisibility(View.INVISIBLE);
                web.setVisibility(view.VISIBLE);
            }
        });
        // long click
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                a.setTitle("삭제확인").setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        names.remove(i);
                        nurl.remove(i);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("아니오",null).setMessage("정말?").show();

                return true; // 길게 눌렀을때 onclick + onLongclick 이 같이 호출 될때는 true 로 바꾸면 끝
            }
        });
        ani = AnimationUtils.loadAnimation(this,R.anim.tt);
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                    ll.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,"즐겨찾기추가");
        menu.add(0,2,0,"즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==1){
            ll.setAnimation(ani);
            web.loadUrl("file:///android_asset/www/addurl.html");
            lv.setVisibility(View.INVISIBLE);
            web.setVisibility(View.VISIBLE);
            ani.start();
        }
        else if (item.getItemId() ==2){
            lv.setVisibility(View.VISIBLE);
            web.setVisibility(View.INVISIBLE);
            ani.start();
        }
        return super.onOptionsItemSelected(item);
    }// java inter face
    // handler 를 구현하지 않았더니 작동 x  꼭 써둘것!
    android.os.Handler han = new android.os.Handler();
    class JavaScriptMethod{
        @JavascriptInterface
        public void store(final String name, final String url){
            han.post(new Runnable() {
                @Override
                public void run() {
                    if(nurl.size() ==0){
                        nurl.add(url);
                        names.add("<"+name+">"+url);
                        Toast.makeText(getApplicationContext(),"저장되었습니다.",Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        boolean a=false ;
                        for(int i = 0;i<names.size();i++){
                            if(nurl.get(i).equals(url)){
                                a = true;
                            }
                        }
                        if(a){
                            //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                            web.loadUrl("javascript:setMsg(msg)");
                            web.loadUrl("javascript:displayMsg()");

                        }
                        else{
                            nurl.add(url);
                            names.add("<"+name+">"+url);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });

        }
        @JavascriptInterface
        public void visi(){
            han.post(new Runnable() {
                @Override
                public void run() {
                    ll.setVisibility(View.VISIBLE);
                }
            });
        }}
    public void clcl(View v){
        web.loadUrl(e1.getText().toString());
    }
}

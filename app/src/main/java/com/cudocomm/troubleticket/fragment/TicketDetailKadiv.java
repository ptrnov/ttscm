package com.cudocomm.troubleticket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cudocomm.troubleticket.R;

public class TicketDetailKadiv extends BaseFragment {

    private View rootView;

    WebView detailKadivWebView;
    ProgressBar detailLoader;

    public TicketDetailKadiv() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail_kadiv, container, false);
        detailKadivWebView = (WebView) rootView.findViewById(R.id.detailKadivWebView);
        detailLoader = (ProgressBar) rootView.findViewById(R.id.detailLoader);
/*

        detailKadivWebView.loadUrl("http://150.107.148.158/TT_SCM/mobilewebview/detail");

        // Enable Javascript
        WebSettings webSettings = detailKadivWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        detailKadivWebView.setWebViewClient(new WebViewClient());

*/

        // Custom User Agent
//        WebSettings settings =  detailKadivWebView.getSettings();
        // Open links in default browser and show offline message when needed
        detailKadivWebView.setWebViewClient(new WebViewClient());

        // Configure progress bar
        detailKadivWebView.setWebChromeClient(
                new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress < 100) {
                            detailLoader.setVisibility(View.VISIBLE);
                            detailLoader.setProgress(progress);
                        } else {
                            detailLoader.setVisibility(View.GONE);
                        }
                    }
                });

        // Go!
        detailKadivWebView.loadUrl("http://150.107.148.158/TT_SCM/mobilewebview/detail");


        return rootView;
    }

}

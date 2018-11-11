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

public class TicketChatKadiv extends BaseFragment {

    private View rootView;

    WebView chatKadivWebView;
    ProgressBar chatLoader;

    public TicketChatKadiv() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat_kadiv, container, false);
        chatKadivWebView = (WebView) rootView.findViewById(R.id.chatKadivWebView);
        chatLoader = (ProgressBar) rootView.findViewById(R.id.chatLoader);

        // Custom User Agent
//        WebSettings settings = chatKadivWebView.getSettings();
        // Open links in default browser and show offline message when needed
        chatKadivWebView.setWebViewClient(new WebViewClient());

        // Configure progress bar
        chatKadivWebView.setWebChromeClient(
                new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress < 100) {
                            chatLoader.setVisibility(View.VISIBLE);
                            chatLoader.setProgress(progress);
                        } else {
                            chatLoader.setVisibility(View.GONE);
                        }
                    }
                });

        // Go!
        chatKadivWebView.loadUrl("http://150.107.148.158/TT_SCM/mobilewebview/chat");


        return rootView;
    }

}

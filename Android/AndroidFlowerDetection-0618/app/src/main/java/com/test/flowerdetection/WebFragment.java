package com.test.flowerdetection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import static com.test.flowerdetection.ShowResult.link;

public class WebFragment extends Fragment {

    public static final WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle bdl = new Bundle();
        bdl.putString("URL", url);
        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        String url = getArguments().getString("URL");
        View v= inflater.inflate(R.layout.fragment_web, container, false);
        WebView web = v.findViewById(R.id.web_view);
        web.setWebViewClient(new WebViewClient());
        web.loadUrl(url);
        return v;
    }
}

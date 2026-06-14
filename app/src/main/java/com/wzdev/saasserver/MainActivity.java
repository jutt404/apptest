package com.wzdev.saasserver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import java.util.Locale;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 991;

    private WebView webView;
    private LinearLayout offlinePanel;
    private ProgressBar progressBar;
    private ValueCallback<Uri[]> filePathCallback;
    private String lastUrl = AppConfig.HOME_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(AppConfig.COLOR_BG);
        getWindow().setNavigationBarColor(AppConfig.COLOR_BG);
        buildInterface();
        setupWebView();

        if (savedInstanceState != null) {
            lastUrl = savedInstanceState.getString("lastUrl", AppConfig.HOME_URL);
        }
        openUrl(lastUrl);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null && webView.getUrl() != null) {
            outState.putString("lastUrl", webView.getUrl());
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebView.setWebContentsDebuggingEnabled(false);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setUserAgentString(settings.getUserAgentString() + " SaaSServerApp/1.0");

        webView.setWebViewClient(new SecureWebViewClient());
        webView.setWebChromeClient(new AppWebChromeClient());
    }

    private void buildInterface() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(AppConfig.COLOR_BG);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(14), dp(12), dp(14), dp(8));
        topBar.setBackgroundColor(AppConfig.COLOR_PANEL);
        root.addView(topBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(72)
        ));

        TextView title = new TextView(this);
        title.setText(AppConfig.APP_NAME);
        title.setTextColor(AppConfig.COLOR_TEXT);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        topBar.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        Button refreshButton = makeSmallButton("Refresh");
        refreshButton.setOnClickListener(v -> {
            if (isOnline()) {
                showWeb();
                webView.reload();
            } else {
                showOffline();
            }
        });
        topBar.addView(refreshButton);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        root.addView(progressBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(3)
        ));

        FrameLayout content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        webView = new WebView(this);
        content.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        offlinePanel = new LinearLayout(this);
        offlinePanel.setOrientation(LinearLayout.VERTICAL);
        offlinePanel.setGravity(Gravity.CENTER);
        offlinePanel.setPadding(dp(24), dp(24), dp(24), dp(24));
        offlinePanel.setBackgroundColor(AppConfig.COLOR_BG);
        offlinePanel.setVisibility(View.GONE);
        content.addView(offlinePanel, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        TextView offlineTitle = new TextView(this);
        offlineTitle.setText("Connection Problem");
        offlineTitle.setTextColor(AppConfig.COLOR_TEXT);
        offlineTitle.setTextSize(24);
        offlineTitle.setGravity(Gravity.CENTER);
        offlineTitle.setTypeface(Typeface.DEFAULT_BOLD);
        offlinePanel.addView(offlineTitle);

        TextView offlineMessage = new TextView(this);
        offlineMessage.setText("Please check internet connection and try again.");
        offlineMessage.setTextColor(AppConfig.COLOR_MUTED);
        offlineMessage.setTextSize(15);
        offlineMessage.setGravity(Gravity.CENTER);
        offlineMessage.setPadding(0, dp(10), 0, dp(20));
        offlinePanel.addView(offlineMessage);

        Button retryButton = makePrimaryButton("Try Again");
        retryButton.setOnClickListener(v -> openUrl(lastUrl));
        offlinePanel.addView(retryButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dp(48)
        ));

        LinearLayout bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.CENTER);
        bottomBar.setPadding(dp(10), dp(8), dp(10), dp(12));
        bottomBar.setBackgroundColor(AppConfig.COLOR_PANEL);
        root.addView(bottomBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(78)
        ));

        addNavButton(bottomBar, "Home", AppConfig.HOME_URL, false);
        addNavButton(bottomBar, "Orders", AppConfig.ORDERS_URL, false);
        addNavButton(bottomBar, "Support", AppConfig.SUPPORT_URL, true);
        if (AppConfig.SHOW_ADMIN_SHORTCUT) {
            addNavButton(bottomBar, "Admin", AppConfig.ADMIN_URL, false);
        }

        setContentView(root);
    }

    private void addNavButton(LinearLayout parent, String text, String url, boolean external) {
        Button button = makeSmallButton(text);
        button.setOnClickListener(v -> {
            if (external) openExternal(url);
            else openUrl(url);
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(48), 1);
        params.setMargins(dp(4), 0, dp(4), 0);
        parent.addView(button, params);
    }

    private Button makeSmallButton(String text) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(text);
        button.setTextColor(AppConfig.COLOR_TEXT);
        button.setTextSize(13);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackground(makeRounded(AppConfig.COLOR_BG, 1, 0xFF243044, 14));
        button.setPadding(dp(8), 0, dp(8), 0);
        return button;
    }

    private Button makePrimaryButton(String text) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackground(makeRounded(AppConfig.COLOR_ACCENT, 0, AppConfig.COLOR_ACCENT, 16));
        button.setPadding(dp(18), 0, dp(18), 0);
        return button;
    }

    private GradientDrawable makeRounded(int color, int strokeDp, int strokeColor, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) drawable.setStroke(dp(strokeDp), strokeColor);
        return drawable;
    }

    private void openUrl(String url) {
        lastUrl = url;
        if (!isOnline()) {
            showOffline();
            return;
        }
        Uri uri = Uri.parse(url);
        if (!isAllowedInside(uri)) {
            openExternal(url);
            return;
        }
        showWeb();
        webView.loadUrl(url);
    }

    private boolean isAllowedInside(Uri uri) {
        if (uri == null || uri.getScheme() == null) return false;
        String scheme = uri.getScheme().toLowerCase(Locale.US);
        if (!scheme.equals("https")) return false;
        String host = uri.getHost();
        if (host == null) return false;
        host = host.toLowerCase(Locale.US);
        for (String allowed : AppConfig.ALLOWED_HOSTS) {
            if (host.equals(allowed.toLowerCase(Locale.US))) return true;
        }
        return false;
    }

    private boolean isExternalScheme(Uri uri) {
        if (uri == null || uri.getScheme() == null) return false;
        String s = uri.getScheme().toLowerCase(Locale.US);
        return s.equals("tel") || s.equals("mailto") || s.equals("sms") || s.equals("whatsapp") || s.equals("intent");
    }

    private void openExternal(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            showMessage("No app found to open this link.");
        }
    }

    private void showWeb() {
        offlinePanel.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    private void showOffline() {
        webView.setVisibility(View.GONE);
        offlinePanel.setVisibility(View.VISIBLE);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return true;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle(AppConfig.APP_NAME)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST) {
            if (filePathCallback == null) return;
            Uri[] results = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
    }

    private class SecureWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return handleUrl(request.getUrl());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return handleUrl(Uri.parse(url));
        }

        private boolean handleUrl(Uri uri) {
            if (isExternalScheme(uri)) {
                openExternal(uri.toString());
                return true;
            }
            if (isAllowedInside(uri)) {
                lastUrl = uri.toString();
                return false;
            }
            openExternal(uri.toString());
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            CookieManager.getInstance().flush();
            if (url != null) lastUrl = url;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request == null || request.isForMainFrame()) showOffline();
        }
    }

    private class AppWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            progressBar.setProgress(newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (MainActivity.this.filePathCallback != null) {
                MainActivity.this.filePathCallback.onReceiveValue(null);
            }
            MainActivity.this.filePathCallback = filePathCallback;
            Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, FILE_CHOOSER_REQUEST);
            } catch (ActivityNotFoundException e) {
                MainActivity.this.filePathCallback = null;
                showMessage("File picker is not available on this device.");
                return false;
            }
            return true;
        }
    }
}

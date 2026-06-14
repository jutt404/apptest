package com.wzdev.saasserver;

public final class AppConfig {
    private AppConfig() {}

    public static final String APP_NAME = "SaaS Server";

    // Main user panel URL
    public static final String HOME_URL = "https://wzdev.qzz.io/index.php";

    // Optional quick links. Keep them on your own domain.
    public static final String ORDERS_URL = "https://wzdev.qzz.io/index.php#orders";
    public static final String SUPPORT_URL = "https://wa.me/923000000000";

    // Admin shortcut is hidden by default for customer app.
    public static final boolean SHOW_ADMIN_SHORTCUT = false;
    public static final String ADMIN_URL = "https://wzdev.qzz.io/ad-admin/";

    // Only these domains can load inside the app WebView.
    public static final String[] ALLOWED_HOSTS = new String[] {
            "wzdev.qzz.io"
    };

    // Brand colors
    public static final int COLOR_BG = 0xFF070B16;
    public static final int COLOR_PANEL = 0xFF0D1324;
    public static final int COLOR_ACCENT = 0xFF06B6D4;
    public static final int COLOR_ACCENT_TWO = 0xFF7C3AED;
    public static final int COLOR_TEXT = 0xFFFFFFFF;
    public static final int COLOR_MUTED = 0xFFA7B4C9;
}

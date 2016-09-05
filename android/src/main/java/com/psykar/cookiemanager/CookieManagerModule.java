package com.psykar.cookiemanager;

import android.webkit.CookieManager;

import com.facebook.react.modules.network.ForwardingCookieHandler;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CookieManagerModule extends ReactContextBaseJavaModule {

    private ForwardingCookieHandler cookieHandler;
    private static final String OPTIONS_NAME = "name";
    private static final String OPTIONS_VALUE = "value";
    private static final String OPTIONS_DOMAIN = "domain";
    private static final String OPTIONS_ORIGIN = "origin";
    private static final String OPTIONS_PATH = "path";
    private static final String OPTIONS_EXPIRATION = "expiration";

    public CookieManagerModule(ReactApplicationContext context) {
        super(context);
        this.cookieHandler = new ForwardingCookieHandler(context);
    }

    public String getName() {
        return "RNCookieManagerAndroid";
    }

    @ReactMethod
    public void set(ReadableMap options, final Callback callback) throws Exception {
        String name = null;
        String value = null;
        String domain = null;
        String origin = null;
        String path = null;
        String expiration = null;
        if (options.hasKey(OPTIONS_NAME)) {
            name = options.getString(OPTIONS_NAME);
        }
        if (options.hasKey(OPTIONS_VALUE)) {
            value = options.getString(OPTIONS_VALUE);
        }
        if (options.hasKey(OPTIONS_DOMAIN)) {
            domain = options.getString(OPTIONS_DOMAIN);
        }
        if (options.hasKey(OPTIONS_ORIGIN)) {
            origin = options.getString(OPTIONS_ORIGIN);
        }
        if (options.hasKey(OPTIONS_PATH)) {
            path = options.getString(OPTIONS_PATH);
        }
        if (options.hasKey(OPTIONS_EXPIRATION)) {
            expiration = options.getString(OPTIONS_EXPIRATION);
        }
        CookieManager.getInstance().setCookie(origin, name + "=" + value + ";"
                + OPTIONS_PATH + "=" + path + ";"
                + OPTIONS_EXPIRATION + "=" + expiration + ";"
                + OPTIONS_DOMAIN + "=" + domain);
    }

    @ReactMethod
    public void setFromResponse(String url, String value, final Callback callback) throws URISyntaxException, IOException {
        Map headers = new HashMap<String, List<String>>();
        // Pretend this is a header
        headers.put("Set-cookie", Collections.singletonList(value));
        URI uri = new URI(url);
        this.cookieHandler.put(uri, headers);
        callback.invoke(null, null);
    }

    @ReactMethod
    public void getAll(Callback callback) throws Exception {
        throw new Exception("Cannot get all cookies on android, try getCookieHeader(url)");
    }

    @ReactMethod
    public void get(String url, Callback callback) throws URISyntaxException, IOException {
        URI uri = new URI(url);

        Map<String, List<String>> cookieMap = this.cookieHandler.get(uri, new HashMap());
        // If only the variables were public
        List<String> cookieList = cookieMap.get("Cookie");
        WritableMap map = Arguments.createMap();
        if (cookieList != null) {
            String[] cookies = cookieList.get(0).split(";");
            for (int i = 0; i < cookies.length; i++) {
                String[] cookie = cookies[i].split("=");
                if(cookie.length > 1) {
                    map.putString(cookie[0].trim(), cookie[1]);
                }
            }
        }
        callback.invoke(null, map);
    }

    @ReactMethod
    public void clearAll(final Callback callback) {
        this.cookieHandler.clearCookies(new Callback() {
            public void invoke(Object... args) {
                callback.invoke(null, null);
            }
        });
    }
}

/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.HostnameVerifier;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import com.synconset.HttpRequest;

public class CordovaHttpPlugin extends CordovaPlugin {
    private static final String TAG = "CordovaHTTP";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("get")) {
            String urlString = args.getString(0);
            JSONObject params = args.getJSONObject(1);
            JSONObject headers = args.getJSONObject(2);
	        boolean cacheResults = args.getBoolean(3);
            CordovaHttp.setCacheResults(cacheResults);
            HashMap<?, ?> paramsMap = this.getMapFromJSONObject(params);
            HashMap<String, String> headersMap = this.getStringMapFromJSONObject(headers);
            CordovaHttpGet get = new CordovaHttpGet(urlString, paramsMap, headersMap, callbackContext);
            cordova.getThreadPool().execute(get);
        } else if (action.equals("post")) {
            String urlString = args.getString(0);
            JSONObject headers = args.getJSONObject(2);
            HashMap<String, String> headersMap = this.getStringMapFromJSONObject(headers);
            CordovaHttpPost post;
            try {
                JSONObject params = args.getJSONObject(1);
                HashMap<?, ?> paramsMap = this.getMapFromJSONObject(params);
                post = new CordovaHttpPost(urlString, paramsMap, headersMap, callbackContext);
            } catch (JSONException exception) {
                String paramsMap = args.getString(1);
                post = new CordovaHttpPost(urlString, paramsMap, headersMap, callbackContext);
            }
            cordova.getThreadPool().execute(post);
        } else {
            return false;
        }
        return true;
    }

    private void enableSSLPinning(boolean enable) throws GeneralSecurityException, IOException {
        if (enable) {
            AssetManager assetManager = cordova.getActivity().getAssets();
            String[] files = assetManager.list("");
            int index;
            ArrayList<String> cerFiles = new ArrayList<String>();
            for (int i = 0; i < files.length; i++) {
                index = files[i].lastIndexOf('.');
                if (index != -1) {
                    if (files[i].substring(index).equals(".cer")) {
                        cerFiles.add(files[i]);
                    }
                }
            }

            // scan the www/certificates folder for .cer files as well
            files = assetManager.list("www/certificates");
            for (int i = 0; i < files.length; i++) {
              index = files[i].lastIndexOf('.');
              if (index != -1) {
                if (files[i].substring(index).equals(".cer")) {
                  cerFiles.add("www/certificates/" + files[i]);
                }
              }
            }

            for (int i = 0; i < cerFiles.size(); i++) {
                InputStream in = cordova.getActivity().getAssets().open(cerFiles.get(i));
                InputStream caInput = new BufferedInputStream(in);
                HttpRequest.addCert(caInput);
            }
            CordovaHttp.enableSSLPinning(true);
        } else {
            CordovaHttp.enableSSLPinning(false);
        }
    }

    private HashMap<String, String> getStringMapFromJSONObject(JSONObject object) throws JSONException {
        HashMap<String, String> map = new HashMap<String, String>();
        Iterator<?> i = object.keys();

        while (i.hasNext()) {
            String key = (String)i.next();
            map.put(key, object.getString(key));
        }
        return map;
    }

    private HashMap<String, Object> getMapFromJSONObject(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<?> i = object.keys();

        while(i.hasNext()) {
            String key = (String)i.next();
            map.put(key, object.get(key));
        }
        return map;
    }
}

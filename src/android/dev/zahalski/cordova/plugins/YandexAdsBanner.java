package dev.zahalski.cordova.plugins;

import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

public class YandexAdsBanner extends CordovaPlugin {

    private static final String LOG_TAG = "YANDEX_ADS";

    private static final String EVENT_BANNER_LOADED = "bannerLoaded";
    private static final String EVENT_BANNER_START_LOAD = "bannerStartLoad";
    private static final String EVENT_BANNER_FAILED_TO_LOAD = "bannerFailed";
    private static final String EVENT_BANNER_SHOWN = "bannerShow";
    private static final String EVENT_BANNER_START_SHOWN = "bannerStartShow";
    private static final String EVENT_BANNER_CLOSED = "bannerClose";
    private static final String EVENT_BANNER_OPEN = "bannerOutApp";
    private static final String EVENT_BANNER_RETURN_APP = "bannerReturnApp";
    private static final String EVENT_INIT = "init";

    private static final String ACTION_INIT = "init";
    private static final String ACTION_LOAD = "load";
    private static final String ACTION_SHOW = "show";

    private static String blockId;

    private InterstitialAd banner;
    private CordovaWebView cordovaWebView;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        cordovaWebView = webView;
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        try {
            if (action.equals(ACTION_INIT)) {
                this.initAction(args, callbackContext);
                return true;
            }else if (action.equals(ACTION_LOAD)) {
                this.loadAction(args, callbackContext);
                return true;
            }else if (action.equals(ACTION_SHOW)) {
                this.showAction(args, callbackContext);
                return true;
            }
            return false;
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
    }

    private void initAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        final YandexAdsBanner self = this;
        MobileAds.initialize(this.cordova.getActivity(), new InitializationListener() {
            @Override
            public void onInitializationCompleted() {
                Log.d(LOG_TAG, EVENT_INIT);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                self.emitWindowEvent(EVENT_INIT, callbackContext);
            }
        });
    }

    private void loadAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        final YandexAdsBanner self = this;

        blockId = args.getString(0);

        banner = new InterstitialAd(this.cordova.getActivity());
        banner.setBlockId(blockId);

        Log.d(LOG_TAG, EVENT_BANNER_START_LOAD + ", blockId: "+blockId);

        banner.setInterstitialAdEventListener(new InterstitialAdEventListener() {
            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, EVENT_BANNER_LOADED);
                self.emitWindowEvent(EVENT_BANNER_LOADED, callbackContext);
            }

            @Override
            public void onAdFailedToLoad(AdRequestError adRequestError) {
                Log.d(LOG_TAG, EVENT_BANNER_FAILED_TO_LOAD + ": " +adRequestError.getCode() + ":" + adRequestError.getDescription());
                self.emitWindowEventError(EVENT_BANNER_FAILED_TO_LOAD, callbackContext, adRequestError.getDescription());
            }

            @Override
            public void onAdShown() {
                Log.d(LOG_TAG, EVENT_BANNER_SHOWN);
                self.emitWindowEvent(EVENT_BANNER_SHOWN, callbackContext);
            }

            @Override
            public void onAdDismissed() {
                Log.d(LOG_TAG, EVENT_BANNER_CLOSED);
                self.emitWindowEvent(EVENT_BANNER_CLOSED, callbackContext);
            }

            @Override
            public void onLeftApplication() {
                Log.d(LOG_TAG, EVENT_BANNER_OPEN);
                self.emitWindowEvent(EVENT_BANNER_OPEN, callbackContext);
            }

            @Override
            public void onReturnedToApplication() {
                Log.d(LOG_TAG, EVENT_BANNER_RETURN_APP);
                self.emitWindowEvent(EVENT_BANNER_RETURN_APP, callbackContext);
            }
        });

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final AdRequest adRequest = new AdRequest.Builder().build();
                banner.loadAd(adRequest);
                self.emitWindowEvent(EVENT_BANNER_START_LOAD, callbackContext);
            }
        });

    }

    private void showAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        final YandexAdsBanner self = this;
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Log.d(LOG_TAG, EVENT_BANNER_START_SHOWN);
                banner.show();
                self.emitWindowEvent(EVENT_BANNER_START_SHOWN, callbackContext);
            }
        });

    }

    private void emitWindowEvent(final String event, final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject JSONResult = new JSONObject();
                    JSONResult.put("ok", event);

                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, JSONResult));
                    webView.loadUrl(String.format("javascript:cordova.fireWindowEvent('YandexAds:%s');", event));

                }catch (JSONException jsonEx){
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION, event));
                    webView.loadUrl(String.format("javascript:cordova.fireWindowEvent('YandexAds:%s','%s');", event+"Error", PluginResult.Status.JSON_EXCEPTION));
                }
            }
        });
    }

    private void emitWindowEventError(final String event, final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, event));
                webView.loadUrl(String.format("javascript:cordova.fireWindowEvent('YandexAds:%s');", event));
            }
        });
    }
    private void emitWindowEventError(final String event, final CallbackContext callbackContext, final String errorText) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, event));
                webView.loadUrl(String.format("javascript:cordova.fireWindowEvent('YandexAds:%s','%s');", event, errorText));
            }
        });
    }

}
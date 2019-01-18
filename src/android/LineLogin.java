package plugin.line;

// import android.content.Context;
// import android.content.Intent;
// import android.util.Log;

// import com.linecorp.linesdk.LineAccessToken;
// import com.linecorp.linesdk.LineApiResponse;
// import com.linecorp.linesdk.LineApiResponseCode;
// import com.linecorp.linesdk.LineProfile;
// import com.linecorp.linesdk.api.LineApiClient;
// import com.linecorp.linesdk.api.LineApiClientBuilder;
// import com.linecorp.linesdk.auth.LineLoginApi;
// import com.linecorp.linesdk.auth.LineLoginResult;

// import org.apache.cordova.*;
// import org.json.JSONArray;
// import org.json.JSONException;
// import org.json.JSONObject;

import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;

import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LineLogin extends CordovaPlugin {

    private static final int REQUEST_CODE = 1;
    String channelId;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        Log.d("", "################# プラグイン with Native");

        if (action.equals("initialize")) {
            JSONObject params = data.getJSONObject(0);
            channelId = params.get("channel_id").toString();
            return true;
        } else if (action.equals("login")) {
            try {
                Intent loginIntent = LineLoginApi.getLoginIntent(
                        this.cordova.getActivity().getApplicationContext(),
                        channelId,
                        new LineAuthenticationParams.Builder()
                                .scopes(Arrays.asList(Scope.PROFILE))
                                .build());
                this.cordova.startActivityForResult((CordovaPlugin) this, loginIntent, REQUEST_CODE);
            } catch (Exception e) {
                Log.e("ERROR", e.toString());
            }
            return true;
        } else {
            return false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE) {
            Log.e("ERROR", "Unsupported Request");
            return;
        }

        LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);

        switch (result.getResponseCode()) {

            case SUCCESS:
                // Login successful
                String accessToken = result.getLineCredential().getAccessToken().getTokenString();

                break;

            case CANCEL:
                // Login canceled by user
                Log.e("ERROR", "LINE Login Canceled by user.");
                break;

            default:
                // Login canceled due to other error
                Log.e("ERROR", "Login FAILED!");
                Log.e("ERROR", result.getErrorData().toString());
        }
    }}

// public class LineLogin extends CordovaPlugin {

//     String channelId;
//     CallbackContext callbackContext;
//     private static LineApiClient lineApiClient;

//     @Override
//     public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

//         if (action.equals("initialize")) {
//             JSONObject params = data.getJSONObject(0);
//             channelId = params.get("channel_id").toString();

//             LineApiClientBuilder apiClientBuilder = new LineApiClientBuilder(this.cordova.getActivity().getApplicationContext(), channelId);
//             lineApiClient = apiClientBuilder.build();

//             return true;
//         } else if (action.equals("login")) {
//             Context context = this.cordova.getActivity().getApplicationContext();
//             Intent loginIntent = LineLoginApi.getLoginIntent(context, channelId);
//             this.cordova.startActivityForResult((CordovaPlugin) this, loginIntent, 0);
//             this.callbackContext = callbackContext;
//             return true;
//         } else if (action.equals("logout")) {
//             try {
//                 lineApiClient.logout();
//                 callbackContext.success();
//             } catch (Exception e) {
//                 callbackContext.error(-1);
//             }
//             return true;
//         } else if (action.equals("getAccessToken")) {
//             JSONObject json = new JSONObject();
//             LineAccessToken lineAccessToken = lineApiClient.getCurrentAccessToken().getResponseData();
//             try {
//                 json.put("accessToken", lineAccessToken.getAccessToken());
//                 json.put("expireTime", lineAccessToken.getEstimatedExpirationTimeMillis());
//                 callbackContext.success(json);
//             } catch (JSONException e) {
//                 callbackContext.error(-1);
//             }
//             return true;
//         } else if (action.equals("verifyAccessToken")) {
//             LineApiResponse verifyResponse = lineApiClient.verifyToken();
//             if (verifyResponse.isSuccess()) {
//                 callbackContext.success();
//             } else {
//                 callbackContext.error(-1);
//             }
//             return true;
//         } else if (action.equals("refreshAccessToken")) {
//             try {
//                 lineApiClient.refreshAccessToken();
//                 String accessToken = lineApiClient.getCurrentAccessToken().getResponseData().getAccessToken();
//                 callbackContext.success(accessToken);
//             } catch (Exception e) {
//                 callbackContext.error(-1);
//             }
//             return true;
//         } else {
//             return false;
//         }
//     }

//     public void onActivityResult(int requestCode, int resultCode, Intent data) {
//         super.onActivityResult(requestCode, resultCode, data);

//         LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
//         JSONObject json = new JSONObject();
//         if (result.getResponseCode() == LineApiResponseCode.SUCCESS) {
//             LineProfile profile = result.getLineProfile();
//             try {
//                 json.put("userID", profile.getUserId());
//                 json.put("displayName", profile.getDisplayName());
//                 json.put("pictureURL", profile.getPictureUrl());
//                 callbackContext.success(json);
//             } catch (JSONException e) {
//                 callbackContext.error(-1);
//             }
//         } else {
//             callbackContext.error(-1);
//         }
//     }
// }

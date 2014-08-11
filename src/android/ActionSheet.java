package se.considerate.plugins.actionsheet;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ActionSheet extends CordovaPlugin {
    private static final String TAG = "ActionSheet";

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        if ("show".equals(action)) {
            try {
                JSONObject options = args.getJSONObject(0);
                JSONArray buttons = options.getJSONArray("buttonLabels");
                String title = options.optString("title");
                String deleteButton = options.optString("addDestructiveButtonWithLabel");
                this.show(title, buttons, deleteButton, callbackContext);
                return true;
            } catch(JSONException e) {
                Log.e(TAG, e.toString());
                callbackContext.error(e.toString());
            }
            return true;
        }
        return false;
    }

    private void show(final String title, final JSONArray buttons, final String deleteButton, final CallbackContext callbackContext) {
        final CordovaInterface cordova = this.cordova;
        cordova.getActivity().runOnUiThread(new Runnable() { 
            public void run() {
                List<String> list = new ArrayList<String>();
                int len = buttons.length();
                for (int i=0;i<len;i++){ 
                    try {
                        String button = buttons.getString(i);
                        list.add(button);
                    } catch(JSONException e) {
                        Log.e(TAG, e.toString());
                        callbackContext.error(e.toString());
                    }
                }
                CharSequence[] buttonLabels = list.toArray(new CharSequence[list.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
                if(title != null) {
                    builder.setTitle(title);
                }
                if(deleteButton != null) {
                    builder.setNegativeButton(deleteButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            callbackContext.success(0);
                        }
                    });
                }

                builder
                .setItems(buttonLabels, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Return 1-based index
                        callbackContext.success(which+1);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
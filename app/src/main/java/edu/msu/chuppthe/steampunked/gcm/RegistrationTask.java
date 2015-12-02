package edu.msu.chuppthe.steampunked.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.msu.chuppthe.steampunked.gcm.registration.Registration;
import edu.msu.chuppthe.steampunked.utility.Cloud;
import edu.msu.chuppthe.steampunked.utility.Preferences;

public class RegistrationTask extends AsyncTask<Void, Void, String> {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    private static final String SENDER_ID = "1002849100494";

    private Preferences preferences;
    private Cloud cloud;

    public RegistrationTask(Context context) {
        this.context = context;
        this.preferences = new Preferences(context);
        this.cloud = new Cloud(context);
    }

    @Override
    protected String doInBackground(Void... params) {
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://steampunked-c3ac2.appspot.com/_ah/api/");

            regService = builder.build();
        }

        String msg;
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String deviceToken = gcm.register(SENDER_ID);
            msg = deviceToken;

            regService.register(deviceToken).execute();

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String token) {

        if (!preferences.getDeviceToken().equals(token)) {
            preferences.setDeviceToken(token);
            //TODO: BROKEN
            //cloud.registerDeviceToCloud(token);
        }

        Log.i("DEVICE TOKEN", token);
    }
}
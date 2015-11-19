package edu.msu.chuppthe.steampunked;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Cloud {
    private static final String MAGIC = "TechItHa$RuzeM8";
    private static final String LOGIN_URL = "https://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-login.php";
    private static final String REGISTER_URL = "https://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-register.php";
    private static final String UTF8 = "UTF-8";

    /**
     * Skip the XML parser to the end tag for whatever
     * tag we are currently within.
     *
     * @param xml the parser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static void skipToEndTag(XmlPullParser xml) throws IOException, XmlPullParserException {
        int tag;
        do {
            tag = xml.next();
            if (tag == XmlPullParser.START_TAG) {
                // Recurse over any start tag
                skipToEndTag(xml);
            }
        } while (tag != XmlPullParser.END_TAG &&
                tag != XmlPullParser.END_DOCUMENT);
    }

    /**
     * Login to the cloud.
     * This should run in a thread
     *
     * @param username username to login as
     * @param password password to login with
     * @return true if login is successful
     */
    public boolean loginToCloud(String username, String password) {
        String query = LOGIN_URL + "?user=" + username + "&pw=" + password + "&magic=" + MAGIC;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            logStream(stream);

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xmlR = Xml.newPullParser();
                xmlR.setInput(stream, UTF8);

                xmlR.nextTag();      // Advance to first tag
                xmlR.require(XmlPullParser.START_TAG, null, "steam");

                String status = xmlR.getAttributeValue(null, "status");
                if (status.equals("no")) {
                    return false;
                }
            } catch (XmlPullParserException e) {
                return false;
            }
        } catch (MalformedURLException e) {
            // Should never happen
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    /**
     * Register user to the cloud.
     * This should run in a thread
     *
     * @param username username to register
     * @param password password to register
     * @return true if register is successful
     */
    public boolean registerToCloud(String username, String password) {
        String query = REGISTER_URL + "?user=" + username + "&pw=" + password + "&magic=" + MAGIC;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            logStream(stream);

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xmlR = Xml.newPullParser();
                xmlR.setInput(stream, UTF8);

                xmlR.nextTag();      // Advance to first tag
                xmlR.require(XmlPullParser.START_TAG, null, "steam");

                String status = xmlR.getAttributeValue(null, "status");
                if (status.equals("no")) {
                    return false;
                }
            } catch (XmlPullParserException e) {
                return false;
            }
        } catch (MalformedURLException e) {
            // Should never happen
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public static void logStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));

        Log.e("Steampunked", "logStream: If you leave this in, code after will not work!");
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.e("Steampunked", line);
            }
        } catch (IOException ignored) {
        }
    }
}

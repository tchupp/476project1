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
    public static final int FAIL_GAME_ID = -1;

    private static final String MAGIC = "TechItHa6RuzeM8";
    private static final String LOGIN_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-login.php";
    private static final String REGISTER_USER_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-register-user.php";
    private static final String REGISTER_DEVICE_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-register-device.php";
    private static final String CREATE_GAME_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-create-game.php";
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
    public String loginToCloud(String username, String password) {
        String authToken = null;
        String query = LOGIN_URL + "?user=" + username + "&pw=" + password + "&magic=" + MAGIC;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            stream = conn.getInputStream();

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
                    return null;
                }

                authToken = xmlR.getAttributeValue(null, "auth");
            } catch (XmlPullParserException e) {
                return null;
            }
        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return authToken;
    }

    /**
     * Register user to the cloud.
     * This should run in a thread
     *
     * @param username username to register
     * @param password password to register
     * @return true if register is successful
     */
    public boolean registerUserToCloud(String username, String password) {
        String query = REGISTER_USER_URL + "?user=" + username + "&pw=" + password + "&magic=" + MAGIC;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

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
     * Register a device to the cloud.
     * This should be run in a thread
     *
     * @param username    user to register for
     * @param deviceToken device token to register
     * @return true if register is successful
     */
    public boolean registerDeviceToCloud(String username, String deviceToken) {
        //TODO: Add auth token to the request header
        String query = REGISTER_DEVICE_URL + "?user=" + username + "&device=" + deviceToken;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

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
     * Create a new game on the cloud.
     * This should be run in a thread
     *
     * @param username user creating the game
     * @param name     name of the game
     * @param gridSize size of the playing area
     * @return the id of the game
     */
    public int createGameOnCloud(String username, String name, int gridSize) {
        int gameId = -1;
        //TODO: Add auth token to the request header
        String query = CREATE_GAME_URL + "?user=" + username + "&name=" + name + "&grid=" + gridSize;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return -1;
            }

            stream = conn.getInputStream();

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
                    return -1;
                }

                gameId = Integer.parseInt(xmlR.getAttributeValue(null, "game"));
            } catch (XmlPullParserException e) {
                return -1;
            }
        } catch (MalformedURLException e) {
            // Should never happen
            return -1;
        } catch (IOException ex) {
            return -1;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return gameId;
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

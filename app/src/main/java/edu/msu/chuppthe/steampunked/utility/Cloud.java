package edu.msu.chuppthe.steampunked.utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.game.GameInfo;
import edu.msu.chuppthe.steampunked.game.Pipe;

public class Cloud {
    private static final String MAGIC = "TechItHa6RuzeM8";

    private static final String GAME_CATALOG_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-catalog.php";
    private static final String GAME_CREATE_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-create.php";
    private static final String GAME_INFO_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-info.php";
    private static final String GAME_JOIN_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-join.php";
    private static final String LOAD_PIPE_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-load.php";
    private static final String SAVE_PIPE_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-save.php";
    private static final String LOGIN_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-login.php";
    private static final String REGISTER_DEVICE_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-register-device.php";
    private static final String REGISTER_USER_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-register-user.php";
    private static final String DISCARD_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-discard.php";

    private static final String UTF8 = "UTF-8";
    private static final String AUTH_USER_FIELD = "AuthUser";
    private static final String AUTH_TOKEN_FIELD = "AuthToken";

    private Context context;

    /**
     * Nested class to store one catalog row
     */
    private static class Item {
        public String grid = "";
        public String creator = "";
        public String name = "";
        public String id = "";
    }

    public static final int FAIL_GAME_ID = -1;

    public static class CatalogAdapter extends BaseAdapter {
        /**
         * The items we display in the list box. Initially this is
         * empty until we get items from the server.
         */
        private List<Item> items = new ArrayList<>();

        private Preferences preferences;

        private View view;

        public CatalogAdapter(final View view, Activity activity) {
            this.view = view;
            this.preferences = new Preferences(view.getContext());

            update(activity);
        }

        public void update(Activity activity) {
            //TODO: add loading catalog dlg here

            // Create a thread to load the catalog
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Item> newItems = getCatalog();
                    if (newItems != null) {
                        items = newItems;

                        view.post(new Runnable() {

                            @Override
                            public void run() {
                                // Tell the adapter the data set has been changed
                                notifyDataSetChanged();
                            }
                        });
                    } else {
                        // Error condition!
                        view.post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), R.string.catalog_fail, Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                }
            }).start();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_game, parent, false);
            }

            TextView nameView = (TextView) view.findViewById(R.id.gameName);
            nameView.setText(items.get(position).name);

            TextView userView = (TextView) view.findViewById(R.id.gameCreator);
            userView.setText(items.get(position).creator);

            TextView gridSizeView = (TextView) view.findViewById(R.id.gridSize);
            gridSizeView.setText(getGridSize(position));

            return view;
        }

        private String getGridSize(int position) {
            String grid = items.get(position).grid;
            switch (grid) {
                case "10":
                    return "10 x 10";
                case "20":
                    return "20 x 20";
                default:
                    return "5 x 5";
            }
        }

        public List<Item> getCatalog() {
            String query = GAME_CATALOG_URL;

            ArrayList<Item> newItems = new ArrayList<>();

            /**
             * Open the connection
             */
            InputStream stream;
            try {
                URL url = new URL(query);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                addAuthHeader(preferences, conn);

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                stream = conn.getInputStream();

                /**
                 * Create an XML parser for the result
                 */
                try {
                    XmlPullParser xml = Xml.newPullParser();
                    xml.setInput(stream, "UTF-8");

                    xml.nextTag();      // Advance to first tag
                    xml.require(XmlPullParser.START_TAG, null, "steam");

                    String status = xml.getAttributeValue(null, "status");
                    if (status.equals("no")) {
                        return null;
                    }

                    while (xml.nextTag() == XmlPullParser.START_TAG) {
                        if (xml.getName().equals("game")) {
                            Item item = new Item();

                            item.grid = xml.getAttributeValue(null, "grid");
                            item.creator = xml.getAttributeValue(null, "creator");
                            item.name = xml.getAttributeValue(null, "name");
                            item.id = xml.getAttributeValue(null, "id");

                            newItems.add(item);
                        }
                        skipToEndTag(xml);
                    }

                    // We are done
                } catch (XmlPullParserException ex) {
                    return null;
                } catch (IOException ex) {
                    return null;
                } finally {
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                }
            } catch (MalformedURLException e) {
                // Should never happen
                return null;
            } catch (IOException ex) {
                return null;
            }

            return newItems;
        }

        public String getId(int position) {
            return items.get(position).id;
        }

        public String getCreator(int position) { return items.get(position).creator; }
    }

    private Preferences preferences;

    public Cloud(Context context) {
        this.context = context;
        preferences = new Preferences(context);
    }

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
     * @param deviceToken device token to register
     * @return true if register is successful
     */
    public boolean registerDeviceToCloud(String deviceToken) {
        String query = REGISTER_DEVICE_URL + "?device=" + deviceToken;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addAuthHeader(preferences, conn);

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
     * Join player two to game
     * This should be run in a thread
     *
     * @param gameId device token to register
     * @return true if register is successful
     */
    public boolean addPlayerTwoToGame(String gameId) {
        String query = GAME_JOIN_URL + "?game=" + gameId;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addAuthHeader(preferences, conn);

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
     * Join player two to game
     * This should be run in a thread
     *
     * @param gameId device token to register
     * @return true if register is successful
     */
    public boolean discardPipeFromCloud(String gameId) {
        String query = DISCARD_URL + "?game=" + gameId;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addAuthHeader(preferences, conn);

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
     * @param name     name of the game
     * @param gridSize size of the playing area
     * @return the id of the game
     */
    public int createGameOnCloud(String name, int gridSize) {
        String query = GAME_CREATE_URL + "?name=" + name + "&grid=" + gridSize;

        int gameId = -1;
        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addAuthHeader(preferences, conn);

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

    /**
     * Saves a pipe to the cloud
     * This should run in a thread!!
     *
     * @param gameId the game id to save under
     * @param pipe   the pipe to save
     * @return true if the save was successful
     */
    public String savePipeToCloud(String gameId, Pipe pipe) {
        String pipeId = null;

        gameId = gameId.trim();
        if (gameId.length() == 0) {
            return null;
        }

        // Create an XML packet with the information about the current image
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startTag(null, "pipe");

            pipe.savePipeXml(gameId, xml);

            xml.endTag(null, "pipe");

            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return null;
        }

        final String xmlStr = writer.toString();

        /*
         * Convert the XML into HTTP POST data
         */
        String postDataStr;
        try {
            postDataStr = "xml=" + URLEncoder.encode(xmlStr, UTF8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        /*
         * Send the data to the server
         */
        byte[] postData = postDataStr.getBytes();

        String query = SAVE_PIPE_URL + "?game=" + gameId;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addAuthHeader(preferences, conn);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            OutputStream out = conn.getOutputStream();
            out.write(postData);
            out.close();

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

                pipeId = xmlR.getAttributeValue(null, "pipe");

                // We are done
            } catch (XmlPullParserException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
        } catch (MalformedURLException e) {
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

        return pipeId;
    }

    public Pipe loadPipeFromCloud(String gameId, String pipeId) {
        String query = LOAD_PIPE_URL + "?game=" + gameId + "&pipe=" + pipeId;

        Pipe pipe = null;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addAuthHeader(preferences, conn);

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
                xmlR.nextTag();
                xmlR.require(XmlPullParser.START_TAG, null, "pipe");

                pipe = Pipe.createPipefromXml(xmlR, context);

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

        return pipe;
    }

    public GameInfo getGameInfoFromCloud(int gameId) {
        String query = GAME_INFO_URL + "?game=" + gameId;

        GameInfo gameInfo = null;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addAuthHeader(preferences, conn);

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

                String name = xmlR.getAttributeValue(null, "name");
                String playerOne = xmlR.getAttributeValue(null, "creating");
                String playerTwo = xmlR.getAttributeValue(null, "joining");
                int gridSize = Integer.parseInt(xmlR.getAttributeValue(null, "grid"));

                gameInfo = new GameInfo(name, playerOne, playerTwo, gridSize);
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

        return gameInfo;
    }

    private static void addAuthHeader(Preferences preferences, HttpURLConnection connection) {
        connection.setRequestProperty(AUTH_USER_FIELD, preferences.getAuthUsername());
        connection.setRequestProperty(AUTH_TOKEN_FIELD, preferences.getAuthToken());
    }
}

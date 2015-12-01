package edu.msu.chuppthe.steampunked;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Cloud {
    private static final String MAGIC = "TechItHa6RuzeM8";
    private static final String LOGIN_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-login.php";
    private static final String REGISTER_USER_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-register-user.php";
    private static final String REGISTER_DEVICE_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-register-device.php";
    private static final String CREATE_GAME_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-create-game.php";
    private static final String CATALOG_URL = "http://webdev.cse.msu.edu/~chuppthe/cse476/steampunked/steam-game-catalog.php";
    private static final String UTF8 = "UTF-8";

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

        public CatalogAdapter(final View view) {

            // Create a thread to load the catalog
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Item> newItems = getCatalog("placeholderUsername");
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

        private List<Item> getCatalog(String username) {
            //TODO: Add auth token to the request header

            String query = CATALOG_URL + "?user=" + username;

            ArrayList<Item> newItems = new ArrayList<>();

            /**
             * Open the connection
             */
            InputStream stream;
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


    //TODO: DELETE THIS
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

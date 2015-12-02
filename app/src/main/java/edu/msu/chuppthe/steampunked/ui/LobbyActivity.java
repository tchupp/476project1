package edu.msu.chuppthe.steampunked.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.utility.Cloud;

public class LobbyActivity extends AppCompatActivity {

    private Cloud.CatalogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Find the list view
        ListView list = (ListView) findViewById(R.id.gameList);


        adapter = new Cloud.CatalogAdapter(list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the id of the one we want to delete
                String gameId = adapter.getId(position);

                //TODO: Load the game with its id
            }
        });
    }

    public void onCreateGame(View view) {
        CreateGameDlg createGameDlg = new CreateGameDlg();
        createGameDlg.show(getFragmentManager(), "create_game");
    }

    public void update() {
        adapter.update();
    }
}
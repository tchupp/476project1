package edu.msu.chuppthe.steampunked.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

                //TODO: Send toast notification to game creator
                //TODO: Add player to game in DB
                moveToGame(gameId);
            }
        });
    }

    public void onCreateGame(View view) {
        if (adapter.isEmpty()) {
            CreateGameDlg createGameDlg = new CreateGameDlg();
            createGameDlg.show(getFragmentManager(), "create_game");
        }
        else {
            Toast.makeText(view.getContext(), R.string.game_created_warning, Toast.LENGTH_SHORT).show();
        }
    }

    public void update() { adapter.update(); }

    public void moveToGame(String gameId) {
        Intent intent = new Intent(this, GameLiveActivity.class);

        //TODO: Get game details from DB and add to extras
        // Temp extras for testing
        Bundle extras = new Bundle();
        extras.putString(GameLiveActivity.GAME_ID, gameId);
        extras.putString(GameLiveActivity.PLAYER_ONE_NAME, "P1");
        //extras.putString(GameLiveActivity.PLAYER_TWO_NAME, "P2");
        extras.putInt(GameLiveActivity.GRID_SIZE, 10);

        intent.putExtras(extras);

        startActivity(intent);
    }
}

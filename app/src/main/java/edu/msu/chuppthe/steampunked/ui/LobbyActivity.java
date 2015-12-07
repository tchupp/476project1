package edu.msu.chuppthe.steampunked.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.game.GameInfo;
import edu.msu.chuppthe.steampunked.utility.Cloud;

public class LobbyActivity extends AppCompatActivity {

    private Cloud.CatalogAdapter adapter;
    private Cloud cloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Find the list view
        ListView list = (ListView) findViewById(R.id.gameList);

        cloud = new Cloud(this);

        adapter = new Cloud.CatalogAdapter(list, this);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the id of the one we want to delete
                final String gameId = adapter.getId(position);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (cloud.addPlayerTwoToGame(gameId)) {
                            moveToGame(Integer.parseInt(gameId));
                        }
                        else {
                            Toast.makeText(getBaseContext(), "Failed to join game", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });
    }

    public void onCreateGame(View view) {
        if (adapter.isEmpty()) {
            CreateGameDlg createGameDlg = new CreateGameDlg();
            createGameDlg.show(getFragmentManager(), "create_game");
        } else {
            Toast.makeText(view.getContext(), R.string.game_created_warning, Toast.LENGTH_SHORT).show();
        }
    }

    public void update() {
        adapter.update(this);
    }

    public void moveToGame(final int gameId) {

        final LobbyActivity context = this;

        final GameLoadingDlg gameLoadingDlg = new GameLoadingDlg();
        gameLoadingDlg.show(context.getFragmentManager(), "game_loading");

        new Thread(new Runnable() {
            @Override
            public void run() {


                GameInfo gameInfo = cloud.getGameInfoFromCloud(gameId);
                if (gameInfo != null) {
                    Intent intent = new Intent(context, GameLiveActivity.class);

                    // Temp extras for testing
                    Bundle extras = new Bundle();
                    extras.putInt(GameLiveActivity.GAME_ID, gameId);
                    extras.putString(GameLiveActivity.PLAYER_ONE_NAME, gameInfo.getPlayerOneName());
                    extras.putString(GameLiveActivity.PLAYER_TWO_NAME, gameInfo.getPlayerTwoName());

                    int gridSize = 1;
                    switch (gameInfo.getGridSize()) {
                        case 10:
                            gridSize = 2;
                            break;
                        case 20:
                            gridSize = 4;
                            break;
                        default:
                            break;
                    }
                    extras.putInt(GameLiveActivity.GRID_SIZE, gridSize);

                    intent.putExtras(extras);

                    startActivity(intent);
                } else {
                    // this really shouldn't happen
                    Toast.makeText(context, "Unable to find game", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }
}

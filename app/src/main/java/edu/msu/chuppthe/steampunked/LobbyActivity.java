package edu.msu.chuppthe.steampunked;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
    }

    public void onCreateGame(View view) {
        CreateGameDlg createGameDlg = new CreateGameDlg();
        createGameDlg.show(getFragmentManager(), "create_game");
    }
}

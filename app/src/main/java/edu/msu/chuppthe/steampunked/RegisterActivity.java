package edu.msu.chuppthe.steampunked;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by evanhlavaty on 11/11/15.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onRegister(View view) {


        //TODO: PUSH DATA TO DB


        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

}

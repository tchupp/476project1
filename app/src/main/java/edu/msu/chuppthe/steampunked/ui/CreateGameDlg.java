package edu.msu.chuppthe.steampunked.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.utility.Cloud;

public class CreateGameDlg extends DialogFragment {

    private AlertDialog dlg;

    private int spinnerPos;

    /**
     * Create the dialog box
     *
     * @param savedInstanceState The saved instance bundle
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.create_game);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.create_game_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel just closes the dialog box
            }
        });

        // Add the created game
        builder.setPositiveButton(R.string.create_game, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String gameName = getGameName();

                if (gameName.isEmpty()) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), R.string.create_game_fail,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    createGame(gameName, getGridSize());
                }
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.grid_sizes, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        Spinner gridSpinner = (Spinner) view.findViewById(R.id.gridSpinner);
        gridSpinner.setAdapter(adapter);

        gridSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
                spinnerPos = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        dlg = builder.create();

        return dlg;
    }

    private void createGame(final String gameName, final int gridSize) {
        if (!(getActivity() instanceof LobbyActivity)) {
            return;
        }

        final GameCreateLoadingDlg gameCreateLoadingDlg  = new GameCreateLoadingDlg();
        gameCreateLoadingDlg.show(getActivity().getFragmentManager(), "create_loading");

        final LobbyActivity activity = (LobbyActivity) getActivity();
        final ListView view = (ListView) activity.findViewById(R.id.gameList);


        Runnable createGameRunnable = new Runnable() {
            @Override
            public void run() {
                // Create a cloud object
                Cloud cloud = new Cloud(view.getContext());
                int gameId = cloud.createGameOnCloud(gameName, gridSize);
                if (gameId == Cloud.FAIL_GAME_ID) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            // If we fail to create a game , display a toast
                            Toast.makeText(view.getContext(),
                                    R.string.create_game_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
                    activity.update();
                } else {
                    activity.moveToGame(Integer.toString(gameId));
                }

                gameCreateLoadingDlg.dismiss();
            }
        };



        new Thread(createGameRunnable).start();
    }

    private String getGameName() {
        EditText gameNameEdit = (EditText) dlg.findViewById(R.id.gameNameText);
        return gameNameEdit.getText().toString();
    }

    private int getGridSize() {
        switch (spinnerPos) {
            case 1:
                return 10;
            case 2:
                return 20;
            default:
                return 5;
        }
    }
}

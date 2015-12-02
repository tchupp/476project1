package edu.msu.chuppthe.steampunked.game;

import android.content.Context;

import edu.msu.chuppthe.steampunked.R;

public class Leak extends Pipe {

    public static Leak createLeak(Context context) {
        Leak leak = new Leak();
        leak.setId(context, R.drawable.leak);
        return leak;
    }

    public Leak() {
        super(false, false, true, false);
    }
}

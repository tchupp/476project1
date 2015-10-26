package edu.msu.chuppthe.steampunked;

import android.content.Context;

public class Leak extends Pipe {

    public static Leak createLeak(Context context, Player player) {
        Leak leak = new Leak();
        leak.setId(context, R.drawable.leak);

        leak.setPlayer(player);
        return leak;
    }

    public Leak() {
        super(false, false, true, false);
    }
}
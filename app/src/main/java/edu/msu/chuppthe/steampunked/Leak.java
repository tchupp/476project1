package edu.msu.chuppthe.steampunked;

import android.content.Context;

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

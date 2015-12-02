package edu.msu.chuppthe.steampunked.game;

import android.content.Context;

import java.util.List;

public abstract class PipeArea {

    protected int[] toIntArray(List<Integer> list) {
        int[] ints = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            ints[i] = list.get(i);
        }

        return ints;
    }

    protected Pipe createPipe(Context context, int imageId, Player player) {
        switch (imageId) {
            case Pipe.CAP_PIPE:
                return Pipe.createCapPipe(context, player);
            case Pipe.TEE_PIPE:
                return Pipe.createTeePipe(context, player);
            case Pipe.NINETY_PIPE:
                return Pipe.createNinetyPipe(context, player);
            default:
                return Pipe.createStraightPipe(context, player);
        }
    }
}

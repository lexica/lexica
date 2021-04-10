/*
 *  Copyright (C) 2008-2009 Rev. Johnny Healey <rev.null@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.serwylo.lexica;

import android.os.Handler;

import java.util.LinkedList;

public class Synchronizer implements Runnable {
    @SuppressWarnings("unused")
    private static String TAG = "Synchronizer";

    /**
     * Anything less than 15 ticks will result in the Android Espresso framework thinking that
     * the game is in a continuous loop, rather than idling. There are "correct" ways to fix this
     * using IdlingResources, but changing it so it is at least 15 high is the simplest.
     */
    public static final int TICK_FREQ = 100;

    public interface Counter {
        long tick();
    }

    public interface Event {
        void tick(long timeRemainingInMillis);
    }

    public interface Finalizer {
        void doFinalEvent();
    }

    private Counter mainCounter;
    private Finalizer mainFinalizer;
    private final LinkedList<Event> events;
    private boolean done;
    private final Handler handler;

    public Synchronizer() {
        mainCounter = null;
        mainFinalizer = null;
        events = new LinkedList<>();
        done = false;

        handler = new Handler();
    }

    public void setCounter(Counter c) {
        mainCounter = c;
    }

    public void setFinalizer(Finalizer f) {
        mainFinalizer = f;
    }

    public void addEvent(Event e) {
        events.add(e);
    }

    public void start() {
        done = false;
        handler.postDelayed(this, TICK_FREQ);
    }

    public void run() {
        if (done) {
            return;
        }

        long time = mainCounter.tick();

        for (Event event : events) {
            event.tick(time);
        }

        if (time <= 0) {
            if (mainFinalizer != null) {
                mainFinalizer.doFinalEvent();
            }
        }

        handler.postDelayed(this, TICK_FREQ);
    }

    public void abort() {
        done = true;
    }

}



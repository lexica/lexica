package com.serwylo.lexica.activities.score;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

class NonScrollingHorizontalLayoutManager extends LinearLayoutManager {
    NonScrollingHorizontalLayoutManager(Context context) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}

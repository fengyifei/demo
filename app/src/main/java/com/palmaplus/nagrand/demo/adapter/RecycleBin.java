package com.palmaplus.nagrand.demo.adapter;

import android.os.Build;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by lchad on 2016/12/14.
 * Github: https://github.com/lchad
 */
public class RecycleBin {

    private View[] mActiveViews = new View[0];
    private int[] mActiveViewTypes = new int[0];

    private SparseArray<View>[] mScrapViews;

    private int mViewTypeCount;

    private SparseArray<View> mCurrentScrapViews;

    public void setViewTypeCount(int viewTypeCount) {
        if (viewTypeCount < 1) {
            throw new IllegalArgumentException("Can't have a mViewTypeCount < 1");
        }
        //noinspection unchecked
        SparseArray<View>[] scrapViews = new SparseArray[viewTypeCount];
        for (int i = 0; i < viewTypeCount; i++) {
            scrapViews[i] = new SparseArray<>();
        }
        this.mViewTypeCount = viewTypeCount;
        mCurrentScrapViews = scrapViews[0];
        this.mScrapViews = scrapViews;
    }

    protected boolean shouldRecycleViewType(int viewType) {
        return viewType >= 0;
    }

    View getScrapView(int position, int viewType) {
        if (mViewTypeCount == 1) {
            return retrieveFromScrap(mCurrentScrapViews, position);
        } else if (viewType >= 0 && viewType < mScrapViews.length) {
            return retrieveFromScrap(mScrapViews[viewType], position);
        }
        return null;
    }

    void addScrapView(View scrap, int position, int viewType) {
        if (mViewTypeCount == 1) {
            mCurrentScrapViews.put(position, scrap);
        } else {
            mScrapViews[viewType].put(position, scrap);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            scrap.setAccessibilityDelegate(null);
        }
    }

    void scrapActiveViews() {
        final View[] activeViews = this.mActiveViews;
        final int[] activeViewTypes = this.mActiveViewTypes;
        final boolean multipleScraps = mViewTypeCount > 1;

        SparseArray<View> scrapViews = mCurrentScrapViews;
        final int count = activeViews.length;
        for (int i = count - 1; i >= 0; i--) {
            final View victim = activeViews[i];
            if (victim != null) {
                int whichScrap = activeViewTypes[i];

                activeViews[i] = null;
                activeViewTypes[i] = -1;

                if (!shouldRecycleViewType(whichScrap)) {
                    continue;
                }

                if (multipleScraps) {
                    scrapViews = this.mScrapViews[whichScrap];
                }
                scrapViews.put(i, victim);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    victim.setAccessibilityDelegate(null);
                }
            }
        }

        pruneScrapViews();
    }

    private void pruneScrapViews() {
        final int maxViews = mActiveViews.length;
        final int viewTypeCount = this.mViewTypeCount;
        final SparseArray<View>[] scrapViews = this.mScrapViews;
        for (int i = 0; i < viewTypeCount; ++i) {
            final SparseArray<View> scrapPile = scrapViews[i];
            int size = scrapPile.size();
            final int extras = size - maxViews;
            size--;
            for (int j = 0; j < extras; j++) {
                scrapPile.remove(scrapPile.keyAt(size--));
            }
        }
    }

    static View retrieveFromScrap(SparseArray<View> scrapViews, int position) {
        int size = scrapViews.size();
        if (size > 0) {
            // See if we still have a view for this position.
            for (int i = 0; i < size; i++) {
                int fromPosition = scrapViews.keyAt(i);
                View view = scrapViews.get(fromPosition);
                if (fromPosition == position) {
                    scrapViews.remove(fromPosition);
                    return view;
                }
            }
            int index = size - 1;
            View r = scrapViews.valueAt(index);
            scrapViews.remove(scrapViews.keyAt(index));
            return r;
        } else {
            return null;
        }
    }
}

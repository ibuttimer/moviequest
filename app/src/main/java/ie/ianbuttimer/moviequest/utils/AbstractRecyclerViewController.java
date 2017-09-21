/*
 * Copyright (C) 2017  Ian Buttimer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.ianbuttimer.moviequest.utils;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Comparator;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.data.IAdapterOnClickHandler;
import ie.ianbuttimer.moviequest.data.adapter.AbstractTMDbAdapter;
import ie.ianbuttimer.moviequest.tmdb.TMDbObject;

/**
 * Abstract class to provide base for a controller for a RecyclerView
 */
@SuppressWarnings("unused")
public abstract class AbstractRecyclerViewController<T extends TMDbObject> implements IAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AbstractTMDbAdapter<T> mAdapter;
    private IRecyclerViewHost mHost;

    /**
     * Constructor
     * @param activity      The current activity
     * @param viewId        Resource id of layout for the RecyclerView
     * @param layout        Type of layout to use
     * @param orientation   Layout orientation. Should be HORIZONTAL or VERTICAL. Defaults to VERTICAL.
     * @param reverseLayout When set to true, layouts from end to start. Default to false.
     * @param hasFixedSize  true if adapter changes cannot affect the size of the RecyclerView.
     * @param host          RecyclerView host
     * @param adapter       Adapter to use for RecyclerView
     */
    public AbstractRecyclerViewController(Activity activity, @IdRes int viewId, IRecyclerViewHost.LAYOUT_TYPE layout,
            int orientation, boolean reverseLayout, boolean hasFixedSize, IRecyclerViewHost host, AbstractTMDbAdapter<T> adapter) {
        // setup the recycler view
        mRecyclerView = activity.findViewById(viewId);

        switch (layout) {
            case LINEAR:
                mLayoutManager = new LinearLayoutManager(activity, orientation, reverseLayout);
                break;
            case GRID:
                mLayoutManager = new GridLayoutManager(activity, host.calcNumColumns(), orientation, reverseLayout);
                break;
            default:
                throw new IllegalArgumentException("Unknown layout type");
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(hasFixedSize);

        if (adapter != null) {
            setAdapter(adapter);
        }
        mHost = host;
    }

    /**
     * Constructor
     * @param activity      The current activity
     * @param viewId        Resource id of layout for the RecyclerView
     * @param layout        Type of layout to use
     * @param orientation   Layout orientation. Should be HORIZONTAL or VERTICAL. Defaults to VERTICAL.
     * @param reverseLayout When set to true, layouts from end to start. Default to false.
     * @param hasFixedSize  true if adapter changes cannot affect the size of the RecyclerView.
     */
    public AbstractRecyclerViewController(Activity activity, @IdRes int viewId, IRecyclerViewHost.LAYOUT_TYPE layout,
                                          int orientation, boolean reverseLayout, boolean hasFixedSize) {
        this(activity, viewId, layout, orientation, reverseLayout, hasFixedSize, null, null);
    }

    /**
     * Constructor
     * @param activity      The current activity
     * @param viewId        Resource id of layout for the RecyclerView
     * @param layout        Type of layout to use
     */
    public AbstractRecyclerViewController(Activity activity, @IdRes int viewId, IRecyclerViewHost.LAYOUT_TYPE layout) {
        this(activity, viewId, layout, LinearLayout.VERTICAL, false, false, null, null);
    }

    /**
     * Convenience method to clear the data set, and notify the adapter
     */
    public void clearAndNotify() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Convenience method to add items to items to the data set, and notify the adapter
     * @param all           Items to add
     * @param tester        Tester to use to filter which items are added
     * @param comparator    Comparator to sort the data set
     */
    public void addAndNotify(@NonNull T[] all, @Nullable ITester<T> tester, @Nullable Comparator<Object> comparator) {
        T[] toAdd;
        mAdapter.clear();
        if (tester != null) {
            toAdd = new FilterList<T>(all, tester).filter();
        } else {
            toAdd = all;
        }
        if (toAdd.length > 0) {
            mAdapter.addAll(toAdd);
            if (comparator != null) {
                mAdapter.sortList(comparator);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Convenience method to add items to items to the data set, and notify the adapter
     * @param all           Items to add
     * @param comparator    Comparator to sort the data set
     */
    public void addAndNotify(@NonNull T[] all, @Nullable Comparator<Object> comparator) {
        addAndNotify(all, null, comparator);
    }

    /**
     * Convenience method to add items to items to the data set, and notify the adapter
     * @param all           Items to add
     * @param tester        Tester to use to filter which items are added
     */
    public void addAndNotify(@NonNull T[] all, @NonNull ITester<T> tester) {
        addAndNotify(all, tester, null);
    }

    /**
     * Convenience method to add items to items to the data set, and notify the adapter
     * @param all           Items to add
     */
    public void addAndNotify(@NonNull T[] all) {
        addAndNotify(all, null, null);
    }

    /**
     * This method simply returns the number of items to display
     * @return The number of items available in the dataset.
     */
    public int getItemCount() {
        int count = 0;
        if (mAdapter != null) {
            count = mAdapter.getItemCount();
        }
        return count;
    }

    public void setHost(IRecyclerViewHost mHost) {
        this.mHost = mHost;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
    }

    public AbstractTMDbAdapter<T> getAdapter() {
        return mAdapter;
    }

    public void setAdapter(AbstractTMDbAdapter<T> adapter) {
        // get adapter to responsible for linking data with the Views that display it
        mAdapter = adapter;
        mAdapter.setClickHandler(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onItemClick(View view) {
        T obj = (T) view.getTag(R.id.tmdb_obj_tag);
        if (mHost != null) {
            mHost.onItemClick(obj);
        }
    }

    /**
     * Interface for a RecyclerView host
     */
    public interface IRecyclerViewHost {

        enum LAYOUT_TYPE { LINEAR, GRID };

        int calcNumColumns();

        /**
         * Process the click event
         * @param obj  View that was clicked
         */
        <T extends TMDbObject> void onItemClick(T obj);
    }


}

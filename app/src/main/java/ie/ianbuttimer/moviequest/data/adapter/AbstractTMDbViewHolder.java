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
package ie.ianbuttimer.moviequest.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ie.ianbuttimer.moviequest.data.IAdapterOnClickHandler;
import ie.ianbuttimer.moviequest.tmdb.TMDbObject;


/**
 * A base RecyclerView.ViewHolder for TMDbObject objects
 */
@SuppressWarnings("unused")
abstract class AbstractTMDbViewHolder<T extends TMDbObject> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final View mView;
    private IAdapterOnClickHandler mClickHandler;

    /**
     * Constructor
     * @param view          View to hold
     * @param clickHandler  onClick handler for view
     */
    public AbstractTMDbViewHolder(View view, IAdapterOnClickHandler clickHandler) {
        super(view);

        mView = view;
        mClickHandler = clickHandler;

        view.setOnClickListener(this);
    }

    /**
     * Get a context reference
     * @return  context reference
     */
    public Context getContext() {
        return mView.getContext();
    }

    /**
     * Set the details to display
     * @param info   Information object to use
     */
    public abstract void setViewInfo(T info);

    @Override
    public void onClick(View v) {
        // pass the click onto the click handler
        if (mClickHandler != null) {
            mClickHandler.onItemClick(v);
        }
    }

    /**
     * Set the click handler
     * @param clickHandler  onClick handler for view
     */
    public void setClickHandler(IAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }
}

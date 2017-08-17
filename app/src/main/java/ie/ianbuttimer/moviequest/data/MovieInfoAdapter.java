/**
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
package ie.ianbuttimer.moviequest.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;

/**
 * Adapter class for a RecyclerView of movies
 */
public class MovieInfoAdapter<T extends MovieInfo> extends RecyclerView.Adapter<MovieInfoViewHolder> {

        private List<T> mMovieList;         // list of objects represented by this adapter
        private MovieInfoAdapterOnClickHandler mClickHandler;

        /**
         * Constructor
         * @param objects       The objects to represent in the list.
         * @param clickHandler  Click handler for the views in this adapter
         */
        public MovieInfoAdapter(@NonNull List<T> objects, @Nullable MovieInfoAdapterOnClickHandler clickHandler) {
            mMovieList = objects;
            mClickHandler = clickHandler;
        }

        /**
         * This gets called when each new ViewHolder is created. This happens when the RecyclerView
         * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
         *
         * @param viewGroup The ViewGroup that these ViewHolders are contained within.
         * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
         *                  can use this viewType integer to provide a different layout. See
         *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
         *                  for more details.
         * @return A new MovieInfoViewHolder that holds the View for each list item
         */
        @Override
        public MovieInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean attachImmediately = false;

            View view = inflater.inflate(R.layout.movie_info_list_item, viewGroup, attachImmediately);

            return new MovieInfoViewHolder(view, mClickHandler);
        }

        /**
         * OnBindViewHolder is called by the RecyclerView to display the data at the specified
         * position. In this method, we update the contents of the ViewHolder to display the weather
         * details for this particular position, using the "position" argument that is conveniently
         * passed into us.
         *
         * @param viewHolder The ViewHolder which should be updated to represent the
         *                   contents of the item at the given position in the data set.
         * @param position   The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(MovieInfoViewHolder viewHolder, int position) {
            T movie = getItem(position);
            if (movie != null) {
                // set the movie object as the view tag for easy retrieval later
                viewHolder.itemView.setTag(R.id.movie_id_tag, movie);
                viewHolder.setViewInfo(movie);      // set the view's elements to the movie's info
            }



        }

        /**
         * Get the data item associated with the specified position in the data set.
         * @param position   Position of the item whose data we want within the adapter's data set.
         * @return The data at the specified position. This value may be null.
         */
        public T getItem(int position) {
            T item = null;
            if (mMovieList != null) {
                item = mMovieList.get(position);
            }
            return item;
        }

        /**
         * This method simply returns the number of items to display. It is used behind the scenes
         * to help layout our Views and for animations.
         *
         * @return The number of items available in our forecast
         */
        @Override
        public int getItemCount() {
            int length = 0;
            if (mMovieList != null) {
                length = mMovieList.size();
            }
            return length;
        }

        /**
         * Adds the specified object at the end of the list.
         * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
         * @param item  The object to add at the end of the list.
         */
        public void add(T item) {
            mMovieList.add(item);
        }

        /**
         * Adds the specified items at the end of the list.
         * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
         * @param items The items to add at the end of the list.
         */
        public void addAll(T... items) {
            addAll(Arrays.asList(items));
        }

        /**
         * Adds the specified items at the end of the list.
         * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
         * @param collection The Collection to add at the end of the list.
         */
        public void addAll(Collection<? extends T> collection) {
            mMovieList.addAll(collection);
        }

        /**
         * Remove all elements from the list.
         * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
         */
        public void clear() {
            mMovieList.clear();
        }

        /**
         * Sets the list to the specified items.
         * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
         * @param items The items to set the list.
         */
        public void setList(T... items) {
            setList(Arrays.asList(items));
        }

        /**
         * Sets the list to the specified items.
         * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
         * @param collection The Collection to set the list.
         */
        public void setList(Collection<? extends T> collection) {
            mMovieList.clear();
            mMovieList.addAll(collection);
        }

    /**
     * Interface for movie adapter
     */
    public interface MovieInfoAdapterOnClickHandler {
        void onItemClick(View view);
    }
}

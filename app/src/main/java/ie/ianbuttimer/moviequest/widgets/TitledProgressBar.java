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

package ie.ianbuttimer.moviequest.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.utils.Utils;

/**
 * Progress bar with a title.<br>
 * The widget has the following attributes:
 * <ul>
 *     <li><b>valueProgressSize</b>: size of progress bar, may be set in pixels or dp</li>
 *     <li><b>valueTitle</b>: Title to display</li>
 * </ul>
 */

public class TitledProgressBar extends LinearLayout {

    private TextView mTextViewTitle;
    private ProgressBar mProgressBar;

    public TitledProgressBar(Context context) {
        this(context, null);
    }

    public TitledProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TitledProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        String title = null;
        String progressSize = null;

        if ( attrs != null ) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitledProgressBar, 0, 0);
            title = a.getString(R.styleable.TitledProgressBar_valueTitle);
            progressSize = a.getString(R.styleable.TitledProgressBar_valueProgressSize);

            a.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.titled_progress_bar, this, true);

        mTextViewTitle = findViewById(R.id.tv_titled_progress_bar);
        mProgressBar = findViewById(R.id.pb_titled_progress_bar);

        if (TextUtils.isEmpty(title)) {
            setTitle("");
        } else {
            setTitle(title);
        }

        setProgressSize(progressSize);
    }

    /**
     * Set the title
     * @param title Title to display
     */
    public void setTitle(String title) {
        mTextViewTitle.setText(title);
    }

    /**
     * Set the title
     * @param titleId ID of title to display
     */
    public void setTitle(@StringRes int titleId) {
        mTextViewTitle.setText(titleId);
    }

    public void setProgressSize(String progressSize) {
        if (!TextUtils.isEmpty(progressSize)) {
            String size = progressSize.trim();
            boolean dp = size.endsWith("dp");       // hardcoded value
            boolean dip = size.endsWith("dip");     // when value passed using resource identifier
            if (dp) {
                size = size.substring(0, size.indexOf("dp"));
            } else if (dip) {
                size = size.substring(0, size.indexOf("dip"));
            }
            try {
                int pixels = Float.valueOf(size).intValue();
                if (dp || dip) {
                    pixels = Utils.convertDpToPixels(mProgressBar.getContext(), pixels);
                }
                mProgressBar.setLayoutParams(new LayoutParams(pixels, pixels));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unable to convert argument to valid size: " + progressSize);
            }
        }
    }
}

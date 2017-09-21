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

import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * Basic list filtering class
 */
@SuppressWarnings("unused")
public class FilterList<T> implements ITester<T> {

    private T[] baseList;
    private ITester<T> tester;

    /**
     * Constructor
     * @param baseList  Base list to be filtered
     * @param tester    Tester to use for filtering
     */
    public FilterList(@Nonnull T[] baseList, ITester<T> tester) {
        this.baseList = baseList;
        this.tester = tester;
    }


    public void setBaseList(T[] baseList) {
        this.baseList = baseList;
    }

    public void setTester(ITester<T> tester) {
        this.tester = tester;
    }

    /**
     * Test if the specified objects matches this object's tester
     * @param obj   Object to test
     * @return  <code>true</code> if matches criteria, <code>false</code> otherwise
     */
    @Override
    public boolean test(T obj) {
        boolean result = false;
        if (tester != null) {
            result = tester.test(obj);
        }
        return result;
    }

    /**
     * Filter a list using a tester
     * @param baseList  Base list to be filtered
     * @param tester    Tester to use for filtering
     * @return  Filtered list
     */
    public T[] filter(@Nonnull T[] baseList, @Nonnull ITester<T> tester) {
        T[] subset = Arrays.copyOf(baseList, baseList.length);
        int length = subset.length;
        for (int i = 0; i < length; ) {
            if (!tester.test(subset[i])) {
                // not required, remove by shifting array left
                int moveIdx = i + 1;
                if (moveIdx < length) {
                    System.arraycopy(subset, moveIdx, subset, i, (length - moveIdx));
                }
                --length;
            } else {
                // required, move to next
                ++i;
            }
        }
        return Arrays.copyOf(subset, length);
    }

    /**
     * Filter this object's list using a tester
     * @param tester    Tester to use for filtering
     * @return  Filtered list
     */
    public T[] filter(@Nonnull ITester<T> tester) {
        return filter(baseList, tester);
    }

    /**
     * Filter this object's list using it's tester
     * @return  Filtered list
     */
    public T[] filter() {
        T[] subset = null;
        if (tester != null) {
            subset = filter(baseList, tester);
        }
        return subset;
    }
}

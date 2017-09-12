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

package ie.ianbuttimer.moviequest.tmdb;

/**
 * Test utility class
 */

public class TestFieldInfo {

    private String message;         // assert message
    private int index;              // field index
    private Object value;           // test value
    private boolean mustHaveValue;  // must have value to do test flag

    public TestFieldInfo(String message, int index, Object value, boolean mustHaveValue) {
        this.message = message;
        this.index = index;
        this.value = value;
        this.mustHaveValue = mustHaveValue;
    }

    public TestFieldInfo(String message, int index, Object value) {
        this(message, index, value, false);
    }

    public String getMessage() {
        return message;
    }

    public int getIndex() {
        return index;
    }

    public Object getValue() {
        return value;
    }

    public boolean isMustHaveValue() {
        return mustHaveValue;
    }
}

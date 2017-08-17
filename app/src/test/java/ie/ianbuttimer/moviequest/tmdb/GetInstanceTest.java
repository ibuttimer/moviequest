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
package ie.ianbuttimer.moviequest.tmdb;

import org.json.JSONException;
import org.json.JSONObject;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Base class for test object instance classes
 */
public abstract class GetInstanceTest {

    /**
     * Test string fields
     * @param jsonObject    JSON object
     * @param testInfo      Array of test infos
     * @param provider      Test object instance
     */
    public void checkStringFields(JSONObject jsonObject, TestFieldInfo[] testInfo, TestObjectInstance provider) {
        try {
            for (TestFieldInfo info: testInfo) {
                String fieldName = provider.getFieldName(info.getIndex());
                String value = (String)info.getValue();
                boolean doTest = true;
                if (info.isMustHaveValue()) {
                    doTest = (jsonObject.has(fieldName) && !value.equals(""));
                }
                if (doTest) {
                    assertEquals(makeAssertMessage(info.getMessage()),
                            jsonObject.getString(fieldName),
                            value);
                }
            }
        }
        catch (JSONException e) {
            fail(makeAssertMessage("JSONObject property error: " + e.getMessage()));
        }
    }

    /**
     * Test int fields
     * @param jsonObject    JSON object
     * @param testInfo      Array of test infos
     * @param provider      Test object instance
     */
    public void checkIntFields(JSONObject jsonObject, TestFieldInfo[] testInfo, TestObjectInstance provider) {
        try {
            for (TestFieldInfo info: testInfo) {
                String fieldName = provider.getFieldName(info.getIndex());
                boolean doTest = true;
                if (info.isMustHaveValue()) {
                    doTest = jsonObject.has(fieldName);
                }
                if (doTest) {
                    assertEquals(makeAssertMessage(info.getMessage()),
                            (long)jsonObject.getInt(fieldName),
                            Long.valueOf((int)info.getValue()).longValue());
                }
            }
        }
        catch (JSONException e) {
            fail(makeAssertMessage("JSONObject property error: " + e.getMessage()));
        }
    }

    /**
     * Test double fields
     * @param jsonObject    JSON object
     * @param testInfo      Array of test infos
     * @param provider      Test object instance
     */
    public void checkDoubleFields(JSONObject jsonObject, TestFieldInfo[] testInfo, TestObjectInstance provider) {
        try {
            for (TestFieldInfo info: testInfo) {
                String fieldName = provider.getFieldName(info.getIndex());
                boolean doTest = true;
                if (info.isMustHaveValue()) {
                    doTest = jsonObject.has(fieldName);
                }
                if (doTest) {
                    assertEquals(makeAssertMessage(info.getMessage()),
                            jsonObject.getDouble(fieldName),
                            (double)info.getValue(),
                            0.1d);
                }
            }
        }
        catch (JSONException e) {
            fail(makeAssertMessage("JSONObject property error: " + e.getMessage()));
        }
    }

    /**
     * Test boolean fields
     * @param jsonObject    JSON object
     * @param testInfo      Array of test infos
     * @param provider      Test object instance
     */
    public void checkBooleanFields(JSONObject jsonObject, TestFieldInfo[] testInfo, TestObjectInstance provider) {
        try {
            for (TestFieldInfo info: testInfo) {
                String fieldName = provider.getFieldName(info.getIndex());
                boolean doTest = true;
                if (info.isMustHaveValue()) {
                    doTest = jsonObject.has(fieldName);
                }
                if (doTest) {
                    assertEquals(makeAssertMessage(info.getMessage()),
                            jsonObject.getBoolean(fieldName),
                            info.getValue());
                }
            }
        }
        catch (JSONException e) {
            fail(makeAssertMessage("JSONObject property error: " + e.getMessage()));
        }
    }

    /**
     * Test object fields
     * @param jsonObject    JSON object
     * @param testInfo      Array of test infos
     * @param provider      Test object instance
     */
    public void checkObjectFields(JSONObject jsonObject, TestFieldInfo[] testInfo, TestObjectInstance provider, IGetInstance instancer) {
        try {
            for (TestFieldInfo info: testInfo) {
                String fieldName = provider.getFieldName(info.getIndex());
                boolean doTest = true;
                if (info.isMustHaveValue()) {
                    doTest = jsonObject.has(fieldName);
                }
                if (doTest) {
                    Object object = instancer.getInstance(info.getIndex(), jsonObject.getJSONObject(fieldName));
                    assertEquals(makeAssertMessage(info.getMessage()),
                        object,
                        info.getValue());
                }
            }
        }
        catch (JSONException e) {
            fail(makeAssertMessage("JSONObject property error: " + e.getMessage()));
        }
    }

    /**
     * Make a message to use for an assert
     * @param msg   Message text
     * @return
     */
    protected String makeAssertMessage(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }


    public interface IGetInstance {
        /**
         * Return an object instance
         * @param index     Field index of object to return
         * @param json      JSON object to convert
         * @return
         */
        Object getInstance(int index, JSONObject json);
    }
}
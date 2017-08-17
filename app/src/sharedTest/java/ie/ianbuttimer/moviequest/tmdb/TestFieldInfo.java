package ie.ianbuttimer.moviequest.tmdb;

/**
 * Created by Ian on 17/08/2017.
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

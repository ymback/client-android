package me.ltype.lightreader.query;

/**
 * Created by ltype on 2015/6/11.
 */
public class Utils {
    public static String toString(final Object[] array, final char token, final boolean includeSpace) {
        final StringBuilder builder = new StringBuilder();
        final int length = array.length;
        for (int i = 0; i < length; i++) {
            final String string = objectToString(array[i]);
            if (string != null) {
                if (i > 0) {
                    builder.append(includeSpace ? token + " " : token);
                }
                builder.append(string);
            }
        }
        return builder.toString();
    }

    private static String objectToString(Object o) {
        if (o instanceof SQLLang)
            return ((SQLLang) o).getSQL();
        return o != null ? o.toString() : null;
    }
}

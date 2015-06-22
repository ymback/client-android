package me.ltype.lightniwa.query;

/**
 * Created by ltype on 2015/6/11.
 */
public class OrderBy implements SQLLang {

    private final String[] orderBy;
    private final boolean[] ascending;

    public OrderBy(final String[] orderBy, final boolean[] ascending) {
        this.orderBy = orderBy;
        this.ascending = ascending;
    }

    public OrderBy(final String... orderBy) {
        this(orderBy, null);
    }

    public OrderBy(final String orderBy, final boolean ascending) {
        this.orderBy = new String[]{orderBy};
        this.ascending = new boolean[]{ascending};
    }

    @Override
    public String getSQL() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, j = orderBy.length; i < j; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(orderBy[i]);
            if (ascending != null) {
                sb.append(ascending[i] ? " ASC" : " DESC");
            }
        }
        return sb.toString();
    }

}

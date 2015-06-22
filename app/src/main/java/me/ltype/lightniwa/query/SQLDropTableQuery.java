package me.ltype.lightniwa.query;

/**
 * Created by ltype on 2015/6/22.
 */
public class SQLDropTableQuery extends SQLDropQuery {
    public SQLDropTableQuery(final boolean dropIfExists, final String table) {
        super(dropIfExists, "TABLE", table);
    }
}

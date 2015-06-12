package me.ltype.lightreader.query;

/**
 * Created by ltype on 2015/6/11.
 */
public class SQLQueryBuilder {
    private SQLQueryBuilder() {
        throw new AssertionError("You can't create instance for this class");
    }

    public static SQLCreateTableQuery.Builder createTable(final boolean temporary, final boolean createIfNotExists,
                                                          final String name) {
        return new SQLCreateTableQuery.Builder().createTable(temporary, createIfNotExists, name);
    }

    public static SQLCreateTableQuery.Builder createTable(final boolean createIfNotExists, final String name) {
        return createTable(false, createIfNotExists, name);
    }

    public static SQLCreateTableQuery.Builder createTable(final String name) {
        return createTable(false, false, name);
    }
}

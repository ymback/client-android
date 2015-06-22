package me.ltype.lightniwa.query;

import android.util.Log;

/**
 * Created by ltype on 2015/6/11.
 */
public class SQLCreateTableQuery implements SQLQuery {
    private boolean temporary;
    private boolean createIfNotExists;
    private String table;
    private NewColumn[] newColumns;
    private SQLSelectQuery selectStmt;
    private Constraint[] constraints;

    @Override
    public String getSQL() {
        if (table == null) throw new NullPointerException("NAME must not be null!");
        if ((newColumns == null || newColumns.length == 0) && selectStmt == null)
            throw new NullPointerException("Columns or AS must not be null!");
        final StringBuilder sb = new StringBuilder("CREATE ");
        if (temporary) {
            sb.append("TEMPORARY ");
        }
        sb.append("TABLE ");
        if (createIfNotExists) {
            sb.append("IF NOT EXISTS ");
        }
        sb.append(table);
        sb.append(' ');
        if (newColumns != null && newColumns.length > 0) {
            sb.append('(');
            sb.append(Utils.toString(newColumns, ',', true));
            if (constraints != null) {
                sb.append(' ');
                sb.append(Utils.toString(constraints, ',', true));
                sb.append(' ');
            }
            sb.append(')');
        } else {
            sb.append("AS ");
            sb.append(selectStmt.getSQL());
        }
        return sb.toString();
    }

    private void setTemporary(final boolean temporary) {
        this.temporary = temporary;
    }

    private void setCreateIfNotExists(final boolean createIfNotExists) {
        this.createIfNotExists = createIfNotExists;
    }

    private void setTable(final String table) {
        this.table = table;
    }

    private void setNewColumns(final NewColumn[] newColumns) {
        this.newColumns = newColumns;
    }

    public static final class Builder implements IBuilder<SQLCreateTableQuery> {
        private final SQLCreateTableQuery query = new SQLCreateTableQuery();
        private boolean buildCalled;

        @Override
        public SQLCreateTableQuery build() {
            buildCalled = true;
            return query;
        }

        @Override
        public String buildSQL() {
            return build().getSQL();
        }

        public Builder createTable(final boolean temporary, final boolean createIfNotExists, final String table) {
            checkNotBuilt();
            query.setTemporary(temporary);
            query.setCreateIfNotExists(createIfNotExists);
            query.setTable(table);
            Log.e("createTable", table);
            return this;
        }

        public Builder createTable(final boolean createIfNotExists, final String table) {
            return createTable(false, createIfNotExists, table);
        }

        public Builder createTemporaryTable(final boolean createIfNotExists, final String table) {
            return createTable(true, createIfNotExists, table);
        }

        private void checkNotBuilt() {
            if (buildCalled) throw new IllegalStateException();
        }

        public Builder columns(final NewColumn... newColumns) {
            checkNotBuilt();
            query.setNewColumns(newColumns);
            return this;
        }
    }
}

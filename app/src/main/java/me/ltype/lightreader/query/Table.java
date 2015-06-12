package me.ltype.lightreader.query;

/**
 * Created by ltype on 2015/6/11.
 */
public class Table implements SelecTable {

    public static final Table NEW = new Table("NEW");

    private final String table;

    public Table(final String table) {
        this.table = table;
    }

    @Override
    public String getSQL() {
        return table;
    }

}
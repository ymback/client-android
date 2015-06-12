package me.ltype.lightreader.query;

/**
 * Created by ltype on 2015/6/11.
 */
public interface SQLLang extends Cloneable {
    /**
     * Build SQL query string
     *
     * @return SQL query
     */
    public String getSQL();
}

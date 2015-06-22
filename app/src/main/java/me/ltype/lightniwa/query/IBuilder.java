package me.ltype.lightniwa.query;

/**
 * Created by ltype on 2015/6/11.
 */
public interface IBuilder<T extends SQLLang> {
    public T build();

    /**
     * Equivalent to {@link #build()}.{@link SQLLang#getSQL()}
     *
     * @return
     */
    public String buildSQL();
}

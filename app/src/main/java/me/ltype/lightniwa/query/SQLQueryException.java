package me.ltype.lightniwa.query;

/**
 * Created by ltype on 2015/6/11.
 */
public class SQLQueryException extends RuntimeException {
    private static final long serialVersionUID = 910158450604676104L;

    public SQLQueryException() {
    }

    public SQLQueryException(final String detailMessage) {
        super(detailMessage);
    }

    public SQLQueryException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    public SQLQueryException(final Throwable throwable) {
        super(throwable);
    }
}

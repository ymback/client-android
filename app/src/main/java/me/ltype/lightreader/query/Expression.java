package me.ltype.lightreader.query;

import java.util.Locale;

import me.ltype.lightreader.query.Columns.Column;

/**
 * Created by ltype on 2015/6/11.
 */
public class Expression implements SQLLang {
    private final String expr;

    public Expression(final String expr) {
        this.expr = expr;
    }

    public Expression(SQLLang lang) {
        this(lang.getSQL());
    }

    public static Expression and(final Expression... expressions) {
        return new Expression(toExpr(expressions, "AND"));
    }

    public static Expression equals(final Column l, final Column r) {
        return new Expression(String.format(Locale.ROOT, "%s = %s", l.getSQL(), r.getSQL()));
    }

    public static Expression equals(final Column l, final SelecTable r) {
        return new Expression(String.format(Locale.ROOT, "%s = (%s)", l.getSQL(), r.getSQL()));
    }

    public static Expression equals(final String l, final SelecTable r) {
        return new Expression(String.format(Locale.ROOT, "%s = (%s)", l, r.getSQL()));
    }

    public static Expression equals(final Column l, final long r) {
        return new Expression(String.format(Locale.ROOT, "%s = %d", l.getSQL(), r));
    }

    public static Expression equals(final Column l, final String r) {
        return new Expression(String.format(Locale.ROOT, "%s = '%s'", l.getSQL(), r));
    }

    public static Expression equals(final String l, final long r) {
        return new Expression(String.format(Locale.ROOT, "%s = %d", l, r));
    }

    public static Expression greaterThan(final String l, final long r) {
        return new Expression(String.format(Locale.ROOT, "%s > %d", l, r));
    }

    public static Expression in(final Column column, final SelecTable in) {
        return new Expression(String.format("%s IN(%s)", column.getSQL(), in.getSQL()));
    }

    public static Expression notEquals(final String l, final long r) {
        return new Expression(String.format(Locale.ROOT, "%s != %d", l, r));
    }

    public static Expression notEquals(final String l, final String r) {
        return new Expression(String.format("%s != %s", l, r));
    }

    public static Expression notIn(final Column column, final SelecTable in) {
        return new Expression(String.format("%s NOT IN(%s)", column.getSQL(), in.getSQL()));
    }

    public static Expression notNull(final Column column) {
        return new Expression(String.format("%s NOT NULL", column.getSQL()));
    }

    public static Expression or(final Expression... expressions) {
        return new Expression(toExpr(expressions, "OR"));
    }

    private static String toExpr(final Expression[] array, final String token) {
        final StringBuilder builder = new StringBuilder();
        builder.append('(');
        final int length = array.length;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                builder.append(String.format(" %s ", token));
            }
            builder.append(array[i].getSQL());
        }
        builder.append(')');
        return builder.toString();
    }

    public static Expression equalsArgs(String l) {
        return new Expression(String.format(Locale.ROOT, "%s = ?", l));
    }

    public static Expression isNull(Column column) {
        return new Expression(String.format(Locale.ROOT, "%s IS NULL", column.getSQL()));
    }

    public static Expression greaterThan(Column column1, Column column2) {
        return new Expression(String.format(Locale.ROOT, "%s > %s", column1.getSQL(), column2.getSQL()));
    }

    public static Expression likeRaw(final Column column, final String pattern, final String escape) {
        return new Expression(String.format(Locale.ROOT, "%s LIKE %s ESCAPE '%s'", column.getSQL(), pattern, escape));
    }



    public static Expression like(final Column column, final SQLLang expression) {
        return new Expression(String.format(Locale.ROOT, "%s LIKE %s", column.getSQL(), expression.getSQL()));
    }


    public static Expression likeRaw(final Column column, final String pattern) {
        return new Expression(String.format(Locale.ROOT, "%s LIKE %s", column.getSQL(), pattern));
    }


    @Override
    public String getSQL() {
        return expr;
    }
}

package me.ltype.lightreader.query;

/**
 * Created by ltype on 2015/6/11.
 */
public class Join implements SQLLang {

    private final boolean natural;
    private final Operation operation;
    private final SelecTable source;
    private final Expression on;

    public Join(boolean natural, Operation operation, SelecTable source, Expression on) {
        this.natural = natural;
        this.operation = operation;
        this.source = source;
        this.on = on;
    }

    @Override
    public String getSQL() {
        if (operation == null) throw new IllegalArgumentException("operation can't be null!");
        if (source == null) throw new IllegalArgumentException("source can't be null!");
        final StringBuilder builder = new StringBuilder();
        if (natural) {
            builder.append("NATURAL ");
        }
        builder.append(operation.getSQL());
        builder.append(" JOIN ");
        builder.append(source.getSQL());
        if (on != null) {
            builder.append(" ON ");
            builder.append(on.getSQL());
        }
        return builder.toString();
    }

    public enum Operation implements SQLLang {
        LEFT("LEFT"), LEFT_OUTER("LEFT OUTER"), INNER("INNER"), CROSS("CROSS");
        private final String op;

        Operation(String op) {
            this.op = op;
        }

        @Override
        public String getSQL() {
            return op;
        }

    }
}
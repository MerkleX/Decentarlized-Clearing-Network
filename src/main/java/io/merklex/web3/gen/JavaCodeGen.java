package io.merklex.web3.gen;

public class JavaCodeGen {
    public static final String TAB = "    ";

    private final StringBuilder builder = new StringBuilder();

    private final Block root = new Block(null, 0);

    public Line pkg() {
        return root.line().append("package ");
    }

    public Line importLine() {
        return root.line().append("import ");
    }

    public Block cls(String name) {
        root.line().append("@javax.annotation.Generated(value=\"merklex-code-gen\")").end();
        return root.cls(name);
    }

    public void space() {
        root.space();
    }

    public class Tabbed extends Block {
        Tabbed(Block parent, int tabs) {
            super(parent, tabs);
        }

        @Override
        public Block end() {
            return super.parent;
        }
    }

    public class Block {
        private final Block parent;
        private final int tabs;

        Block(Block parent, int tabs) {
            this.parent = parent;
            this.tabs = tabs;
        }

        public Line line() {
            tab();
            return new Line(this);
        }

        public Block tabbed() {
            return new Tabbed(this, tabs + 1);
        }

        public void codeBlock(String data) {
            String[] lines = data.split("\n");
            for (String line : lines) {
                line().append(line).end();
            }
        }

        public void space() {
            builder.append('\n');
        }

        public Block staticClass(String name) {
            line().append("public static class ").append(name).append(" {").end();
            return new Block(this, tabs + 1);
        }

        public Block staticClassInherit(String name, String inherit) {
            line().append("public static class ").append(name).append(" implements ").append(inherit).append(" {").end();
            return new Block(this, tabs + 1);
        }

        public Block cls(String name) {
            line().append("public class ").append(name).append(" {").end();
            return new Block(this, tabs + 1);
        }

        public Arguments privateMethod(String name, String type) {
            return new Arguments(line().append("private ").append(type).append(" ").append(name).append("("));
        }

        public Arguments publicMethod(String name, String type) {
            return new Arguments(line().append("public ").append(type).append(" ").append(name).append("("));
        }

        public Arguments publicStaticMethod(String name, String type) {
            return new Arguments(line().append("public static ").append(type).append(" ").append(name).append("("));
        }

        public Block end() {
            parent.line().append("}").end();
            return parent;
        }

        private void tab() {
            for (int i = 0; i < tabs; i++) {
                builder.append(TAB);
            }
        }

        public Block block() {
            return new Block(this, tabs + 1);
        }

        public Block publicEnum(String name) {
            line().append("public enum ").append(name).append(" {").end();
            return new Block(this, tabs + 1);
        }
    }

    public class Arguments {
        private final Line line;
        private int count = 0;
        private String throwsStr = "";

        public Arguments(Line line) {
            this.line = line;
        }

        public Arguments arg(String name, String type) {
            if (count++ > 0) {
                line.append(", ");
            }
            line.append(type).append(" ").append(name);
            return this;
        }

        public Arguments Throws(String th) {
            throwsStr = "throws " + th + " ";
            return this;
        }

        public Block end() {
            line.append(") ").append(throwsStr).append("{").end();
            return new Block(line.parent, line.parent.tabs + 1);
        }
    }

    public class Line {
        private final Block parent;

        public Line(Block parent) {
            this.parent = parent;
        }

        public <T> Line append(T value) {
            builder.append(value);
            return this;
        }

        public Block end() {
            builder.append('\n');
            return parent;
        }
    }

    public String toString() {
        return builder.toString();
    }

}

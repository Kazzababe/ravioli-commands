package ravioli.gravioli.command.parse;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
public final class StringTraverser {
    private final String content;
    private final boolean endsInWhitespace;

    @Setter
    private int cursor;

    public StringTraverser(@NotNull final String content) {
        this.content = content.stripLeading();
        this.endsInWhitespace = content.endsWith(" ");
    }

    public StringTraverser(@NotNull final StringTraverser stringTraverser) {
        this.content = stringTraverser.content;
        this.cursor = stringTraverser.cursor;
        this.endsInWhitespace = stringTraverser.endsInWhitespace;
    }

    public boolean endsInWhitespace() {
        return this.endsInWhitespace;
    }

    public boolean hasNext() {
        return this.cursor < this.content.length();
    }

    public char current() {
        return this.content.charAt(this.cursor);
    }

    public char next() {
        return this.content.charAt(this.cursor++);
    }

    public char peekNext() {
        return this.content.charAt(this.cursor + 1);
    }

    public char back() {
        return this.content.charAt(this.cursor--);
    }

    public char peekBack() {
        return this.content.charAt(this.cursor - 1);
    }

    public @NotNull String peekString() {
        final int currentPosition = this.cursor;
        final String result = this.readString();

        this.cursor = currentPosition;

        return result;
    }

    public @NotNull String readString() {
        final StringBuilder stringBuilder = new StringBuilder();
        char currentChar;

        while (this.hasNext() && !Character.isSpaceChar(currentChar = this.current())) {
            stringBuilder.append(currentChar);

            this.next();
        }
        return stringBuilder.toString();
    }

    public @NotNull String readGreedyString() {
        final StringBuilder stringBuilder = new StringBuilder();

        while (this.hasNext()) {
            stringBuilder.append(this.current());

            this.next();
        }
        return stringBuilder.toString();
    }

    public @NotNull String readWrappedString(final char originalWrapperChar, final boolean optional) {
        final Character wrapperChar;

        if (originalWrapperChar == this.current()) {
            wrapperChar = originalWrapperChar;
        } else if (optional) {
            wrapperChar = null;
        } else {
            throw new IllegalArgumentException("Can't parse wrapped string that does not start with the wrapper character.");
        }
        final StringBuilder stringBuilder = new StringBuilder();
        char currentChar;

        this.next();

        while (this.hasNext()) {
            currentChar = this.next();

            if (wrapperChar != null && currentChar == wrapperChar) {
                break;
            }
            stringBuilder.append(currentChar);
        }
        return stringBuilder.toString();
    }

    public int readInt() {
        final String next = this.readString();

        try {
            return Integer.parseInt(next);
        } catch (final NumberFormatException e) {
            throw e; // TODO: Throw traversal exception or command parse exception?
        }
    }

    public double readDouble() {
        final String next = this.readString();

        try {
            return Double.parseDouble(next);
        } catch (final NumberFormatException e) {
            throw e; // TODO: Throw traversal exception or command parse exception?
        }
    }

    public float readFloat() {
        final String next = this.readString();

        try {
            return Float.parseFloat(next);
        } catch (final NumberFormatException e) {
            throw e; // TODO: Throw traversal exception or command parse exception?
        }
    }

    public long readLong() {
        final String next = this.readString();

        try {
            return Long.parseLong(next);
        } catch (final NumberFormatException e) {
            throw e; // TODO: Throw traversal exception or command parse exception?
        }
    }

    public boolean readStateful(@NotNull final String trueLabel, @NotNull final String falseLabel) {
        final String next = this.readString();

        if (next.equalsIgnoreCase(trueLabel)) {
            return true;
        }
        if (next.equalsIgnoreCase(falseLabel)) {
            return false;
        }
        throw new IllegalArgumentException("Parsed string does not match either provided stateful true/false labels."); // TODO: Throw traversal exception or command parse exception?
    }

    public boolean readBoolean() {
        return this.readStateful("true", "false");
    }

    public <T extends Enum<T>> @NotNull T readEnum(@NotNull final Class<T> enumClass) {
        final String next = this.readString();

        try {
            return Enum.valueOf(enumClass, next.toUpperCase());
        } catch (final IllegalArgumentException e) {
            throw e; // TODO: Throw traversal exception or command parse exception?
        }
    }

    public int traverseWhitespace() {
        int amount = 0;

        while (this.hasNext() && Character.isSpaceChar(this.current())) {
            this.next();

            amount++;
        }
        return amount;
    }

    public void apply(@NotNull final StringTraverser stringTraverser) {
        stringTraverser.cursor = this.cursor;
    }
}

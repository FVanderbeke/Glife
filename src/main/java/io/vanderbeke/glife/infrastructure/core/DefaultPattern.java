package io.vanderbeke.glife.infrastructure.core;

import io.vanderbeke.glife.api.model.Pattern;

import java.util.stream.IntStream;

public class DefaultPattern implements Pattern {

    public static Builder aBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {}

        private int width;
        private int height;
        private int[] space;

        public int getWidth() {
            return width;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public int getHeight() {
            return height;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public int[] getSpace() {
            return space;
        }

        public Builder setSpace(int[] space) {
            this.space = space;
            return this;
        }

        public DefaultPattern build() {
            return new DefaultPattern(this);
        }
    }

    private final int width;
    private final int height;
    private final int[] space;

    private DefaultPattern(Builder builder) {
        this.width = builder.getWidth();
        this.height = builder.getHeight();
        this.space = builder.getSpace();
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public IntStream space() {
        return IntStream.of(space);
    }
}

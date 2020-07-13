package io.vanderbeke.glife.business.basic;

class Cell {

    static Cell of(int x, int y) {
        return new Cell(x, y);
    }

    final int x;
    final int y;

    private Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isInRange(int width, int height) {
        return x >= 0 && x < height && y >= 0 && y < width;
    }
}

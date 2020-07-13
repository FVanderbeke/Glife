package io.vanderbeke.glife.api.model;

/**
 * How neighbors should be found in the universe.
 *
 * If FIXED, neighbors on the borders only see inside cells.
 *
 * If CIRCULAR, the top border cells see the bottom border ones (and the bottom border ones see the top border ones);
 * the left border ones see the right border ones (and vice versa)
 */
public enum ExpansionStrategy {
    FIXED, CIRCULAR
}

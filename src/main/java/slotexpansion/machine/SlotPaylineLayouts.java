package slotexpansion.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stores the row-major base and optional paylines for each supported grid width.
 *
 * <p>The comment above each mask is a top-to-bottom diagram of the grid.
 * {@code X} marks a payline cell, {@code .} marks an unused cell, and {@code |}
 * separates the three rows.</p>
 */
final class SlotPaylineLayouts {
    private SlotPaylineLayouts() {
    }

    static List<int[]> baseForColumnCount(int columnCount) {
        return switch (columnCount) {
            case 3 -> baseThreeColumnGrid();
            case 4 -> baseFourColumnGrid();
            case 5 -> baseFiveColumnGrid();
            default -> throw new IllegalArgumentException("Unsupported grid width: " + columnCount);
        };
    }

    static List<int[]> additionalForColumnCount(int columnCount) {
        return switch (columnCount) {
            case 3 -> additionalThreeColumnGrid();
            case 4 -> additionalFourColumnGrid();
            case 5 -> additionalFiveColumnGrid();
            default -> throw new IllegalArgumentException("Unsupported grid width: " + columnCount);
        };
    }

    private static List<int[]> baseThreeColumnGrid() {
        return new ArrayList<>(Arrays.asList(
                // X X X |
                // . . . |
                // . . .
                new int[]{1, 1, 1, 0, 0, 0, 0, 0, 0},
                // . . . |
                // X X X |
                // . . .
                new int[]{0, 0, 0, 1, 1, 1, 0, 0, 0},
                // . . . |
                // . . . |
                // X X X
                new int[]{0, 0, 0, 0, 0, 0, 1, 1, 1},
                // X . . |
                // . X . |
                // . . X
                new int[]{1, 0, 0, 0, 1, 0, 0, 0, 1},
                // . . X |
                // . X . |
                // X . .
                new int[]{0, 0, 1, 0, 1, 0, 1, 0, 0}
        ));
    }

    private static List<int[]> baseFourColumnGrid() {
        return new ArrayList<>(Arrays.asList(
                // X X X X |
                // . . . . |
                // . . . .
                new int[]{1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                // . . . . |
                // X X X X |
                // . . . .
                new int[]{0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},
                // . . . . |
                // . . . . |
                // X X X X
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
                // X . . X |
                // . X X . |
                // . . . .
                new int[]{1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0},
                // . . . . |
                // X . . X |
                // . X X .
                new int[]{0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0},
                // . . . . |
                // . X X . |
                // X . . X
                new int[]{0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1},
                // . X X . |
                // X . . X |
                // . . . .
                new int[]{0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0},
                // X . . . |
                // . X X . |
                // . . . X
                new int[]{1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1},
                // . . . X |
                // . X X . |
                // X . . .
                new int[]{0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0}
        ));
    }

    private static List<int[]> baseFiveColumnGrid() {
        return new ArrayList<>(Arrays.asList(
                // X X X X X |
                // . . . . . |
                // . . . . .
                new int[]{1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                // . . . . . |
                // X X X X X |
                // . . . . .
                new int[]{0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                // . . . . . |
                // . . . . . |
                // X X X X X
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1},
                // X . . . X |
                // . X . X . |
                // . . X . .
                new int[]{1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0},
                // . . X . . |
                // . X . X . |
                // X . . . X
                new int[]{0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1},
                // . X X X . |
                // X . . . X |
                // . . . . .
                new int[]{0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                // . . . . . |
                // X . . . X |
                // . X X X .
                new int[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0},
                // . . . . X |
                // . X X X . |
                // X . . . .
                new int[]{0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0},
                // . . . . . |
                // . X X X . |
                // X . . . X
                new int[]{0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1},
                // X . . . X |
                // . X X X . |
                // . . . . .
                new int[]{1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                // X . . . . |
                // . X X X . |
                // . . . . X
                new int[]{1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1},
                // . X X X . |
                // . . . . . |
                // X . . . X
                new int[]{0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                // X . . . X |
                // . . . . . |
                // . X X X .
                new int[]{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
                // . . . X X |
                // . . X . . |
                // X X . . .
                new int[]{0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0},
                // X X . . . |
                // . . X . . |
                // . . . X X
                new int[]{1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1}
        ));
    }

    private static List<int[]> additionalThreeColumnGrid() {
        return new ArrayList<>(Arrays.asList(
                // . X . |
                // X . X |
                // . . .
                new int[]{0, 1, 0, 1, 0, 1, 0, 0, 0},
                // . . . |
                // . X . |
                // X . X
                new int[]{0, 0, 0, 0, 1, 0, 1, 0, 1},
                // X . X |
                // . X . |
                // . . .
                new int[]{1, 0, 1, 0, 1, 0, 0, 0, 0},
                // . . . |
                // X . X |
                // . X .
                new int[]{0, 0, 0, 1, 0, 1, 0, 1, 0}
        ));
    }

    private static List<int[]> additionalFourColumnGrid() {
        return new ArrayList<>(Arrays.asList(
                // . X X . |
                // . . . . |
                // X . . X
                new int[]{0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
                // X . . X |
                // . . . . |
                // . X X .
                new int[]{1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0},
                // . . . . |
                // . X . X |
                // X . X .
                new int[]{0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0},
                // . X . X |
                // X . X . |
                // . . . .
                new int[]{0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0},
                // . X . X |
                // . . . . |
                // X . X .
                new int[]{0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0},
                // X . X . |
                // . X . X |
                // . . . .
                new int[]{1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0},
                // . . . . |
                // X . X . |
                // . X . X
                new int[]{0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1},
                // X . X . |
                // . . . . |
                // . X . X
                new int[]{1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                // . . X X |
                // . . . . |
                // X X . .
                new int[]{0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0},
                // X X . . |
                // . . . . |
                // . . X X
                new int[]{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1}
        ));
    }

    private static List<int[]> additionalFiveColumnGrid() {
        return new ArrayList<>(Arrays.asList(
                // X X . X X |
                // . . X . . |
                // . . . . .
                new int[]{1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                // . . . . . |
                // . . X . . |
                // X X . X X
                new int[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1},
                // . . X . . |
                // X X . X X |
                // . . . . .
                new int[]{0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0},
                // . . . . . |
                // X X . X X |
                // . . X . .
                new int[]{0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0},
                // X . X . X |
                // . X . X . |
                // . . . . .
                new int[]{1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0},
                // . . . . . |
                // X . X . X |
                // . X . X .
                new int[]{0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                // . X . X . |
                // X . X . X |
                // . . . . .
                new int[]{0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0},
                // . . . . . |
                // . X . X . |
                // X . X . X
                new int[]{0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
                // X . X . X |
                // . . . . . |
                // . X . X .
                new int[]{1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0},
                // . X . X . |
                // . . . . . |
                // X . X . X
                new int[]{0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1},
                // X X . X X |
                // . . . . . |
                // . . X . .
                new int[]{1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                // . . X . . |
                // . . . . . |
                // X X . X X
                new int[]{0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1},
                // X . . . X |
                // . . X . . |
                // . X . X .
                new int[]{1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0},
                // . X . X . |
                // . . X . . |
                // X . . . X
                new int[]{0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1},
                // . X . X . |
                // X . . . X |
                // . . X . .
                new int[]{0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0},
                // . . X . . |
                // X . . . X |
                // . X . X .
                new int[]{0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0},
                // . X . . . |
                // X . X . X |
                // . . . X .
                new int[]{0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0},
                // . . . X . |
                // X . X . X |
                // . X . . .
                new int[]{0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0}
        ));
    }
}

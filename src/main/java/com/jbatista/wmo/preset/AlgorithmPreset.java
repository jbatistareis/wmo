package com.jbatista.wmo.preset;

/**
 * <p>A 2D array representing an algorithm structure.</p>
 * <p><code>[0], [1], [n]</code></p>
 * <p><code>[Carriers...], [Oscillator X, Feedback source Y], [Oscillator X, Modulated by oscillator Y]...</code></p>
 * <p>
 *  <ul>
 *     <li><code>Index 0</code> contains the oscillators that are going to act as carriers.</li>
 *     <li><code>Index 1</code> contains the oscillator that is going to receive feedback, and its source.</li>
 *     <li><code>Index 2</code> onward describes from where an oscillator receives its modulation, repeats for every oscillator.</li>
 *  </ul>
 * </p>
 */
public enum AlgorithmPreset {

    ALGO_4_OSC_1(1, new int[][]{{0}, {3, 3}, {0, 1}, {1, 2}, {2, 3}}),
    ALGO_4_OSC_2(2, new int[][]{{0}, {3, 3}, {0, 1}, {1, 2}, {1, 3}}),
    ALGO_4_OSC_3(3, new int[][]{{0}, {3, 3}, {0, 1}, {1, 2}, {0, 3}}),
    ALGO_4_OSC_4(4, new int[][]{{0}, {3, 3}, {0, 1}, {0, 2}, {2, 3}}),
    ALGO_4_OSC_5(5, new int[][]{{0, 2}, {3, 3}, {0, 1}, {2, 3}}),
    ALGO_4_OSC_6(6, new int[][]{{0, 1, 2}, {3, 3}, {0, 3}, {1, 3}, {2, 3}}),
    ALGO_4_OSC_7(7, new int[][]{{0, 1, 2}, {3, 3}, {2, 3}}),
    ALGO_4_OSC_8(8, new int[][]{{0, 1, 2, 3}, {3, 3}}),

    ALGO_6_OSC_1(1, new int[][]{{0, 2}, {5, 4}, {0, 1}, {2, 3}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_2(2, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_3(3, new int[][]{{0, 3}, {5, 5}, {0, 1}, {1, 2}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_4(4, new int[][]{{0, 3}, {5, 3}, {0, 1}, {1, 2}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_5(5, new int[][]{{0, 2, 4}, {5, 5}, {0, 1}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_6(6, new int[][]{{0, 2, 4}, {5, 4}, {0, 1}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_7(7, new int[][]{{0, 2}, {5, 5}, {0, 1}, {2, 3}, {2, 4}, {4, 5}}),
    ALGO_6_OSC_8(8, new int[][]{{0, 2}, {3, 3}, {0, 1}, {2, 3}, {2, 4}, {4, 5}}),
    ALGO_6_OSC_9(9, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {2, 4}, {4, 5}}),
    ALGO_6_OSC_10(10, new int[][]{{0, 3}, {2, 2}, {0, 1}, {1, 2}, {3, 5}, {3, 4}}),
    ALGO_6_OSC_11(11, new int[][]{{0, 3}, {5, 5}, {0, 1}, {1, 2}, {3, 5}, {3, 4}}),
    ALGO_6_OSC_12(12, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {2, 4}, {2, 5}}),
    ALGO_6_OSC_13(13, new int[][]{{0, 2}, {5, 5}, {0, 1}, {2, 3}, {2, 4}, {2, 5}}),
    ALGO_6_OSC_14(14, new int[][]{{0, 2}, {5, 5}, {0, 1}, {2, 3}, {3, 5}, {3, 4}}),
    ALGO_6_OSC_15(15, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_16(16, new int[][]{{0}, {5, 5}, {0, 1}, {0, 2}, {0, 4}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_17(17, new int[][]{{0}, {1, 1}, {0, 1}, {0, 2}, {0, 4}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_18(18, new int[][]{{0}, {2, 2}, {0, 1}, {0, 2}, {0, 3}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_19(19, new int[][]{{0, 3, 4}, {5, 5}, {0, 1}, {1, 2}, {3, 5}}),
    ALGO_6_OSC_20(20, new int[][]{{0, 1, 3}, {2, 2}, {0, 2}, {1, 2}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_21(21, new int[][]{{0, 1, 3, 4}, {2, 2}, {0, 2}, {1, 2}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_22(22, new int[][]{{0, 2, 3, 4}, {5, 5}, {0, 1}, {2, 5}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_23(23, new int[][]{{0, 1, 3, 4}, {5, 5}, {1, 2}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_24(24, new int[][]{{0, 1, 2, 3, 4}, {5, 5}, {2, 5}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_25(25, new int[][]{{0, 1, 2, 3, 4}, {5, 5}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_26(26, new int[][]{{0, 1, 3}, {5, 5}, {1, 2}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_27(27, new int[][]{{0, 1, 3}, {2, 2}, {1, 2}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_28(28, new int[][]{{0, 2, 5}, {4, 4}, {0, 1}, {2, 3}, {3, 4}}),
    ALGO_6_OSC_29(29, new int[][]{{0, 1, 2, 4}, {5, 5}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_30(30, new int[][]{{0, 1, 2, 5}, {4, 4}, {2, 3}, {3, 4}}),
    ALGO_6_OSC_31(31, new int[][]{{0, 1, 2, 3, 4}, {5, 5}, {4, 5}}),
    ALGO_6_OSC_32(32, new int[][]{{0, 1, 2, 3, 4, 5}, {5, 5}});

    private int id;
    private int[][] pattern;

    AlgorithmPreset(int id, int[][] pattern) {
        this.id = id;
        this.pattern = pattern;
    }

    public int getId() {
        return id;
    }

    public int[][] getPattern() {
        return pattern;
    }

}

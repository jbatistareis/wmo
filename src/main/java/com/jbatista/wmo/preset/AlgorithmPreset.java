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

    ALGO_4_OSC_1(4, 1, new int[][]{{0}, {3, 3}, {0, 1}, {1, 2}, {2, 3}}),
    ALGO_4_OSC_2(4, 2, new int[][]{{0}, {3, 3}, {0, 1}, {1, 2}, {1, 3}}),
    ALGO_4_OSC_3(4, 3, new int[][]{{0}, {3, 3}, {0, 1}, {1, 2}, {0, 3}}),
    ALGO_4_OSC_4(4, 4, new int[][]{{0}, {3, 3}, {0, 1}, {0, 2}, {2, 3}}),
    ALGO_4_OSC_5(4, 5, new int[][]{{0, 2}, {3, 3}, {0, 1}, {2, 3}}),
    ALGO_4_OSC_6(4, 6, new int[][]{{0, 1, 2}, {3, 3}, {0, 3}, {1, 3}, {2, 3}}),
    ALGO_4_OSC_7(4, 7, new int[][]{{0, 1, 2}, {3, 3}, {2, 3}}),
    ALGO_4_OSC_8(4, 8, new int[][]{{0, 1, 2, 3}, {3, 3}}),

    ALGO_6_OSC_1(6, 1, new int[][]{{0, 2}, {5, 5}, {0, 1}, {2, 3}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_2(6, 2, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_3(6, 3, new int[][]{{0, 3}, {5, 5}, {0, 1}, {1, 2}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_4(6, 4, new int[][]{{0, 3}, {5, 3}, {0, 1}, {1, 2}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_5(6, 5, new int[][]{{0, 2, 4}, {5, 5}, {0, 1}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_6(6, 6, new int[][]{{0, 2, 4}, {5, 4}, {0, 1}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_7(6, 7, new int[][]{{0, 2}, {5, 5}, {0, 1}, {2, 3}, {2, 4}, {4, 5}}),
    ALGO_6_OSC_8(6, 8, new int[][]{{0, 2}, {3, 3}, {0, 1}, {2, 3}, {2, 4}, {4, 5}}),
    ALGO_6_OSC_9(6, 9, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {2, 4}, {4, 5}}),
    ALGO_6_OSC_10(6, 10, new int[][]{{0, 3}, {2, 2}, {0, 1}, {1, 2}, {3, 5}, {3, 4}}),
    ALGO_6_OSC_11(6, 11, new int[][]{{0, 3}, {5, 5}, {0, 1}, {1, 2}, {3, 5}, {3, 4}}),
    ALGO_6_OSC_12(6, 12, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {2, 4}, {2, 5}}),
    ALGO_6_OSC_13(6, 13, new int[][]{{0, 2}, {5, 5}, {0, 1}, {2, 3}, {2, 4}, {2, 5}}),
    ALGO_6_OSC_14(6, 14, new int[][]{{0, 2}, {5, 5}, {0, 1}, {2, 3}, {3, 5}, {3, 4}}),
    ALGO_6_OSC_15(6, 15, new int[][]{{0, 2}, {1, 1}, {0, 1}, {2, 3}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_16(6, 16, new int[][]{{0}, {5, 5}, {0, 1}, {0, 2}, {0, 4}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_17(6, 17, new int[][]{{0}, {1, 1}, {0, 1}, {0, 2}, {0, 4}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_18(6, 18, new int[][]{{0}, {2, 2}, {0, 1}, {0, 2}, {0, 3}, {3, 4}, {4, 5}}),
    ALGO_6_OSC_19(6, 19, new int[][]{{0, 3, 4}, {5, 5}, {0, 1}, {1, 2}, {3, 5}}),
    ALGO_6_OSC_20(6, 20, new int[][]{{0, 1, 3}, {2, 2}, {0, 2}, {1, 2}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_21(6, 21, new int[][]{{0, 1, 3, 4}, {2, 2}, {0, 2}, {1, 2}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_22(6, 22, new int[][]{{0, 2, 3, 4}, {5, 5}, {0, 1}, {2, 5}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_23(6, 23, new int[][]{{0, 1, 3, 4}, {5, 5}, {1, 2}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_24(6, 24, new int[][]{{0, 1, 2, 3, 4}, {5, 5}, {2, 5}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_25(6, 25, new int[][]{{0, 1, 2, 3, 4}, {5, 5}, {3, 5}, {4, 5}}),
    ALGO_6_OSC_26(6, 26, new int[][]{{0, 1, 3}, {5, 5}, {1, 2}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_27(6, 27, new int[][]{{0, 1, 3}, {2, 2}, {1, 2}, {3, 4}, {3, 5}}),
    ALGO_6_OSC_28(6, 28, new int[][]{{0, 2, 5}, {4, 4}, {0, 1}, {2, 3}, {3, 4}}),
    ALGO_6_OSC_29(6, 29, new int[][]{{0, 1, 2, 4}, {5, 5}, {2, 3}, {4, 5}}),
    ALGO_6_OSC_30(6, 30, new int[][]{{0, 1, 2, 5}, {4, 4}, {2, 3}, {3, 4}}),
    ALGO_6_OSC_31(6, 31, new int[][]{{0, 1, 2, 3, 4}, {5, 5}, {4, 5}}),
    ALGO_6_OSC_32(6, 32, new int[][]{{0, 1, 2, 3, 4, 5}, {5, 5}});

    private int oscillatorCount;
    private int id;
    private int[][] pattern;

    AlgorithmPreset(int oscillatorCount, int id, int[][] pattern) {
        this.oscillatorCount = oscillatorCount;
        this.id = id;
        this.pattern = pattern;
    }

    public int getOscillatorCount() {
        return oscillatorCount;
    }

    public int getId() {
        return id;
    }

    public int[][] getPattern() {
        return pattern;
    }

}

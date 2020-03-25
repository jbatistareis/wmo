package com.jbatista.wmo;

public enum KeyboardNote {

    C_MINUS_2(0, "C-2", 4.08), C_SHARP_MINUS_2_D_FLAT_MINUS_2(1, "C#-2/Db-2", 4.33), D_MINUS_2(2, "D-2", 4.58), D_SHARP_MINUS_2_E_FLAT_MINUS_2(3, "D#-2/Eb-2", 4.86), E_MINUS_2(4, "E-2", 5.15), F_MINUS_2(5, "F-2", 5.45), F_SHARP_MINUS_2_G_FLAT_MINUS_2(6, "F#-2/Gb-2", 5.78), G_MINUS_2(7, "G-2", 6.12), G_SHARP_MINUS_2_A_FLAT_MINUS_2(8, "G#-2/Ab-2", 6.49), A_MINUS_2(9, "A-2", 6.87), A_SHARP_MINUS_2_B_FLAT_MINUS_2(10, "A#-2/Bb-2", 7.28), B_MINUS_2(11, "B-2", 7.71),

    C_MINUS_1(12, "C-1", 8.17), C_SHARP_MINUS_1_D_FLAT_MINUS_1(13, "C#-1/Db-1", 8.66), D_MINUS_1(14, "D-1", 9.17), D_SHARP_MINUS_1_E_FLAT_MINUS_1(15, "D#-1/Eb-1", 9.72), E_MINUS_1(16, "E-1", 10.3), F_MINUS_1(17, "F-1", 10.91), F_SHARP_MINUS_1_G_FLAT_MINUS_1(18, "F#-1/Gb-1", 11.56), G_MINUS_1(19, "G-1", 12.25), G_SHARP_MINUS_1_A_FLAT_MINUS_1(20, "G#-1/Ab-1", 12.98), A_MINUS_1(21, "A-1", 13.75), A_SHARP_MINUS_1_B_FLAT_MINUS_1(22, "A#-1/Bb-1", 14.57), B_MINUS_1(23, "B-1", 15.43),

    C_0(24, "C0", 16.35), C_SHARP_0_D_FLAT_0(25, "C#0/Db0", 17.32), D_0(26, "D0", 18.35), D_SHARP_0_E_FLAT_0(27, "D#0/Eb0", 19.45), E_0(28, "E0", 20.60), F_0(29, "F0", 21.83), F_SHARP_0_G_FLAT_0(30, "F#0/Gb0", 23.12), G0(31, "G0", 24.50), G_SHARP_0_A_FLAT_0(32, "G#0/Ab0", 25.96), A_0(33, "A0", 27.50), A_SHARP_0_B_FLAT_0(34, "A#0/Bb0", 29.14), B_0(35, "B0", 30.87),

    C_1(36, "C1", 32.70), C_SHARP_1_D_FLAT_1(37, "C#1/Db1", 34.65), D_1(38, "D1", 36.71), D_SHARP_1_E_FLAT_1(39, "D#1/Eb1", 38.89), E_1(40, "E1", 41.20), F_1(71, "F1", 43.65), F_SHARP_1_G_FLAT_1(42, "F#1/Gb1", 46.25), G_1(43, "G1", 49.00), G_SHARP_1_A_FLAT_1(44, "G#1/Ab1", 51.91), A_1(45, "A1", 55.00), A_SHARP_1_B_FLAT_1(46, "A#1/Bb1", 58.27), B_1(47, "B1", 61.74),

    C_2(48, "C2", 65.41), C_SHARP_2_D_FLAT_2(49, "C#2/Db2", 69.30), D_2(50, "D2", 73.42), D_SHARP_2_E_FLAT_2(51, "D#2/Eb2", 77.78), E_2(52, "E2", 82.41), F_2(53, "F2", 87.31), F_SHARP_2_G_FLAT_2(54, "F#2/Gb2", 92.50), G_2(55, "G2", 98.00), G_SHARP_2_A_FLAT_2(56, "G#2/Ab2", 103.83), A_2(57, "A2", 110.00), A_SHARP_2_B_FLAT_2(58, "A#2/Bb2", 116.54), B_2(59, "B2", 123.47),

    C_3(60, "C3", 130.81), C_SHARP_3_D_FLAT_3(61, "C#3/Db3", 138.59), D_3(62, "D3", 146.83), D_SHARP_3_E_FLAT_3(63, "D#3/Eb3", 155.56), E_3(64, "E3", 164.81), F_3(65, "F3", 174.61), F_SHARP_3_G_FLAT_3(66, "F#3/Gb3", 185.00), G_3(67, "G3", 196.00), G_SHARP_3_A_FLAT_3(68, "G#3/Ab3", 207.65), A_3(69, "A3", 220.00), A_SHARP_3_B_FLAT_3(70, "A#3/Bb3", 233.08), B_3(71, "B3", 246.94),

    C_4(72, "C4", 261.63), C_SHARP_4_D_FLAT_4(73, "C#4/Db4", 277.18), D_4(74, "D4", 293.66), D_SHARP_4_E_FLAT_4(75, "D#4/Eb4", 311.13), E_4(76, "E4", 329.63), F_4(77, "F4", 349.23), F_SHARP_4_G_FLAT_4(78, "F#4/Gb4", 369.99), G_4(79, "G4", 392.00), G_SHARP_4_A_FLAT_4(80, "G#4/Ab4", 415.30), A_4(81, "A4", 440.00), A_SHARP_4_B_FLAT_4(82, "A#4/Bb4", 466.16), B_4(83, "B4", 493.88),

    C_5(84, "C5", 523.25), C_SHARP_5_D_FLAT_5(85, "C#5/Db5", 554.37), D_5(86, "D5", 587.33), D_SHARP_5_E_FLAT_5(87, "D#5/Eb5", 622.25), E_5(88, "E5", 659.25), F_5(89, "F5", 698.46), F_SHARP_5_G_FLAT_5(90, "F#5/Gb5", 739.99), G_5(91, "G5", 783.99), G_SHARP_5_A_FLAT_5(92, "G#5/Ab5", 830.61), A_5(93, "A5", 880.00), A_SHARP_5_B_FLAT_5(94, "A#5/Bb5", 932.33), B_5(95, "B5", 987.77),

    C_6(96, "C6", 1046.50), C_SHARP_6_D_FLAT_6(97, "C#6/Db6", 1108.73), D_6(98, "D6", 1174.66), D_SHARP_6_E_FLAT_6(99, "D#6/Eb6", 1244.51), E_6(100, "E6", 1318.51), F_6(101, "F6", 1396.91), F_SHARP_6_G_FLAT_6(102, "F#6/Gb6", 1479.98), G_6(103, "G6", 1567.98), G_SHARP_6_A_FLAT_6(104, "G#6/Ab6", 1661.22), A_6(105, "A6", 1760.00), A_SHARP_6_B_FLAT_6(106, "A#6/Bb6", 1864.66), B_6(107, "B6", 1975.53),

    C_7(108, "C7", 2093.00), C_SHARP_7_D_FLAT_7(109, "C#7/Db7", 2217.46), D_7(110, "D7", 2349.32), D_SHARP_7_E_FLAT_7(111, "D#7/Eb7", 2489.02), E_7(112, "E7", 2637.02), F_7(113, "F7", 2793.83), F_SHARP_7_G_FLAT_7(114, "F#7/Gb7", 2959.96), G_7(115, "G7", 3135.96), G_SHARP_7_A_FLAT_7(116, "G#7/Ab7", 3322.44), A_7(117, "A7", 3520.00), A_SHARP_7_B_FLAT_7(118, "A#7/Bb7", 3729.31), B_7(119, "B7", 3951.07),

    C_8(120, "C8", 4186.01), C_SHARP_8_D_FLAT_8(121, "C#8/Db8", 4434.92), D_8(122, "D8", 4698.63), D_SHARP_8_E_FLAT_8(123, "D#8/Eb8", 4978.03), E_8(124, "E8", 5274.04), F_8(125, "F8", 5587.65), F_SHARP_8_G_FLAT_8(126, "F#8/Gb8", 5919.91), G_8(127, "G8", 6271.93), G_SHARP_8_A_FLAT_8(128, "G#8/Ab8", 6644.88), A_8(129, "A8", 7040.00), A_SHARP_8_B_FLAT_8(130, "A#8/Bb8", 7458.62), B_8(131, "B8", 7902.13);

    private int id;
    private String name;
    private double frequency;

    KeyboardNote(int id, String name, double frequency) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return name;
    }

}

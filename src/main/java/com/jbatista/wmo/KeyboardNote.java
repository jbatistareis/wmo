package com.jbatista.wmo;

public enum KeyboardNote {

    C_MINUS_2("C-2", 4.08), C_SHARP_MINUS_2_D_FLAT_MINUS_2("C#-2/Db-2", 4.33), D_MINUS_2("D-2", 4.58), D_SHARP_MINUS_2_E_FLAT_MINUS_2("D#-2/Eb-2", 4.86), E_MINUS_2("E-2", 5.15), F_MINUS_2("F-2", 5.45), F_SHARP_MINUS_2_G_FLAT_MINUS_2("F#-2/Gb-2", 5.78), G_MINUS_2("G-2", 6.12), G_SHARP_MINUS_2_A_FLAT_MINUS_2("G#-2/Ab-2", 6.49), A_MINUS_2("A-2", 6.87), A_SHARP_MINUS_2_B_FLAT_MINUS_2("A#-2/Bb-2", 7.28), B_MINUS_2("B-2", 7.71),
    C_MINUS_1("C-1", 8.17), C_SHARP_MINUS_1_D_FLAT_MINUS_1("C#-1/Db-1", 8.66), D_MINUS_1("D-1", 9.17), D_SHARP_MINUS_1_E_FLAT_MINUS_1("D#-1/Eb-1", 9.72), E_MINUS_1("E-1", 10.3), F_MINUS_1("F-1", 10.91), F_SHARP_MINUS_1_G_FLAT_MINUS_1("F#-1/Gb-1", 11.56), G_MINUS_1("G-1", 12.25), G_SHARP_MINUS_1_A_FLAT_MINUS_1("G#-1/Ab-1", 12.98), A_MINUS_1("A-1", 13.75), A_SHARP_MINUS_1_B_FLAT_MINUS_1("A#-1/Bb-1", 14.57), B_MINUS_1("B-1", 15.43),
    C_0("C0", 16.35), C_SHARP_0_D_FLAT_0("C#0/Db0", 17.32), D_0("D0", 18.35), D_SHARP_0_E_FLAT_0("D#0/Eb0", 19.45), E_0("E0", 20.60), F_0("F0", 21.83), F_SHARP_0_G_FLAT_0("F#0/Gb0", 23.12), G0("G0", 24.50), G_SHARP_0_A_FLAT_0("G#0/Ab0", 25.96), A_0("A0", 27.50), A_SHARP_0_B_FLAT_0("A#0/Bb0", 29.14), B_0("B0", 30.87),
    C_1("C1", 32.70), C_SHARP_1_D_FLAT_1("C#1/Db1", 34.65), D_1("D1", 36.71), D_SHARP_1_E_FLAT_1("D#1/Eb1", 38.89), E_1("E1", 41.20), F_1("F1", 43.65), F_SHARP_1_G_FLAT_1("F#1/Gb1", 46.25), G_1("G1", 49.00), G_SHARP_1_A_FLAT_1("G#1/Ab1", 51.91), A_1("A1", 55.00), A_SHARP_1_B_FLAT_1("A#1/Bb1", 58.27), B_1("B1", 61.74),
    C_2("C2", 65.41), C_SHARP_2_D_FLAT_2("C#2/Db2", 69.30), D_2("D2", 73.42), D_SHARP_2_E_FLAT_2("D#2/Eb2", 77.78), E_2("E2", 82.41), F_2("F2", 87.31), F_SHARP_2_G_FLAT_2("F#2/Gb2", 92.50), G_2("G2", 98.00), G_SHARP_2_A_FLAT_2("G#2/Ab2", 103.83), A_2("A2", 110.00), A_SHARP_2_B_FLAT_2("A#2/Bb2", 116.54), B_2("B2", 123.47),
    C_3("C3", 130.81), C_SHARP_3_D_FLAT_3("C#3/Db3", 138.59), D_3("D3", 146.83), D_SHARP_3_E_FLAT_3("D#3/Eb3", 155.56), E_3("E3", 164.81), F_3("F3", 174.61), F_SHARP_3_G_FLAT_3("F#3/Gb3", 185.00), G_3("G3", 196.00), G_SHARP_3_A_FLAT_3("G#3/Ab3", 207.65), A_3("A3", 220.00), A_SHARP_3_B_FLAT_3("A#3/Bb3", 233.08), B_3("B3", 246.94),
    C_4("C4", 261.63), C_SHARP_4_D_FLAT_4("C#4/Db4", 277.18), D_4("D4", 293.66), D_SHARP_4_E_FLAT_4("D#4/Eb4", 311.13), E_4("E4", 329.63), F_4("F4", 349.23), F_SHARP_4_G_FLAT_4("F#4/Gb4", 369.99), G_4("G4", 392.00), G_SHARP_4_A_FLAT_4("G#4/Ab4", 415.30), A_4("A4", 440.00), A_SHARP_4_B_FLAT_4("A#4/Bb4", 466.16), B_4("B4", 493.88),
    C_5("C5", 523.25), C_SHARP_5_D_FLAT_5("C#5/Db5", 554.37), D_5("D5", 587.33), D_SHARP_5_E_FLAT_5("D#5/Eb5", 622.25), E_5("E5", 659.25), F_5("F5", 698.46), F_SHARP_5_G_FLAT_5("F#5/Gb5", 739.99), G_5("G5", 783.99), G_SHARP_5_A_FLAT_5("G#5/Ab5", 830.61), A_5("A5", 880.00), A_SHARP_5_B_FLAT_5("A#5/Bb5", 932.33), B_5("B5", 987.77),
    C_6("C6", 1046.50), C_SHARP_6_D_FLAT_6("C#6/Db6", 1108.73), D_6("D6", 1174.66), D_SHARP_6_E_FLAT_6("D#6/Eb6", 1244.51), E_6("E6", 1318.51), F_6("F6", 1396.91), F_SHARP_6_G_FLAT_6("F#6/Gb6", 1479.98), G_6("G6", 1567.98), G_SHARP_6_A_FLAT_6("G#6/Ab6", 1661.22), A_6("A6", 1760.00), A_SHARP_6_B_FLAT_6("A#6/Bb6", 1864.66), B_6("B6", 1975.53),
    C_7("C7", 2093.00), C_SHARP_7_D_FLAT_7("C#7/Db7", 2217.46), D_7("D7", 2349.32), D_SHARP_7_E_FLAT_7("D#7/Eb7", 2489.02), E_7("E7", 2637.02), F_7("F7", 2793.83), F_SHARP_7_G_FLAT_7("F#7/Gb7", 2959.96), G_7("G7", 3135.96), G_SHARP_7_A_FLAT_7("G#7/Ab7", 3322.44), A_7("A7", 3520.00), A_SHARP_7_B_FLAT_7("A#7/Bb7", 3729.31), B_7("B7", 3951.07),
    C_8("C8", 4186.01), C_SHARP_8_D_FLAT_8("C#8/Db8", 4434.92), D_8("D8", 4698.63), D_SHARP_8_E_FLAT_8("D#8/Eb8", 4978.03), E_8("E8", 5274.04), F_8("F8", 5587.65), F_SHARP_8_G_FLAT_8("F#8/Gb8", 5919.91), G_8("G8", 6271.93), G_SHARP_8_A_FLAT_8("G#8/Ab8", 6644.88), A_8("A8", 7040.00), A_SHARP_8_B_FLAT_8("A#8/Bb8", 7458.62), B_8("B8", 7902.13);

    private String name;
    private double frequency;

    KeyboardNote(String name, double frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return name;
    }

    public double getFrequency() {
        return frequency;
    }

}

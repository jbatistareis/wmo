package com.jbatista.wmo.midi;

public class Event {

    private Type type;
    private int value1;
    private int value2;
    private int channel;

    protected Event(Type type, int value1, int value2, int channel) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.channel = channel;
    }

    protected enum Type {
        SET_INSTRUMENT,
        SET_BPM,
        KEY_PRESS,
        KEY_RELEASE
    }


    protected Type getType() {
        return type;
    }

    protected int getValue1() {
        return value1;
    }

    protected int getValue2() {
        return value2;
    }

    protected int getChannel() {
        return channel;
    }

}

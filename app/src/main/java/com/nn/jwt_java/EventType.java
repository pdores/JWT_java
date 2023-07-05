package com.nn.jwt_java;

public interface EventType {

    public static final int Unknown=0;
    public static final int SaleLoadTransaction=1;
    public static final int TransferTransaction=2;
    public static final int ValidationTransaction=3;
    public static final int PersonalizationTransaction=4;
    public static final int ControlTransaction=5;
    public static final int ErrorTransaction=6;
    public static final int HeartbeatTransaction=7;
    public static final int Error=8;
    public static final int ShiftStart=9;
    public static final int ShiftEnd=10;
    public static final int ShiftPauseStart=11;
    public static final int ShiftPauseEnd=12;

}

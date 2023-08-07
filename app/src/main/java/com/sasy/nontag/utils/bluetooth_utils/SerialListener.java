package com.sasy.nontag.utils.bluetooth_utils;

import java.util.ArrayDeque;

public interface SerialListener {
    void onSerialConnect      ();
    void onSerialConnectError (Exception e);
    void onSerialRead         (byte[] data);
    void onSerialRead         (ArrayDeque<byte[]> datas);
    void onSerialIoError      (Exception e);
}

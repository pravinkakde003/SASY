package com.sasy.nontag.utils.bluetooth_utils;

import java.util.ArrayDeque;

public interface SerialListener {
    void onSerialConnect      ();
    void onSerialConnectError (Exception e);
    void onSerialRead         (byte[] data);                // socket -> service
    void onSerialRead         (ArrayDeque<byte[]> datas);   // service -> UI thread
    void onSerialIoError      (Exception e);
}

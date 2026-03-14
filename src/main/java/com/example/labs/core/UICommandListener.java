package com.example.labs.core;

public interface UICommandListener {
    void onToggleTimer();
    boolean isTimerVisible();
    String getTimerState();
    public boolean isFileNull();
}

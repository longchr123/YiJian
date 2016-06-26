package com.example.administrator.kaoyan.bean;

import android.content.ContentValues;

/**
 * Created by Administrator on 2016/5/4.
 */
public class MyEventBus {
    private int  testId;

    public MyEventBus(int  testId) {
        this.testId = testId;
    }

    public int getValues() {
        return testId;
    }
}

package com.w3engineers.unicef.telemesh.util;

import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TestObserver<T> implements Observer<T> {

    public List<T> observedvalues = new ArrayList<>();

    @Override
    public void onChanged(@Nullable T t) {
        observedvalues.add(t);
    }
}
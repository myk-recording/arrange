package com.example.myk.jzq_mvvm.viewmodel;

import android.databinding.ObservableArrayMap;
import android.databinding.ObservableField;

import com.example.myk.jzq_mvvm.model.Board;

public class JzqViewModel {

    private Board board;
    public final ObservableArrayMap<String,String> cells = new ObservableArrayMap<>();
    public final ObservableField<String> winner = new ObservableField<>();

}

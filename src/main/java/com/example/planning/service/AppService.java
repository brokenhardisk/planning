package com.example.planning.service;


import java.util.List;

public interface AppService<S, T> {
    List<T> getItemList(int page, int limit);

    T addItem(S item);

    T getItemById(Integer id);

    T updateItem(S item, Integer id);

    void removeItem(Integer id);
}

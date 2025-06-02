package ru.dulfi.petservice.service;

import java.util.List;

public interface BaseService<T> {
    T save(T entity);
    T getById(Long id);
    List<T> getAll();
    T update(T entity);
    void deleteByEntity(T entity);
    void deleteAll();
} 
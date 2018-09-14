package com.ckmio.client;

@FunctionalInterface
public interface Handler<T>{
    public void handle(T a);
}
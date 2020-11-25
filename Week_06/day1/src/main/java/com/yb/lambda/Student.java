package com.yb.lambda;

import java.io.Serializable;

public interface Student<T extends Serializable&Comparable> {
    public T getValue();
}

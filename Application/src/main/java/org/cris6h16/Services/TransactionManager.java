package org.cris6h16.Services;

public interface TransactionManager {
    void readCommitted(Runnable runnable);

    void repeatableRead(Runnable runnable);

    void serializable(Runnable runnable);

    void readUncommitted(Runnable runnable);
}

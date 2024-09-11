package org.cris6h16.Services;

public interface TransactionManager {
    /**
     * Execute a runnable Synchronously in a transaction
     * @param isolationLevel
     * @param runnable
     */
    public void executeInTransaction(EIsolationLevel isolationLevel, Runnable runnable);

    public void commit();

    public void rollback();
}

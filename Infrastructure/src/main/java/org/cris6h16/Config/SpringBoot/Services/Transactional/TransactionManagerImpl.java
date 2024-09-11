package org.cris6h16.Config.SpringBoot.Services.Transactional;

import org.cris6h16.Services.EIsolationLevel;
import org.cris6h16.Services.TransactionManager;
import org.springframework.stereotype.Component;

@Component
public class TransactionManagerImpl implements TransactionManager {

    private final TransactionService transactionService;

    public TransactionManagerImpl() {
        this.transactionService = new TransactionService();
    }

    @Override
    public void executeInTransaction(EIsolationLevel isolationLevel, Runnable runnable) {
        switch (isolationLevel) {
            case READ_COMMITTED:
                transactionService.readCommitted(runnable);
                break;
            case REPEATABLE_READ:
                transactionService.repeatableRead(runnable);
                break;
            case SERIALIZABLE:
                transactionService.serializable(runnable);
                break;
            case READ_UNCOMMITTED:
                transactionService.readUncommitted(runnable);
                break;
        }
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

}

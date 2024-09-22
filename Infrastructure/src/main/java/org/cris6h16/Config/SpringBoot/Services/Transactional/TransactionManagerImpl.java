package org.cris6h16.Config.SpringBoot.Services.Transactional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionManagerImpl implements TransactionManager {


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void readCommitted(Runnable runnable) {
        runnable.run();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void repeatableRead(Runnable runnable) {
        runnable.run();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void serializable(Runnable runnable) {
        runnable.run();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void readUncommitted(Runnable runnable) {
        runnable.run();
    }

}

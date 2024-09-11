package org.cris6h16.Config.SpringBoot.Services.Transactional;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


class TransactionService {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    protected void readCommitted(Runnable runnable) {
        runnable.run();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    protected void repeatableRead(Runnable runnable) {
        runnable.run();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    protected void serializable(Runnable runnable) {
        runnable.run();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    protected void readUncommitted(Runnable runnable) {
        runnable.run();
    }
}

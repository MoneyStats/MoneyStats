package com.moneystats.MoneyStats.commStats.statement;

import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IStatementDAO extends JpaRepository<StatementEntity, Long> {

    /**
     * Used on WalletService method deleteWalletEntity cause it need to be removed before to remove the Wallet
     * @param idWallet
     * @return a List of statementEntity by a specific WalletID
     */
    List<StatementEntity> findStatementByWalletId(Long idWallet);
}

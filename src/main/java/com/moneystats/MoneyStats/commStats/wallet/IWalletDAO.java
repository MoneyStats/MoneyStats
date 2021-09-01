package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWalletDAO extends JpaRepository<WalletEntity, Long> {

  List<WalletEntity> findWalletsByCategoryId(Long id);

  /**
   * Used on WalletService method getAll
   * @param userId
   * @return all the wallet by the userId provided
   */
  List<WalletEntity> findAllByUserId(Long userId);

  WalletEntity findWalletsById(Long id);
}

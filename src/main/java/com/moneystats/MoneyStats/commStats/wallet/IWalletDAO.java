package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWalletDAO extends JpaRepository<WalletEntity, Long> {

  /**
   * not used
   *
   * @param id
   * @return
   */
  List<WalletEntity> findWalletsByCategoryId(Long id);

  /**
   * Used on WalletService method getAll
   *
   * @param userId
   * @return all the wallet by the userId provided
   */
  List<WalletEntity> findAllByUserId(Long userId);

  /**
   * not used
   *
   * @param id
   * @return
   */
  WalletEntity findWalletsById(Long id);
}

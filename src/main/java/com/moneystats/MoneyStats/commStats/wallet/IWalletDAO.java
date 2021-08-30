package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IWalletDAO extends JpaRepository<WalletEntity, Long> {

  List<WalletEntity> findWalletsByCategoryId(Long id);

  List<WalletEntity> findAllByUserId(Long userId);

  WalletEntity findWalletsById(Long id);
}

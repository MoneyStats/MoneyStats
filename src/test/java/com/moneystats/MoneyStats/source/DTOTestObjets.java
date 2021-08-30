package com.moneystats.MoneyStats.source;

import com.moneystats.MoneyStats.commStats.category.DTO.CategoryDTO;
import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;

import java.util.List;

public class DTOTestObjets {
  public static List<CategoryDTO> categoryDTOList =
      List.of(
          new CategoryDTO("Credit Card"),
          new CategoryDTO("Debit Card"),
          new CategoryDTO("Cash"),
          new CategoryDTO("Investment"));

  public static List<CategoryEntity> categoryEntities =
      List.of(
          new CategoryEntity(1, "Credit Card"),
          new CategoryEntity(2, "Debit Card"),
          new CategoryEntity(3, "Cash"),
          new CategoryEntity(4, "Investment"));

  public static List<WalletDTO> walletDTOS =
      List.of(
          new WalletDTO("My-Wallet-Name", new CategoryEntity(1, "Credit Card"), null, null),
          new WalletDTO("My-Wallet-Name1", new CategoryEntity(1, "Credit Card"), null, null),
          new WalletDTO("My-Wallet-Name2", new CategoryEntity(1, "Credit Card"), null, null),
          new WalletDTO("My-Wallet-Name3", new CategoryEntity(1, "Credit Card"), null, null));
  public static List<WalletEntity> walletEntities =
      List.of(
          new WalletEntity(1L, "My-Wallet-Name", new CategoryEntity(1, "Credit Card"), null, null),
          new WalletEntity(2L, "My-Wallet-Name1", new CategoryEntity(1, "Credit Card"), null, null),
          new WalletEntity(3L, "My-Wallet-Name2", new CategoryEntity(1, "Credit Card"), null, null),
          new WalletEntity(4L, "My-Wallet-Name3", new CategoryEntity(1, "Credit Card"), null, null));

  public static WalletDTO walletDTO =
      new WalletDTO("My-Wallet-Name", new CategoryEntity(1, "Credit Card"), null, null);
}

package com.moneystats.MoneyStats.source;

import com.moneystats.MoneyStats.commStats.category.DTO.CategoryDTO;
import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementInputDTO;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletInputDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletInputIdDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletStatementDTO;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.authentication.entity.AuthCredentialEntity;

import java.util.ArrayList;
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

  public static List<StatementEntity> statementList = new ArrayList<>();

  public static AuthCredentialEntity authCredentialEntity =
      new AuthCredentialEntity(
          "my-firstName",
          "my-lastName",
          "my-dateOfBirth",
          "my-email",
          "my-username",
          "my-password",
          SecurityRoles.MONEYSTATS_USER_ROLE);

  public static List<WalletDTO> walletDTOS =
      List.of(
          new WalletDTO(
              "My-Wallet-Name",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList),
          new WalletDTO(
              "My-Wallet-Name1",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList),
          new WalletDTO(
              "My-Wallet-Name2",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList),
          new WalletDTO(
              "My-Wallet-Name3",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList));

  public static List<WalletEntity> walletEntities =
      List.of(
          new WalletEntity(
              1L,
              "My-Wallet-Name",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList),
          new WalletEntity(
              2L,
              "My-Wallet-Name1",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList),
          new WalletEntity(
              3L,
              "My-Wallet-Name2",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList),
          new WalletEntity(
              4L,
              "My-Wallet-Name3",
              new CategoryEntity(1, "Credit Card"),
              authCredentialEntity,
              statementList));

  public static WalletDTO walletDTO =
      new WalletDTO(
          "My-Wallet-Name",
          new CategoryEntity(1, "Credit Card"),
          authCredentialEntity,
          statementList);
  public static WalletInputDTO walletInputDTO = new WalletInputDTO("My-Wallet-Name", 1);

  public static CategoryEntity categoryEntity = new CategoryEntity(1, "my-category-name");

  public static WalletEntity walletEntity = new WalletEntity("my-wallet-name", categoryEntity, authCredentialEntity, null);

  public static List<StatementEntity> statementEntityList =
      List.of(new StatementEntity("my-date", 1.00, authCredentialEntity, walletEntity),
              new StatementEntity("my-date", 1.00, authCredentialEntity, walletEntities.get(1)),
              new StatementEntity("my-date", 1.00, authCredentialEntity, walletEntities.get(2)),
              new StatementEntity("my-date", 1.00, authCredentialEntity, walletEntities.get(3)));

  public static StatementDTO statementDTO =
      new StatementDTO("01-01-2021", 10.0, authCredentialEntity, walletEntities.get(0));

  public static StatementInputDTO statementInputDTO = new StatementInputDTO(10.0, "01-01-2021", 1L);


  public static WalletStatementDTO walletStatementDTO = new WalletStatementDTO(walletEntities, statementEntityList);

  public static WalletInputIdDTO walletInputIdDTO = new WalletInputIdDTO(1L, "My-Wallet-Name", 1);

  public static List<String> listDate = List.of("01-01-2021", "02-01-2021");

  public static StatementEntity statementEntity = new StatementEntity(listDate.get(0), 10.00, authCredentialEntity, walletEntity);

}

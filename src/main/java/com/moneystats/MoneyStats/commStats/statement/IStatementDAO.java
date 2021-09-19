package com.moneystats.MoneyStats.commStats.statement;

import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStatementDAO extends JpaRepository<StatementEntity, Long> {

  /**
   * Used on {@link com.moneystats.MoneyStats.commStats.wallet.WalletService} method
   * deleteWalletEntity cause it need to be removed before to remove the Wallet
   *
   * @param idWallet
   * @return a List of statementEntity by a specific WalletID
   */
  List<StatementEntity> findStatementByWalletId(Long idWallet);

  /**
   * Used on {@link StatementService}, method List<String> listOfDate(TokenDTO tokenDTO) to get all
   * the Date ordered and just one time
   *
   * @param userId
   * @return a List od unique and not duplicate date
   */
  @Query(
      value =
          "select distinct statements.date from StatementEntity statements where statements.user.id = :userId")
  List<String> selectdistinctstatement(Long userId);

  /**
   * Used on {@link StatementService}, method public List<StatementEntity>
   * listStatementByDate(TokenDTO tokenDTO, String date) to get all the Statement by userID and
   * Date,all in order of walletId
   *
   * @param userId
   * @param date
   * @return List of {@link StatementEntity}
   */
  List<StatementEntity> findAllByUserIdAndDateOrderByWalletId(Long userId, String date);

  /**
   * Used on {@link StatementService}, method public List<String> listByWalletAndValue(TokenDTO
   * tokenDTO)
   *
   * @param userId
   * @return List of statements ordered by Date
   */
  // @Query(
  //    value =
  //        "select statements.date, group_concat(statements.wallet.id) as wallet,
  // group_concat(statements.value) from StatementEntity statements "
  //            + "where statements.user.id = :userId group by statements.date")
  // List<String> findStatementByDateOrdered(Long userId);
}

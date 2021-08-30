package com.moneystats.MoneyStats.walletTest;

import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.WalletService;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class WalletServiceTest {

  @Mock private IWalletDAO walletDAO;
  @InjectMocks private WalletService walletService;
  @InjectMocks private TokenService tokenService;

  @Test
  void test_walletDTOlist_shouldReturnTheList() throws Exception {
    List<WalletDTO> walletDTO = DTOTestObjets.walletDTOS;
    List<WalletEntity> walletEntities = DTOTestObjets.walletEntities;

    Mockito.when(walletDAO.findAll()).thenReturn(walletEntities);

    Mockito.when(tokenService.parseToken(TestSchema.TOKEN_JWT_DTO_ROLE_USER))
        .thenReturn(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER);

    Mockito.when(walletService.getAll(TestSchema.TOKEN_JWT_DTO_ROLE_USER)).thenReturn(walletDTO);

    List<WalletDTO> actual = walletService.getAll(TestSchema.TOKEN_JWT_DTO_ROLE_USER);
    for (int i = 0; i < actual.size(); i++) {
      Assertions.assertEquals(walletDTO.get(i), actual.get(i));
    }
  }
}

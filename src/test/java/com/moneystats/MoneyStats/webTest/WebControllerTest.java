package com.moneystats.MoneyStats.webTest;

import com.moneystats.MoneyStats.web.WebController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = WebController.class)
public class WebControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private WebController webController;

  /**
   * Test FirstPage
   *
   * @throws Exception
   */
  @Test
  public void testFirstPage_shouldReturnLoginPage() throws Exception {
    String expectedPath = "loginPage.html";

    String actual = webController.index();

    mockMvc
        .perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.status().isOk());

    Assertions.assertEquals(expectedPath, actual);
  }

  /**
   * Test Logout
   *
   * @throws Exception
   */
  @Test
  public void testLogout_shouldReturnLoginPage() throws Exception {
    String expectedPath = "loginPage.html";

    String actual = webController.logout();

    mockMvc
        .perform(MockMvcRequestBuilders.get("/logout"))
        .andExpect(MockMvcResultMatchers.status().isOk());

    Assertions.assertEquals(expectedPath, actual);
  }
}

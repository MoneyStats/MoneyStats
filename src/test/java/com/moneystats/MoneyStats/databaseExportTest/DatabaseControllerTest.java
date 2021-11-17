package com.moneystats.MoneyStats.databaseExportTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementResponseDTO;
import com.moneystats.MoneyStats.commStats.statement.StatementController;
import com.moneystats.MoneyStats.commStats.statement.StatementService;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommand;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommandDTO;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseResponseDTO;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseController;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseService;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplatePlaceholders;
import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.ResponseMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = DatabaseController.class)
public class DatabaseControllerTest {

    @MockBean
    private DatabaseService databaseService;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test backup database
     *
     * @throws Exception
     */
    @Test
    public void testBackupDatabase() throws Exception {
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        DatabaseResponseDTO databaseResponseDTO =
                new DatabaseResponseDTO(DatabaseResponseDTO.String.EXPORTED);
        String responseAsString = objectMapper.writeValueAsString(databaseResponseDTO);

        DatabaseCommandDTO databaseCommandDTO = new DatabaseCommandDTO(
                TemplatePlaceholders.FILEPATH_BACKUP,
                DatabaseCommand.EXPORT_DUMP_COMMAND,
                SecurityRoles.MONEYSTATS_ADMIN_ROLE);
        String databaseAsString = objectMapper.writeValueAsString(databaseCommandDTO);

        Mockito.when(databaseService.backupDatabase(Mockito.any(), Mockito.any()))
                .thenReturn(databaseResponseDTO);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/database/exportDatabase")
                                .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(databaseAsString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(responseAsString));
    }

    /**
     * Test backup database
     *
     * @throws Exception
     */
    @Test
    public void testRestoreDatabase() throws Exception {
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        DatabaseResponseDTO databaseResponseDTO =
                new DatabaseResponseDTO(DatabaseResponseDTO.String.IMPORTED);
        String responseAsString = objectMapper.writeValueAsString(databaseResponseDTO);

        DatabaseCommandDTO databaseCommandDTO = new DatabaseCommandDTO(
                TemplatePlaceholders.FILEPATH_BACKUP,
                DatabaseCommand.IMPORT_DUMP_COMMAND,
                SecurityRoles.MONEYSTATS_ADMIN_ROLE);
        String databaseAsString = objectMapper.writeValueAsString(databaseCommandDTO);

        Mockito.when(databaseService.backupDatabase(Mockito.any(), Mockito.any()))
                .thenReturn(databaseResponseDTO);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/database/importDatabase")
                                .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(databaseAsString))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

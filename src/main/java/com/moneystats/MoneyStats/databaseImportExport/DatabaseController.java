package com.moneystats.MoneyStats.databaseImportExport;

import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommandDTO;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseJSONExportDTO;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseResponseDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.DTO.TemplateDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateException;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.generic.SchemaDescription;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/database")
@OpenAPIDefinition(tags = {@Tag(name = "Database", description = "")})
public class DatabaseController {

  @Autowired private DatabaseService databaseService;

  @PostMapping("/exportDatabase")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.POST_EXPORT_DATABASE_SUMMARY,
      description = SchemaDescription.POST_EXPORT_DATABASE_DESCRIPTION,
      tags = "Database")
  public DatabaseJSONExportDTO exportDatabase(
      @RequestHeader(value = "Authorization") String jwt,
      @RequestBody DatabaseCommandDTO databaseCommandDTO)
      throws AuthenticationException, DatabaseException, TemplateException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return databaseService.backupDatabase(databaseCommandDTO, tokenDTO);
  }

  @PostMapping("/importDatabase")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.POST_IMPORT_DATABASE_SUMMARY,
      description = SchemaDescription.POST_IMPORT_DATABASE_DESCRIPTION,
      tags = "Database")
  public DatabaseResponseDTO importDatabase(
      @RequestHeader(value = "Authorization") String jwt,
      @RequestBody DatabaseCommandDTO databaseCommandDTO)
      throws AuthenticationException, DatabaseException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return databaseService.restoreDatabase(databaseCommandDTO, tokenDTO);
  }

  /*@GetMapping("/getBackupFolder")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.GET_FOLDER_DATABASE_SUMMARY,
      description = SchemaDescription.GET_FOLDER_DATABASE_DESCRIPTION,
      tags = "Database")
  public List<String> getBackupFolder() throws DatabaseException {
    return databaseService.getFolderDatabase();
  }*/
}

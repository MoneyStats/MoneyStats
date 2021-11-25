package com.moneystats.MoneyStats.databaseImportExport.template;

import com.moneystats.MoneyStats.databaseImportExport.template.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITemplateDAO extends JpaRepository<TemplateEntity, Long> {

    /**
     * Get The Template for the Backup
     * @param identifier
     * @return
     */
    TemplateEntity findTemplateEntityByIdentifier(String identifier);
}

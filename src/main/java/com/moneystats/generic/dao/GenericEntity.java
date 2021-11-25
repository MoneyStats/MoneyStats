package com.moneystats.generic.dao;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class GenericEntity<K extends Serializable> {

  public abstract K getId();

  @Deprecated
  public abstract void setId(K id);

  @Column(name = "CREATION_DATE", updatable = false)
  private LocalDateTime creationDate;

  @Column(name = "UPDATE_DATE")
  private LocalDateTime updateDate;

  public GenericEntity(LocalDateTime creationDate, LocalDateTime updateDate) {
    this.creationDate = creationDate;
    this.updateDate = updateDate;
  }

  public GenericEntity() {}

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public LocalDateTime getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(LocalDateTime updateDate) {
    this.updateDate = updateDate;
  }

  @PrePersist
  public void creationDates() {
    LocalDateTime now = LocalDateTime.now();
    setCreationDate(now);
    setUpdateDate(now);
  }

  @PreUpdate
  public void updateDates() {
    setUpdateDate(LocalDateTime.now());
  }
}

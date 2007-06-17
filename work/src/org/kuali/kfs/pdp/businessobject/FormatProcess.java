/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * @author delyea
 * 
 * @hibernate.class  table="PDP.PDP_FMT_PROC_T"
 */

public class FormatProcess implements Serializable,PersistenceBrokerAware {

  private String physicalCampusProcessCode; // PHYS_CMP_PROC_CD       
  private Timestamp beginFormat;            // BEG_FMT_TS
  private Timestamp lastUpdate;
  private Integer version;                  //  VER_NBR

  public FormatProcess() {
    super();
  }

  public Timestamp getBeginFormat() {
    return beginFormat;
  }

  public void setBeginFormat(Timestamp beginFormat) {
    this.beginFormat = beginFormat;
  }

  /** 
   * @return
   * @hibernate.version column="VER_NBR" not-null="true"
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * @return
   * @hibernate.id column="PHYS_CMP_PROC_CD" length="2"  generator-class="assigned"
   */
  public String getPhysicalCampusProcessCode() {
    return physicalCampusProcessCode;
  }

  /**
   * @param string
   */
  public void setPhysicalCampusProcessCode(String string) {
    physicalCampusProcessCode = string;
  }

  /**
   * @param integer
   */
  public void setVersion(Integer integer) {
    version = integer;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof FormatProcess) ) { return false; }
    FormatProcess o = (FormatProcess)obj;
    return new EqualsBuilder()
    .append(physicalCampusProcessCode, o.getPhysicalCampusProcessCode())
    .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder(83,91)
      .append(physicalCampusProcessCode)
      .toHashCode();
  }

  public String toString() {
    return new ToStringBuilder(this)
      .append("physicalCampusProcessCode",  this.physicalCampusProcessCode)
      .toString();
  }

  /**
   * @return Returns the lastUpdate.
   */
  public Timestamp getLastUpdate() {
    return lastUpdate;
  }
  /**
   * @param lastUpdate The lastUpdate to set.
   */
  public void setLastUpdate(Timestamp lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void beforeInsert(PersistenceBroker broker) throws PersistenceBrokerException {
    lastUpdate = new Timestamp( (new Date()).getTime() );
  }

  public void afterInsert(PersistenceBroker broker) throws PersistenceBrokerException {

  }

  public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
    lastUpdate = new Timestamp( (new Date()).getTime() );    
  }

  public void afterUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
    
  }

  public void beforeDelete(PersistenceBroker broker) throws PersistenceBrokerException {

  }
  
  public void afterDelete(PersistenceBroker broker) throws PersistenceBrokerException {

  }

  public void afterLookup(PersistenceBroker broker) throws PersistenceBrokerException {

  }
}

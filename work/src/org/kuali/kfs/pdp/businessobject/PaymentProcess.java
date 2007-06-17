/*
 * Created on Aug 19, 2004
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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;

/**
 * @author jsissom
 *
 */
public class PaymentProcess implements UserRequired,Serializable,PersistenceBrokerAware {
  private Integer id;
  private Timestamp processTimestamp;
  private String campus;
  private String processUserId;
  private PdpUser processUser;
  private Timestamp lastUpdate;
  private Integer version;

  public PaymentProcess() {
    super();
  }

  public void updateUser(UniversalUserService userService) throws UserNotFoundException {
      UniversalUser u = userService.getUniversalUser(processUserId);
      if ( u == null ) {
          setProcessUser(null);
      } else {
          setProcessUser(new PdpUser(u));
      }
  }

  public String getCampus() {
    return campus;
  }
  public void setCampus(String campus) {
    this.campus = campus;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Timestamp getProcessTimestamp() {
    return processTimestamp;
  }

  public void setProcessTimestamp(Timestamp processTimestamp) {
    this.processTimestamp = processTimestamp;
  }

  public PdpUser getProcessUser() {
    return processUser;
  }

  public void setProcessUser(PdpUser processUser) {
    if ( processUser != null ) {
      processUserId = processUser.getPersonUniversalIdentifier();
    }
    this.processUser = processUser;
  }

  public String getProcessUserId() {
    return processUserId;
  }
  public void setProcessUserId(String processUserId) {
    this.processUserId = processUserId;
  }
  public Integer getVersion() {
    return version;
  }
  public void setVersion(Integer version) {
    this.version = version;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof PaymentProcess) ) { return false; }
    PaymentProcess tc = (PaymentProcess)obj;
    return new EqualsBuilder()
    .append(id, tc.getId())
    .isEquals();
  }
  
  public int hashCode() {
    return new HashCodeBuilder(67,3)
      .append(id)
      .toHashCode();
  }

  public String toString() {
    return new ToStringBuilder(this)
      .append("id",  this.id)
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

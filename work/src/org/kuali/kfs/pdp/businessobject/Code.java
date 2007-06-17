/*
 * Created on Jun 29, 2004
 *
 */
package org.kuali.module.pdp.bo;

import java.sql.Timestamp;

/**
 * @author jsissom
 *
 */
public interface Code {
  public String getData();

  public String getCode();
  public void setCode(String code);

  public String getDescription();
  public void setDescription(String description);

  public Timestamp getLastUpdate();
  public void setLastUpdate(Timestamp lastUpdate);

  public PdpUser getLastUpdateUser();
  public void setLastUpdateUser(PdpUser u);

  public Integer getVersion();
  public void setVersion(Integer version);
}

/*
 * Created on Aug 16, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.Date;

import org.kuali.module.pdp.bo.FormatProcess;


/**
 * @author jsissom
 *
 */
public interface FormatProcessDao {
  public FormatProcess getByCampus(String campus);
  public void removeByCampus(String campus);
  public void add(String campus,Date now);
}

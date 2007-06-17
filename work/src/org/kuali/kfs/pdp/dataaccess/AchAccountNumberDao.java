/*
 * Created on Aug 19, 2004
 */
package org.kuali.module.pdp.dao;

import org.kuali.module.pdp.bo.AchAccountNumber;

/**
 * @author HSTAPLET
 */
public interface AchAccountNumberDao {

  public AchAccountNumber get(Integer id);
  
  public void delete(AchAccountNumber achAccountNumber);

}

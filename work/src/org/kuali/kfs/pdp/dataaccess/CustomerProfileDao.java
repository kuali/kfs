/*
 * Created on Jul 7, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;

import org.kuali.module.pdp.bo.CustomerBank;
import org.kuali.module.pdp.bo.CustomerProfile;


/**
 * @author jsissom
 *
 */
public interface CustomerProfileDao {
  public List getAll();
  public CustomerProfile get(Integer custId);
  public CustomerProfile get(String chartCode, String orgCode, String subUnitCode);
  public void save(CustomerProfile cp);
  public void saveCustomerBank(CustomerBank cb);
  public void deleteCustomerBank(CustomerBank cb);
}

/*
 * Created on Jul 7, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.CustomerBank;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.dao.CustomerProfileDao;
import org.kuali.module.pdp.service.CustomerProfileService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jsissom
 *
 */
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileServiceImpl.class);

  private CustomerProfileDao customerProfileDao;

  public List getAll() {
    return customerProfileDao.getAll();
  }

  public void setCustomerProfileDao(CustomerProfileDao c) {
    customerProfileDao = c;
  }

  public CustomerProfile get(Integer id) {
    return customerProfileDao.get(id);
  }

  public CustomerProfile get(String chartCode, String orgCode, String subUnitCode) {
    return customerProfileDao.get(chartCode,orgCode,subUnitCode);
  }

  public void save(CustomerProfile cp) {
    customerProfileDao.save(cp);
  }

  public void saveCustomerBank(CustomerBank cb) {
    customerProfileDao.saveCustomerBank(cb);
  }

  public void deleteCustomerBank(CustomerBank cb) {
    customerProfileDao.deleteCustomerBank(cb);
  }
}

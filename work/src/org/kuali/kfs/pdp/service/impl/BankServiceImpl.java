/*
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.service.impl;

import java.util.List;

import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.dao.BankDao;
import org.kuali.module.pdp.service.BankService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author delyea
 *
 */
@Transactional
public class BankServiceImpl implements BankService{
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankServiceImpl.class);

  private BankDao bankDao;

  public void setBankDao(BankDao c) {
    bankDao = c;
  }

  public List getAll() {
    return bankDao.getAll();
  }

  public Bank get(Integer bankId) {
    return bankDao.get(bankId);
  }

  public List getAllBanksForDisbursementType(String type) {
    return bankDao.getAllBanksForDisbursementType(type);
  }

  public void save(Bank b) {
    bankDao.save(b);
  }

}

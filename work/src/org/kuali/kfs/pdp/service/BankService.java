/*
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.service;

import java.util.List;

import org.kuali.module.pdp.bo.Bank;


/**
 * @author delyea
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface BankService {
  public List getAll();
  public Bank get(Integer bankId);
  public List getAllBanksForDisbursementType(String type);
  public void save(Bank b);

}

/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;

import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.PdpUser;


/**
 * @author jsissom
 *
 */
public interface ProcessDao {

  public PaymentProcess createProcess(String campusCd,PdpUser processUser);
  public PaymentProcess get(Integer procId);
  public List getMostCurrentProcesses(Integer number);
}

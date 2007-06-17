/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.module.pdp.dao;


/**
 * @author delyea
 *
 */
public interface BatchMaintenanceDao {
  public boolean doBatchPaymentsHaveOpenStatus(Integer batchId);
  public boolean doBatchPaymentsHaveHeldStatus(Integer batchId);
}

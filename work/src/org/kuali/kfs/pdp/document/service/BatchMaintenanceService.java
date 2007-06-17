/*
 * Created on Aug 12, 2004
 */
package org.kuali.module.pdp.service;

import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.exception.PdpException;

/**
 * @author HSTAPLET
 */
public interface BatchMaintenanceService {
    public void cancelPendingBatch(Integer batchId, String note, PdpUser user) throws PdpException;
    public void holdPendingBatch(Integer batchId, String note, PdpUser user) throws PdpException;
    public void removeBatchHold(Integer batchId, String changeText, PdpUser user) throws PdpException;
    public boolean doBatchPaymentsHaveOpenStatus(Integer batchId);
    public boolean doBatchPaymentsHaveOpenOrHeldStatus(Integer id);
    public boolean doBatchPaymentsHaveHeldStatus(Integer id);
}

/*
 * Created on Aug 12, 2004
 */
package org.kuali.module.pdp.service;

import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.exception.PdpException;


/**
 * @author HSTAPLET
 */
public interface PaymentMaintenanceService {
    public void cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, PdpUser u, SecurityRecord sr) throws PdpException; 

    public void holdPendingPayment(Integer paymentGroupId, String note, PdpUser user) throws PdpException;
  
    public void removeHoldPendingPayment(Integer paymentGroupId, String note, PdpUser user, SecurityRecord sr) throws PdpException;

    public void cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, PdpUser user) throws PdpException;

    public void cancelReissueDisbursement(Integer paymentGroupId, String changeText, PdpUser user) throws PdpException;

    public void changeImmediateFlag(Integer paymentGroupId, String changeText, PdpUser user) throws PdpException;
}

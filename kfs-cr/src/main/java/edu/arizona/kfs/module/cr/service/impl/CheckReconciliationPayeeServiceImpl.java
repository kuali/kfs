package edu.arizona.kfs.module.cr.service.impl;

import java.util.Collection;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cr.CrConstants;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;
import edu.arizona.kfs.module.cr.dataaccess.CheckReconciliationDao;
import edu.arizona.kfs.module.cr.service.CheckReconciliationPayeeService;

public class CheckReconciliationPayeeServiceImpl implements CheckReconciliationPayeeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationPayeeService.class);

    private CheckReconciliationDao checkReconciliationDao;

    // Spring Injectors

    public void setCheckReconciliationDao(CheckReconciliationDao checkReconciliationDao) {
        this.checkReconciliationDao = checkReconciliationDao;
    }

    // Implemented Methods

    @Override
    public String getCheckPayeeName(CheckReconciliation checkReconciliation) {
        if (CrConstants.CheckReconciliationSourceCodes.PDP_SRC.equals(checkReconciliation.getSourceCode())) {
            PaymentGroup paymentGroup = getPaymentGroupForCheck(checkReconciliation);
            if (ObjectUtils.isNotNull(paymentGroup)) {
                return paymentGroup.getPayeeName();
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Check number " + checkReconciliation.getCheckNumber() + " was not from PDP.");
        }
        // if the Check recon wasn't from PDP, then we just return null
        return null;
    }

    // Private Methods

    private PaymentGroup getPaymentGroupForCheck(CheckReconciliation checkReconciliation) {

        Collection<PaymentGroup> paymentGroups = checkReconciliationDao.getPdpPaymentGroups(checkReconciliation);
        if (!paymentGroups.isEmpty()) {
            for (PaymentGroup paymentGroup : paymentGroups) {
                if (ObjectUtils.isNotNull(paymentGroup)) {
                    return paymentGroup;
                }
            }
        }

        // if we didn't find a payment group, maybe the check was cancelled/reissued.
        // need to look into payment group history to see if there was a matching payment that used to contain the check number that we want
        Collection<PaymentGroupHistory> paymentGroupHistories = checkReconciliationDao.getPdpPaymentGroupHistories(checkReconciliation);
        if (!paymentGroupHistories.isEmpty()) {
            for (PaymentGroupHistory paymentGroupHistory : paymentGroupHistories) {
                PaymentGroup paymentGroup = paymentGroupHistory.getPaymentGroup();
                if (ObjectUtils.isNotNull(paymentGroup)) {
                    return paymentGroup;
                }
            }
        }

        // didn't find anything, so return null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Check number " + checkReconciliation.getCheckNumber() + " did not have a Payment Group.");
        }
        return null;
    }

}

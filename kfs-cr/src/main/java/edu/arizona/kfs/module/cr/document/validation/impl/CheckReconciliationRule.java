package edu.arizona.kfs.module.cr.document.validation.impl;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.module.cr.CrConstants;
import edu.arizona.kfs.module.cr.CrKeyConstants;
import edu.arizona.kfs.module.cr.CrPropertyConstants;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;

/**
 * Business rule(s) applicable to Check Reconciliation documents.
 * 
 * Deprecation: Eclipse identifies org.kuali.rice.kns.document.MaintenanceDocument and org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase as deprecated.
 */

@SuppressWarnings("deprecation")
public class CheckReconciliationRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationRule.class);

    private CheckReconciliation oldCheckReconciliation;
    private CheckReconciliation newCheckReconciliation;

    public CheckReconciliationRule() {
    }

    @Override
    public void setupConvenienceObjects() {
        oldCheckReconciliation = (CheckReconciliation) super.getOldBo();
        newCheckReconciliation = (CheckReconciliation) super.getNewBo();
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        setupConvenienceObjects();
        processCustomRouteDocumentBusinessRules(document);
        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        boolean valid = true;

        if (newCheckReconciliation.getAmount() == null) {
            putFieldError(CrPropertyConstants.CheckReconciliation.AMOUNT, KFSKeyConstants.ERROR_ZERO_AMOUNT);
            valid = false;
        }
        if (newCheckReconciliation.getAmount().isZero()) {
            putFieldError(CrPropertyConstants.CheckReconciliation.AMOUNT, KFSKeyConstants.ERROR_ZERO_AMOUNT);
            valid = false;
        }
        if (newCheckReconciliation.getAmount().isNegative()) {
            putFieldError(CrPropertyConstants.CheckReconciliation.AMOUNT, KFSKeyConstants.ERROR_NEGATIVE_AMOUNT);
            valid = false;
        }

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CrPropertyConstants.CheckReconciliation.CHECK_NUMBER, newCheckReconciliation.getCheckNumber().toString());
        fieldValues.put(CrPropertyConstants.CheckReconciliation.BANK_ACCOUNT_NUMBER, newCheckReconciliation.getBankAccountNumber());

        Collection<CheckReconciliation> checks = businessObjectService.findMatching(CheckReconciliation.class, fieldValues);

        boolean isEdit = document.isEdit();

        if (checks.size() > 0 && !isEdit) {
            putFieldError(CrPropertyConstants.CheckReconciliation.CHECK_NUMBER, CrKeyConstants.ERROR_CHECKEXISTS, newCheckReconciliation.getCheckNumber().toString());
            valid = false;
        }

        Date statusChangeDate = determineStatusChangeDate();
        newCheckReconciliation.setStatusChangeDate(statusChangeDate);

        return valid;
    }

    /**
     * This method updates the status change date field under the following conditions:
     * 1. If it's a new check reconciliation, the status change date will get set to:
     * - the cleared date if the new status is "cleared" and the user enters a cleared date
     * - the current date if the new status is anything other than "issued"
     * 
     * 2. If it's a current check reconciliation, the status change date will get set to:
     * - the new cleared date if the status is changed from "issued" to "cleared" and the user enters a cleared date
     * - the current date if the status is changed from "issued" to anything other than "cleared",
     * -- or if the status is changed from "issued" to "cleared" and there was no cleared date entered
     * 
     * 3. If it's a current check reconciliation, the status change date will get set to null if it's changed from anything else to "issued".
     * 
     * 4. "anything else" means
     * - if the status is changed, the date is changed to current
     * - otherwise, the status change date is unchanged.
     */
    private Date determineStatusChangeDate() {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());

        boolean isNewCheckReconciliation = isNewCheckReconciliation();
        boolean isOldCheckReconciliationStatusIssued = isOldCheckReconciliationStatusIssued();
        boolean isNewCheckReconciliationStatusIssued = isNewCheckReconciliationStatusIssued();
        boolean isNewCheckReconciliationStatusCleared = isNewCheckReconciliationStatusCleared();
        boolean isNewClearedDate = isNewClearedDate();

        if (isNewCheckReconciliation) {
            if (isNewCheckReconciliationStatusCleared && isNewClearedDate) {
                return newCheckReconciliation.getClearedDate();
            }
            if (!isNewCheckReconciliationStatusIssued) {
                return currentDate;
            }
            return null;
        }

        if (isOldCheckReconciliationStatusIssued) {
            if (isNewCheckReconciliationStatusCleared && isNewClearedDate) {
                return newCheckReconciliation.getClearedDate();
            }
            return currentDate;
        }

        if (!isOldCheckReconciliationStatusIssued && isNewCheckReconciliationStatusIssued) {
            return null;
        }

        if (!newCheckReconciliation.getStatus().equals(oldCheckReconciliation.getStatus())) {
            return currentDate;
        }
        return newCheckReconciliation.getStatusChangeDate();
    }

    private boolean isNewCheckReconciliation() {
        if (oldCheckReconciliation == null) {
            return true;
        }
        if (oldCheckReconciliation.getId() == null) {
            return true;
        }
        return false;
    }

    private boolean isOldCheckReconciliationStatusIssued() {
        if (oldCheckReconciliation == null) {
            return false;
        }
        if (oldCheckReconciliation.getStatus() == null) {
            return false;
        }
        if (oldCheckReconciliation.getStatus().equals(CrConstants.CheckReconciliationStatusCodes.ISSUED)) {
            return true;
        }
        return false;
    }

    private boolean isNewCheckReconciliationStatusIssued() {
        if (newCheckReconciliation == null) {
            return false;
        }
        if (newCheckReconciliation.getStatus() == null) {
            return false;
        }
        if (newCheckReconciliation.getStatus().equals(CrConstants.CheckReconciliationStatusCodes.ISSUED)) {
            return true;
        }
        return false;
    }

    private boolean isNewCheckReconciliationStatusCleared() {
        if (newCheckReconciliation == null) {
            return false;
        }
        if (newCheckReconciliation.getStatus() == null) {
            return false;
        }
        if (newCheckReconciliation.getStatus().equals(CrConstants.CheckReconciliationStatusCodes.CLEARED)) {
            return true;
        }
        return false;
    }

    private boolean isNewClearedDate() {
        if (newCheckReconciliation == null) {
            return false;
        }
        if (newCheckReconciliation.getClearedDate() != null) {
            return true;
        }
        return false;
    }
}

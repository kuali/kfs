/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.validation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.document.service.PredeterminedBillingScheduleMaintenanceService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the PredeterminedBillingSchedule maintenance document.
 */
public class PredeterminedBillingScheduleRule extends KfsMaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(PredeterminedBillingScheduleRule.class);
    protected PredeterminedBillingSchedule newPredeterminedBillingScheduleCopy;

    private static volatile PredeterminedBillingScheduleMaintenanceService predeterminedBillingScheduleMaintenanceService;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        LOG.debug("Entering PredeterminedBillingScheduleRule.processCustomAddCollectionLineBusinessRules");
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        isValid &= checkForDuplicateBillNumber(collectionName, line);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();
        //for some reason a duplicate message is added to the error map
        GlobalVariables.getMessageMap().removeAllErrorMessagesForProperty("document.newMaintainableObject." + ArPropertyConstants.PredeterminedBillingScheduleFields.ESTIMATED_AMOUNT);
        LOG.info("Leaving PredeterminedBillingScheduleRule.processCustomAddCollectionLineBusinessRules");
        return isValid;
    }

    /**
     * Check to see if a Bill with the same bill number already exists.
     *
     * @param collectionName name of the collection being added to
     * @param line PersistableBusinessObject being added to the collection
     * @return true if there isn't already a bill with the same bill number, false otherwise
     */
    private boolean checkForDuplicateBillNumber(String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;

        if (StringUtils.equalsIgnoreCase(collectionName, ArConstants.BILL_SECTION)) {
            Bill bill = (Bill) line;
            Long newBillNumber = bill.getBillNumber();

            for (Bill existingBill: newPredeterminedBillingScheduleCopy.getBills()) {
                if (existingBill.getBillNumber().equals(newBillNumber)) {
                    isValid = false;
                    putFieldError(collectionName, ArKeyConstants.ERROR_DUPLICATE_BILL_NUMBER);
                    break;
                }
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering PredeterminedBillingScheduleRule.processCustomSaveDocumentBusinessRules");
        processCustomRouteDocumentBusinessRules(document);
        LOG.info("Leaving PredeterminedBillingScheduleRule.processCustomSaveDocumentBusinessRules");
        return true; // save despite error messages
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering PredeterminedBillingScheduleRule.processCustomRouteDocumentBusinessRules");
        boolean success = true;
        success &= checkAwardBillingFrequency();
        success &= checkForDuplicateBillNumbers();
        LOG.info("Leaving PredeterminedBillingScheduleRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    /**
     * checks to see if the billing frequency on the award is Milestone
     *
     * @return
     */
    protected boolean checkAwardBillingFrequency() {
        boolean success = false;

        if (ObjectUtils.isNotNull(newPredeterminedBillingScheduleCopy.getAward().getBillingFrequencyCode())) {
            if (StringUtils.equals(newPredeterminedBillingScheduleCopy.getAward().getBillingFrequencyCode(), ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
                success = true;
            }
        }

        if (!success) {
            putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, ArKeyConstants.ERROR_AWARD_PREDETERMINED_BILLING_SCHEDULE_INCORRECT_BILLING_FREQUENCY, new String[] { newPredeterminedBillingScheduleCopy.getProposalNumber().toString() });
        }

        return success;
    }

    /**
     * Check to see if there is more than one Bill with the same bill number.
     *
     * @return true if there is more than one bill with the same bill number, false otherwise
     */
    private boolean checkForDuplicateBillNumbers() {
        boolean isValid = true;

        Set<Long> billNumbers = new HashSet<>();
        Set<Long> duplicateBillNumbers = new HashSet<>();

        for (Bill bill: newPredeterminedBillingScheduleCopy.getBills()) {
            if (!billNumbers.add(bill.getBillNumber())) {
                duplicateBillNumbers.add(bill.getBillNumber());
            }
        }

        if (duplicateBillNumbers.size() > 0) {
            isValid = false;
            int lineNum = 0;
            for (Bill bill: newPredeterminedBillingScheduleCopy.getBills()) {
                // If the Bill has already been copied to the Invoice, it will be readonly, the user won't have been able to change
                // it and thus we don't need to highlight it as an error if it's a dupe. There will be another dupe in the list that
                // we will highlight.
                boolean copiedToInvoice = false;
                if (ObjectUtils.isNotNull(bill.getBillIdentifier())) {
                    if (getPredeterminedBillingScheduleMaintenanceService().hasBillBeenCopiedToInvoice(bill.getProposalNumber(), bill.getBillIdentifier().toString())) {
                        copiedToInvoice = true;
                    }
                }

                if (!copiedToInvoice) {
                    if (duplicateBillNumbers.contains(bill.getBillNumber())) {
                        String errorPath = ArPropertyConstants.PredeterminedBillingScheduleFields.BILLS + "[" + lineNum + "]." + ArPropertyConstants.BillFields.BILL_NUMBER;
                        putFieldError(errorPath, ArKeyConstants.ERROR_DUPLICATE_BILL_NUMBER);
                    }
                }
                lineNum++;
            }
        }
        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        newPredeterminedBillingScheduleCopy = (PredeterminedBillingSchedule) super.getNewBo();
    }

    public static PredeterminedBillingScheduleMaintenanceService getPredeterminedBillingScheduleMaintenanceService() {
        if (predeterminedBillingScheduleMaintenanceService == null) {
            predeterminedBillingScheduleMaintenanceService = SpringContext.getBean(PredeterminedBillingScheduleMaintenanceService.class);
        }
        return predeterminedBillingScheduleMaintenanceService;
    }
}

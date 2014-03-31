/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.validation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the MilestoneSchedule maintenance document.
 */
public class MilestoneScheduleRule extends KfsMaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(MilestoneScheduleRule.class);
    protected MilestoneSchedule newMilestoneScheduleCopy;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        LOG.debug("Entering PredeterminedBillingScheduleRule.processCustomAddCollectionLineBusinessRules");
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
        isValid &= checkForDuplicateBillNumber(collectionName, line);
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

        if (StringUtils.equalsIgnoreCase(collectionName, ArPropertyConstants.MILESTONES_SECTION)) {
            Milestone milestone = (Milestone) line;
            Long newMilestoneNumber = milestone.getMilestoneNumber();

            for (Milestone existingMilestone: newMilestoneScheduleCopy.getMilestones()) {
                if (existingMilestone.getMilestoneNumber().equals(newMilestoneNumber)) {
                    isValid = false;
                    putFieldError(collectionName, ArKeyConstants.ERROR_DUPLICATE_MILESTONE_NUMBER);
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
        LOG.debug("Entering MilestoneScheduleRule.processCustomSaveDocumentBusinessRules");
        processCustomRouteDocumentBusinessRules(document);
        LOG.info("Leaving MilestoneScheduleRule.processCustomSaveDocumentBusinessRules");
        return true; // save despite error messages
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering MilestoneScheduleRule.processCustomRouteDocumentBusinessRules");
        boolean success = true;
        success &= checkAwardBillingFrequency();
        success &= checkForDuplicateMilestoneNumbers();
        LOG.info("Leaving MilestoneScheduleRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    /**
     * checks to see if the billing frequency on the award is Milestone
     *
     * @return
     */
    protected boolean checkAwardBillingFrequency() {
        boolean success = false;

        if (ObjectUtils.isNotNull(newMilestoneScheduleCopy.getAward().getBillingFrequency())) {
            if (StringUtils.equals(newMilestoneScheduleCopy.getAward().getBillingFrequency().getFrequency(), ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
                success = true;
            }
        }

        if (!success) {
            putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, ArKeyConstants.ERROR_AWARD_MILESTONE_SCHEDULE_INCORRECT_BILLING_FREQUENCY, new String[] { newMilestoneScheduleCopy.getProposalNumber().toString() });
        }

        return success;
    }

    /**
     * Check to see if there is more than one Milestone with the same milestone number.
     *
     * @return true if there is more than one Milestone with the same milestone number, false otherwise
     */
    private boolean checkForDuplicateMilestoneNumbers() {
        boolean isValid = true;

        Set<Long> milestoneNumbers = new HashSet();
        Set<Long> duplicateMilestoneNumbers = new HashSet();

        for (Milestone milestone: newMilestoneScheduleCopy.getMilestones()) {
            if (!milestoneNumbers.add(milestone.getMilestoneNumber())) {
                duplicateMilestoneNumbers.add(milestone.getMilestoneNumber());
            }
        }

        if (duplicateMilestoneNumbers.size() > 0) {
            isValid = false;
            int lineNum = 0;
            for (Milestone milestone: newMilestoneScheduleCopy.getMilestones()) {
                // If the Milestone has already been copied to the Invoice, it will be readonly, the user won't have been able to change
                // it and thus we don't need to highlight it as an error if it's a dupe. There will be another dupe in the list that
                // we will highlight.
                if (!SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).hasMilestoneBeenCopiedToInvoice(milestone.getProposalNumber(), milestone.getMilestoneIdentifier().toString())) {
                    if (duplicateMilestoneNumbers.contains(milestone.getMilestoneNumber())) {
                        String errorPath = ArPropertyConstants.MilestoneScheduleFields.MILESTONES + "[" + lineNum + "]." + ArPropertyConstants.MilestoneFields.MILESTONE_NUMBER;
                        putFieldError(errorPath, ArKeyConstants.ERROR_DUPLICATE_MILESTONE_NUMBER);
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
        newMilestoneScheduleCopy = (MilestoneSchedule) super.getNewBo();
    }

}

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
package org.kuali.kfs.pdp.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpConstants.PayeeIdTypeCodes;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Performs business rules for the Payee ACH Account maintenance document
 */
public class PayeeAchAccountRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeACHAccount.class);

    protected PayeeACHAccount oldPayeeAchAccount;
    protected PayeeACHAccount newPayeeAchAccount;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    public void setupConvenienceObjects() {
        LOG.info("setupConvenienceObjects called");

        // setup oldPayeeAchAccount convenience objects, make sure all possible sub-objects are populated
        oldPayeeAchAccount = (PayeeACHAccount) super.getOldBo();

        // setup newPayeeAchAccount convenience objects, make sure all possible sub-objects are populated
        newPayeeAchAccount = (PayeeACHAccount) super.getNewBo();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");

        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        String payeeIdTypeCode = newPayeeAchAccount.getPayeeIdentifierTypeCode();
        boolean valid = true;
        if (ObjectUtils.isNull(payeeIdTypeCode) || (!StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.EMPLOYEE) && !StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.ENTITY) && !StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.VENDOR_ID))) {
            if (ObjectUtils.isNull(newPayeeAchAccount.getPayeeName())) {
                putFieldError(PdpPropertyConstants.PAYEE_NAME, KFSKeyConstants.ERROR_REQUIRED, PdpConstants.PayeeACHAccountDocumentStrings.PAYEE_NAME + " ( " + PdpConstants.PayeeACHAccountDocumentStrings.PAYEE_NAME + " )");
                valid &= false;
            }
        }
        if (ObjectUtils.isNull(payeeIdTypeCode) || (!StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.EMPLOYEE) && !StringUtils.equalsIgnoreCase(payeeIdTypeCode, PayeeIdTypeCodes.ENTITY))) {
            if (ObjectUtils.isNull(newPayeeAchAccount.getPayeeEmailAddress())) {
                putFieldError(PdpPropertyConstants.PAYEE_EMAIL_ADDRESS, KFSKeyConstants.ERROR_REQUIRED, PdpConstants.PayeeACHAccountDocumentStrings.PAYEE_EMAIL_ADDRESS + " ( " + PdpConstants.PayeeACHAccountDocumentStrings.PAYEE_EMAIL_ADDRESS + " )");
                valid &= false;
            }
        }

        if (!valid) {
            return true;
        }

        // no need to do further checking if user is not even allowed to submit new BO
        if (!checkTransactionTypeAllowed()) {
            return false;
        }

        return checkForDuplicateRecord();
    }

    /**
     * Checks to verify record is not a duplicate for payee id. Do not check for a duplicate record if the following conditions are
     * true 1. editing an existing record (old primary key = new primary key) 2. new PSD code = old PSD code 3. new payee type code
     * = old payee type code 4. depending of the value of payee type code, new correspoding PayeeId = old corresponding PayeeId
     *
     * @return true if record is not duplicate, false otherwise
     */
    protected boolean checkForDuplicateRecord() {
        String newPayeeIdNumber = newPayeeAchAccount.getPayeeIdNumber();
        String newPayeeIdTypeCd = newPayeeAchAccount.getPayeeIdentifierTypeCode();
        String newAchTransactionType = newPayeeAchAccount.getAchTransactionType();

        boolean valid = true;

        if (newPayeeAchAccount.getAchAccountGeneratedIdentifier() != null && oldPayeeAchAccount.getAchAccountGeneratedIdentifier() != null && newPayeeAchAccount.getAchAccountGeneratedIdentifier().equals(oldPayeeAchAccount.getAchAccountGeneratedIdentifier())) {
            if (newPayeeIdTypeCd.equals(oldPayeeAchAccount.getPayeeIdentifierTypeCode()) && newAchTransactionType.equals(oldPayeeAchAccount.getAchTransactionType())) {
                if (newPayeeAchAccount.getPayeeIdNumber().equals(oldPayeeAchAccount.getPayeeIdNumber())) {
                    return valid;
                }
            }
        }

        // check for a duplicate record if creating a new record or editing an old one and above mentioned conditions are not true
        Map<String, Object> criteria = new HashMap<String, Object>();

        criteria.put(PdpPropertyConstants.ACH_TRANSACTION_TYPE, newAchTransactionType);
        criteria.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, newPayeeIdTypeCd);
        criteria.put(PdpPropertyConstants.PAYEE_ID_NUMBER, newPayeeIdNumber);

        int matches = SpringContext.getBean(BusinessObjectService.class).countMatching(PayeeACHAccount.class, criteria);
        if (matches > 0) {
            putFieldError(PdpPropertyConstants.PAYEE_ID_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_PAYEEACHACCOUNTMAINT_DUPLICATE_RECORD);
            valid = false;
        }

        return valid;
    }

    /**
     * Checks if the user is allowed to submit the new created/edited PayeeAchAccount based on its current transactionType. This
     * checking is needed to prevent the following scenarios which bypass the document level authorization checking: #1 A Bursar
     * user creates a blank PayeeAchAccount, sets the transactionType to TR and submits; #2 A Bursar user copies a PayeeAchAccount
     * with transactionType BZ, changes the transactionType to TR and submits; #3 A Bursar user edits a PayeeAchAccount with
     * transactionType BZ, changes the transactionType to TR and submits.
     */
    protected boolean checkTransactionTypeAllowed() {
        String docTypeName = maintDocDictionaryService.getDocumentTypeName(PayeeACHAccount.class);
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean allowed = businessObjectAuthorizationService.canMaintain(newPayeeAchAccount, user, docTypeName);
        String transType = newPayeeAchAccount.getAchTransactionType();

        if (!allowed) {
            putFieldError(PdpPropertyConstants.ACH_TRANSACTION_TYPE, KFSKeyConstants.ERROR_DOCUMENT_PAYEEACHACCOUNTMAINT_TRANSACTION_TYPE_NOT_ALLOWED, transType);
        }
        return allowed;
    }

}

/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.pdp.rules;

import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.pdp.bo.PayeeAchAccount;
/*
import java.util.Iterator;
import java.util.List;


import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.KeyValuesService;

import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.rules.AccountingPeriodRule;
 */

public class PayeeAchAccountRule extends MaintenanceDocumentRuleBase {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeAchAccount.class);
    
    private PayeeAchAccount oldPayeeAchAccount;
    private PayeeAchAccount newPayeeAchAccount;

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    public void setupConvenienceObjects() {
        
        LOG.info("setupConvenienceObjects called");

        // setup oldAchBank convenience objects, make sure all possible sub-objects are populated
        oldPayeeAchAccount = (PayeeAchAccount) super.getOldBo();

        // setup newAchBank convenience objects, make sure all possible sub-objects are populated
        newPayeeAchAccount = (PayeeAchAccount) super.getNewBo();
    }
    
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        
        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }
 /*
        String label = boe.getAttributeDefinition(propertyName).getShortLabel();
        GlobalVariables.getErrorMap().putError(propertyName, KFSKeyConstants.ERROR_REQUIRED, label);
  */   
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        
        boolean validEntry = true;
        String payeeIdTypeCd, payeeUserId, feinNumber, dvPayeeId, ssn;
        Integer vendorGnrtdId, vendorAsndId;

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();
        
        payeeIdTypeCd = newPayeeAchAccount.getPayeeIdentifierTypeCode();
        
        if (payeeIdTypeCd == null)
            return validEntry;

        if (payeeIdTypeCd.equals("E")) {
            payeeUserId = newPayeeAchAccount.getPersonUniversalIdentifier();
            if (payeeUserId == null)
                putFieldError("error.required", KFSKeyConstants.ERROR_REQUIRED,"Payee User Id");
            else {
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            } 
        } else if (payeeIdTypeCd.equals("V")) {
            vendorGnrtdId = newPayeeAchAccount.getVendorHeaderGeneratedIdentifier();
            vendorAsndId = newPayeeAchAccount.getVendorDetailAssignedIdentifier(); 
            if (vendorGnrtdId == null) {
                putFieldError("error.required", KFSKeyConstants.ERROR_REQUIRED,"Vendor");
                validEntry = false;
            }
            if (vendorAsndId == null) {
                putFieldError("error.required", KFSKeyConstants.ERROR_REQUIRED,"Vendor");
                validEntry = false;
            }
            if (validEntry) {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
            }       
        } else if (payeeIdTypeCd.equals("F")) {
            feinNumber = newPayeeAchAccount.getPayeeFederalEmployerIdentificationNumber();
            if (feinNumber == null) {
                putFieldError("error.required", KFSKeyConstants.ERROR_REQUIRED,"FEIN Number");
                validEntry = false;
            }
            else {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            } 
        } else if (payeeIdTypeCd.equals("S")) {
            ssn = newPayeeAchAccount.getPayeeSocialSecurityNumber();
            if (ssn == null) {
                putFieldError("error.required", KFSKeyConstants.ERROR_REQUIRED,"Social Security Number");
                validEntry = false;
            } else {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
                newPayeeAchAccount.setDisbVchrPayeeIdNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            }   
        } else if (payeeIdTypeCd.equals("P")) {
            dvPayeeId = newPayeeAchAccount.getDisbVchrPayeeIdNumber();
            if (dvPayeeId == null) {
                putFieldError("error.required", KFSKeyConstants.ERROR_REQUIRED,"Disbursement Voucher Payee ID");
                validEntry = false;
        } else {
                newPayeeAchAccount.setPersonUniversalIdentifier(null);
                newPayeeAchAccount.setPayeeFederalEmployerIdentificationNumber(null);
                newPayeeAchAccount.setPayeeSocialSecurityNumber(null);
                newPayeeAchAccount.setVendorHeaderGeneratedIdentifier(null);
                newPayeeAchAccount.setVendorDetailAssignedIdentifier(null);
            }   
         } 
        return validEntry;
    }

}

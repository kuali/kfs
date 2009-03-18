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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class SystemInformationRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(SystemInformationRule.class);
    
    private ObjectTypeService objectTypeService;
    private AccountService accountService;
    private SystemInformation newSystemInformation;
    private SystemInformation oldSystemInformation;

    public SystemInformationRule() {
        super();
        // insert object type service
        this.setObjectTypeService(SpringContext.getBean(ObjectTypeService.class));
    }

    @Override
    public void setupConvenienceObjects() {
        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldSystemInformation = (SystemInformation) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSystemInformation = (SystemInformation) super.getNewBo();
    }

    /**
     * This performs the following checks on document approve:
     * <ul>
     * <li>{@link SystemInformationRule#checkClearingAccountIsActive()}</li>
     * </ul>
     * This rule fails on rule failure
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");

        success &= checkClearingAccountIsActive();
        success &= checkWireAccountIsActive();
        success &= checkLockboxNumberIsUnique();

        return success;
    }

    /**
     * This performs the following checks on document route:
     * <ul>
     * <ul>
     * <li>{@link SystemInformationRule#checkClearingAccountIsActive()}</li>
     * </ul>
     * </ul>
     * This rule fails on rule failure
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        success &= checkClearingAccountIsActive();
        success &= checkWireAccountIsActive();
        success &= checkLockboxNumberIsUnique();

        //return success;
        return true;
    }

    /**
     * This performs the following checks on document save:
     * <ul>
     * <ul>
     * <li>{@link SystemInformationRule#checkClearingAccountIsActive()}</li>
     * </ul>
     * </ul>
     * This rule does not fail on rule failure
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        success &= checkClearingAccountIsActive();
        success &= checkWireAccountIsActive();
        success &= checkLockboxNumberIsUnique();

        return success;
        // TODO method never shows any errors even if returning false; just says 'Document was successfully saved'
    }

     /**
     *
     * This checks to see if the account is active
     * @return true if the account is active or false otherwise
     */
    protected boolean checkClearingAccountIsActive() {

        LOG.info("Entering checkClearingAccountIsActive()");
        boolean success = true;

       AccountService accountService = SpringContext.getBean(AccountService.class);
       Account clearingAccount = accountService.getByPrimaryId(newSystemInformation.getUniversityClearingChartOfAccountsCode(), newSystemInformation.getUniversityClearingAccountNumber());

        // check clearing account is not in-active
         if (ObjectUtils.isNull(clearingAccount)) {         
         return false;
         }
         if (!clearingAccount.isActive()) {
            success &= false;
            putGlobalError(ArKeyConstants.SystemInformation.ERROR_CLEARING_ACCOUNT_INACTIVE);
        }

        return success;
    }

    /**
     * This checks to see if the account is active
     *
     * @return true if the account is active or false otherwise
     */
    protected boolean checkWireAccountIsActive() {

        LOG.info("Entering checkWireAccountIsActive()");
        boolean success = true;

        AccountService accountService = SpringContext.getBean(AccountService.class);
        Account wireAccount = accountService.getByPrimaryId(newSystemInformation.getWireChartOfAccountsCode(), newSystemInformation.getWireAccountNumber());

        // check wire account is not in-active
        if (ObjectUtils.isNull(wireAccount)) {
            return false;
        }
        
        if (!wireAccount.isActive()) {
            success &= false;
            putGlobalError(ArKeyConstants.SystemInformation.ERROR_WIRE_ACCOUNT_INACTIVE);
        }

        return success;
    }
    
    /**
    *
    * This checks to see if the lockbox number is unique
    * @return true if the lockbox number is active or false otherwise
    */
   protected boolean checkLockboxNumberIsUnique() {

       LOG.info("Entering checkLockboxNumberIsUnique()");
       boolean success = true;

      SystemInformationService systemInformationService = SpringContext.getBean(SystemInformationService.class);
      int recordNumber = systemInformationService.getCountByChartOrgAndLockboxNumber(newSystemInformation.getProcessingChartOfAccountCode(),
                                                                                     newSystemInformation.getProcessingOrganizationCode(),
                                                                                     newSystemInformation.getLockboxNumber());
      // if not unique
      if (recordNumber > 0) {
          success = false;
          putFieldError(ArPropertyConstants.SystemInformationFields.LOCKBOX_NUMBER, ArKeyConstants.SystemInformation.ERROR_LOCKBOX_NUMBER_NOT_UNIQUE);
      }

      return success;
   }



/*
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success;
        success = checkSalesTaxObjectValidCode(newSystemInformation);

        return success;
    }
*/

 /*
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        //processCustomRouteDocumentBusinessRules(document);
        return true;
    }
*/
    
    /**
     * 
     * This method checks that the Sales Tax Objcet Code is of type Income
     * Using the ParameterService to find this valid value?
     * <ul>
     * <li>IC</li>
     * <li>IH</li>
     * <li>CN</li>
     * </ul>
     * @return true if it is an income object
     */
/*
    protected boolean checkSalesTaxObjectValidCode(SystemInformation document) {
        boolean success = true;
        Integer universityFiscalYear = document.getUniversityFiscalYear();
        ObjectCode salesTaxFinancialObject = document.getSalesTaxFinancialObject();
        
        if (ObjectUtils.isNotNull(universityFiscalYear) && ObjectUtils.isNotNull(salesTaxFinancialObject)) {
            success = objectTypeService.getBasicIncomeObjectTypes(universityFiscalYear).contains(salesTaxFinancialObject.getFinancialObjectTypeCode());

            if (!success) {
                putFieldError("salesTaxFinancialObjectCode",ArKeyConstants.SystemInformation.SALES_TAX_OBJECT_CODE_INVALID,salesTaxFinancialObject.getCode());
            }
        }
        return success;
    }
*/


    public ObjectTypeService getObjectTypeService() {
        return objectTypeService;
    }

    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }
}

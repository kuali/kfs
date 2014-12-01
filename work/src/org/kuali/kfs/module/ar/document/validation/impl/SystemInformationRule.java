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
import org.kuali.rice.krad.util.ObjectUtils;

public class SystemInformationRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(SystemInformationRule.class);
    
    protected ObjectTypeService objectTypeService;
    protected AccountService accountService;
    protected SystemInformation newSystemInformation;
    protected SystemInformation oldSystemInformation;

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

        LOG.debug("Entering processCustomApproveDocumentBusinessRules()");

        success &= checkClearingAccountIsActive();
//        success &= checkWireAccountIsActive();
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

        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

        success &= checkClearingAccountIsActive();
//        success &= checkWireAccountIsActive();
        success &= checkLockboxNumberIsUnique();

        return success;
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

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");

        success &= checkClearingAccountIsActive();
//        success &= checkWireAccountIsActive();
        success &= checkLockboxNumberIsUnique();

        //return success;
        return true;
        // TODO method never shows any errors even if returning false; just says 'Document was successfully saved'
    }

     /**
     *
     * This checks to see if the account is active
     * @return true if the account is active or false otherwise
     */
    protected boolean checkClearingAccountIsActive() {

        LOG.debug("Entering checkClearingAccountIsActive()");
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
    *
    * This checks to see if the lockbox number is unique
    * @return true if the lockbox number is active or false otherwise
    */
   protected boolean checkLockboxNumberIsUnique() {

       LOG.debug("Entering checkLockboxNumberIsUnique()");
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
    
    public ObjectTypeService getObjectTypeService() {
        return objectTypeService;
    }

    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }
}

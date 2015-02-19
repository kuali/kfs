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

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class AchBankRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ACHBank.class);

    protected ACHBank oldAchBank;
    protected ACHBank newAchBank;

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupConvenienceObjects() {

        LOG.info("setupConvenienceObjects called");

        // setup oldAchBank convenience objects, make sure all possible sub-objects are populated
        oldAchBank = (ACHBank) super.getOldBo();

        // setup newAchBank convenience objects, make sure all possible sub-objects are populated
        newAchBank = (ACHBank) super.getNewBo();
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);
        
        // Save always succeeds, even if there are business rule failures
        return true;
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean validEntry = true;

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        String officeCode = newAchBank.getBankOfficeCode();
        if ((ObjectUtils.isNotNull(officeCode)) && !officeCode.equals(PdpConstants.AchBankOfficeCodes.AchBankOfficeCode_O) && !officeCode.equals(PdpConstants.AchBankOfficeCodes.AchBankOfficeCode_B)) {
            putFieldError("bankOfficeCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_OFFICE_CODE);
            validEntry = false;
        }

        String typeCode = newAchBank.getBankTypeCode();
        if ((typeCode != null) && !typeCode.equals(PdpConstants.AchBankTypeCodes.AchBankTypeCode_0) && !typeCode.equals(PdpConstants.AchBankTypeCodes.AchBankTypeCode_1) && !typeCode.equals(PdpConstants.AchBankTypeCodes.AchBankTypeCode_2)) {
            putFieldError("bankTypeCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_TYPE_CODE);
            validEntry = false;
        }

        String bankInstitutionStatusCode = newAchBank.getBankInstitutionStatusCode();
        if ((ObjectUtils.isNotNull(bankInstitutionStatusCode) ) && !bankInstitutionStatusCode.equals(PdpConstants.ACH_BANK_INSTITUTION_CODE_DEFAULT)) {
            putFieldError("bankInstitutionStatusCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_INST_STATUS_CODE);
            validEntry = false;
        }

        String bankDataViewCode = newAchBank.getBankDataViewCode();
        if ((ObjectUtils.isNotNull(bankDataViewCode) ) && !bankDataViewCode.equals(PdpConstants.ACH_BANK_DATA_VIEW_CODE_DEFAULT)) {
            putFieldError("bankDataViewCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_DATA_VIEW_CODE);
            validEntry = false;
        }

        return validEntry;
    }
   
}

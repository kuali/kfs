/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.validation.impl;


import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class EquipmentLoanOrReturnDocumentRule extends TransactionalDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocumentRule.class);

    private AssetLockService assetLockService;

    /**
     * Retrieve asset number need to be locked.
     * 
     * @param document
     * @return
     */
    protected List<Long> retrieveAssetNumberForLocking(Document document) {
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;
        List<Long> assetNumbers = new ArrayList<Long>();
        if (equipmentLoanOrReturnDocument.getCapitalAssetNumber() != null) {
            assetNumbers.add(equipmentLoanOrReturnDocument.getCapitalAssetNumber());
        }
        return assetNumbers;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        if (!super.processCustomRouteDocumentBusinessRules(document)) {
            return false;
        }

        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;
        boolean valid = processValidation(equipmentLoanOrReturnDocument);

        // check if asset is locked by other document and display error message when save
        valid &= !getAssetLockService().isAssetLocked(retrieveAssetNumberForLocking(document), CamsConstants.DocumentTypeName.ASSET_EQUIPMENT_LOAN_OR_RETURN, document.getDocumentNumber());
        return valid;
    }

    /**
     * This method applies business rules
     * 
     * @param document equipmentLoanOrReturnDocument Document
     * @return true if all rules are pass
     */
    protected boolean processValidation(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        if (equipmentLoanOrReturnDocument.getBorrowerPerson() == null) {
            valid &= false;
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_PRINCIPAL_NAME, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_ID);
        }
        // validate campus tag number
        valid &= validateTagNumber(equipmentLoanOrReturnDocument);

        // validate both loan return date and expected loan return date
        valid &= validateLoanDate(equipmentLoanOrReturnDocument);

        // validate borrower and storage state codes
        valid &= validStateZipCode(equipmentLoanOrReturnDocument);

        return valid;
    }

    /**
     * Validate that the campus tag number exists prior to submitting a loan.
     * 
     * @param equipmentLoanOrReturnDocument
     * @return boolean false if the campus tag number does not exist
     */
    protected boolean validateTagNumber(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;

        HashMap<String, Long> map = new HashMap<String, Long>();
        map.put(CamsPropertyConstants.EquipmentLoanOrReturnDocument.CAPITAL_ASSET_NUMBER, equipmentLoanOrReturnDocument.getCapitalAssetNumber());
        Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, map);

        if (asset.getCampusTagNumber() == null) {
            valid &= false;
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_CAMPUS_TAG_NUMBER_REQUIRED);
        }

        return valid;
    }

    /**
     * Implementation of the rule that if a document has a valid expect loan date and loan return date, the both dates should come
     * before the 2 years limit.
     * 
     * @param equipmentLoanOrReturnDocument the equipmentLoanOrReturn document to be validated
     * @return boolean false if the expect loan date or loan return date is not before the 2 years limit.
     */
    protected boolean validateLoanDate(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        Date loanDate = DateUtils.clearTimeFields(equipmentLoanOrReturnDocument.getLoanDate());
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(loanDate);
        cal.add(Calendar.YEAR, 2);
        Date maxDate = new Date(cal.getTime().getTime());

        // Loan can not be before today
        Date loanReturnDate = equipmentLoanOrReturnDocument.getLoanReturnDate();
        if (equipmentLoanOrReturnDocument.isNewLoan() && loanDate.before(DateUtils.clearTimeFields(new java.util.Date()))) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.LOAN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_LOAN_DATE);
        }

        // expect return date must be >= loan date and within 2 years limit
        Date expectReturnDate = equipmentLoanOrReturnDocument.getExpectedReturnDate();
        if (expectReturnDate != null) {
            DateUtils.clearTimeFields(expectReturnDate);
            if (expectReturnDate.before(loanDate)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.EXPECTED_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_EXPECTED_RETURN_DATE);
            }
            if (maxDate.before(expectReturnDate)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.EXPECTED_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_EXPECTED_MAX_DATE);
            }
        }

        // loan return date must be >= loan date and within 2 years limit
        if (loanReturnDate != null) {
            DateUtils.clearTimeFields(loanReturnDate);
            if (loanDate.after(loanReturnDate) || maxDate.before(loanReturnDate)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.LOAN_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_LOAN_RETURN_DATE);
            }
        }

        return valid;
    }

    /**
     * Implementation of the rule that if borrower and storage state codes are valid
     * 
     * @param equipmentLoanOrReturnDocument the equipmentLoanOrReturn document to be validated
     * @return boolean false if the borrower or storage state code is not valid
     */
    private boolean validStateZipCode(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        // validate borrower state and postal zip code
        if (StringUtils.isBlank(equipmentLoanOrReturnDocument.getBorrowerCountryCode())) {
            equipmentLoanOrReturnDocument.setBorrowerCountryCode(KFSConstants.COUNTRY_CODE_UNITED_STATES);
        }
        State borrowStateCode = equipmentLoanOrReturnDocument.getBorrowerState();
        if (ObjectUtils.isNull(borrowStateCode)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_STATE_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_STATE, equipmentLoanOrReturnDocument.getBorrowerStateCode());
            valid &= false;
        }

        // validate borrower storage state and postal zip code
        if (StringUtils.isNotBlank(equipmentLoanOrReturnDocument.getBorrowerStorageStateCode())) {
            if (StringUtils.isBlank(equipmentLoanOrReturnDocument.getBorrowerStorageCountryCode())) {
                equipmentLoanOrReturnDocument.setBorrowerStorageCountryCode(KFSConstants.COUNTRY_CODE_UNITED_STATES);
            }
            State borrowStorageStateCode = equipmentLoanOrReturnDocument.getBorrowerStorageState();
            if (ObjectUtils.isNull(borrowStorageStateCode)) {
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_STAORAGE_STATE_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_STORAGE_STATE, equipmentLoanOrReturnDocument.getBorrowerStorageStateCode());
                valid = false;
            }
        }

        return valid;
    }

    public AssetLockService getAssetLockService() {
        if (this.assetLockService == null) {
            this.assetLockService = SpringContext.getBean(AssetLockService.class);
        }
        return assetLockService;
    }
}

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
package org.kuali.module.cams.rules;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.service.AssetService;

public class EquipmentLoanOrReturnDocumentRule extends TransactionalDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocumentRule.class);

    private static final String DATEFORMAT = "MM/dd/yyyy";
    private AssetService assetService;
    private DateTimeService dateTimeService;

    /**
     * Does not fail on rules failure
     * 
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;

        if (getAssetService().isAssetLocked(equipmentLoanOrReturnDocument.getDocumentNumber(), equipmentLoanOrReturnDocument.getCapitalAssetNumber())) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;

        if (getAssetService().isAssetLocked(equipmentLoanOrReturnDocument.getDocumentNumber(), equipmentLoanOrReturnDocument.getCapitalAssetNumber())) {
            return false;
        }

        boolean valid = processValidation(equipmentLoanOrReturnDocument);

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
        // validate if both loan return date and expected loan return date are valid
        valid &= validateLoanDate(equipmentLoanOrReturnDocument);
        // validate if borrower id is valid
        valid &= validBorrowerId(equipmentLoanOrReturnDocument);

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
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        Date maxLoanDate = CalculateMaxLoanDate(equipmentLoanOrReturnDocument);
        Date loanDate = equipmentLoanOrReturnDocument.getLoanDate();

        Date expectReturnDate = equipmentLoanOrReturnDocument.getExpectedReturnDate();
        if (ObjectUtils.isNotNull(expectReturnDate)) {
            if ((dateTimeService.dateDiff(loanDate, expectReturnDate, false) <= 0) || (dateTimeService.dateDiff(expectReturnDate, maxLoanDate, false) <= 0)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.EXPECTED_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_EXPECTED_RETURN_DATE);
            }
        }

        Date loanReturnDate = equipmentLoanOrReturnDocument.getLoanReturnDate();
        if (ObjectUtils.isNotNull(loanReturnDate)) {
            if ((dateTimeService.dateDiff(loanDate, loanReturnDate, false) <= 0) || (dateTimeService.dateDiff(loanReturnDate, maxLoanDate, false) <= 0)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.LOAN_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_LOAN_RETURN_DATE);
            }
        }

        return valid;
    }

    /**
     * This method calculate maximum loan date
     * 
     * @param equipmentLoanOrReturnDocument
     * @return maxi loan date which is 2 years limit
     */
    protected Date CalculateMaxLoanDate(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        Date maxDate = null;
        String sLoanDate = new SimpleDateFormat(DATEFORMAT).format(equipmentLoanOrReturnDocument.getLoanDate());
        int sMaxLoanYear = Integer.parseInt(sLoanDate.substring(6, 10));
        sMaxLoanYear += 2;
        String maxLoanDate = sLoanDate.substring(0, 6) + sMaxLoanYear;
        try {
            maxDate = new java.sql.Date(((new SimpleDateFormat(DATEFORMAT)).parse(maxLoanDate)).getTime());
        }
        catch (ParseException pE) {
            LOG.error("EquipmentLoanOrReturnAction - convertStringToDate() error occurred.", pE);
        }

        return maxDate;
    }

    /**
     * Implementation of the rule that if borrower id is valid
     * 
     * @param equipmentLoanOrReturnDocument the equipmentLoanOrReturn document to be validated
     * @return boolean false if the borrower id does not exist.
     */
    private boolean validBorrowerId(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        LOG.info("borrower id= " + equipmentLoanOrReturnDocument.getBorrowerUniversalIdentifier() + "");
        boolean valid = true;
        if (StringUtils.isBlank(equipmentLoanOrReturnDocument.getBorrowerUniversalIdentifier())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_UNIVERSAL_USER + "." + KFSPropertyConstants.PERSON_USER_IDENTIFIER, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_ID);
            valid = false;
        }
        return valid;
    }

    public AssetService getAssetService() {
        if (this.assetService == null) {
            this.assetService = SpringContext.getBean(AssetService.class);
        }
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}

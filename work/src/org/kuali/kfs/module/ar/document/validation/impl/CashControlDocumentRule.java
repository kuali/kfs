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
package org.kuali.module.ar.rules;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.rule.AddCashControlDetailRule;
import org.kuali.module.ar.service.OrganizationOptionsService;
import org.kuali.module.ar.service.impl.OrganizationOptionsServiceImpl;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.AdvanceDepositDocumentRuleUtil;
import org.kuali.module.financial.rules.DisbursementVoucherDocumentRule;

public class CashControlDocumentRule extends TransactionalDocumentRuleBase implements AddCashControlDetailRule<CashControlDocument>{
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentRule.class);

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomSaveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        CashControlDocument ccDocument = (CashControlDocument) document;

        isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);
        isValid &= validateCashControlDetails(ccDocument);

        return true;
    }
    
    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CashControlDocument ccDocument = (CashControlDocument) document;

        isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);
        isValid &= validateCashControlDetails(ccDocument);

        return isValid;
    }
    
    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomApproveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        CashControlDocument ccDocument = (CashControlDocument) approveEvent.getDocument();

        isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);
        isValid &= validateCashControlDetails(ccDocument);

        return isValid;
    }
    
  
    private boolean checkLineAmount(CashControlDocument document, CashControlDetail detail) {

        boolean isValid = true;

        if (detail.getFinancialDocumentLineAmount().isZero()) {
            GlobalVariables.getErrorMap().putError("financialDocumentLineAmount", KFSKeyConstants.CashControl.LINE_AMOUNT_CANNOT_BE_ZERO);
            isValid = false;
        }
        else if (detail.getFinancialDocumentLineAmount().isNegative() && document.isHasNegativeCashControlDetail()) {
            GlobalVariables.getErrorMap().putError("financialDocumentLineAmount", KFSKeyConstants.ERROR_INVALID_NEGATIVE_AMOUNT);
            isValid = false;
        }

        if (detail.getFinancialDocumentLineAmount().isNegative()) {
            document.setHasNegativeCashControlDetail(true);
        }

        return isValid;
    }
    
    private boolean checkPaymentMedium(CashControlDocument document) {
        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        String paymentMedium = document.getCustomerPaymentMediumCode();
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }
    
    private boolean checkOrgDocNumber(CashControlDocument document) {
        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        String paymentMedium = document.getCustomerPaymentMediumCode();
        if (paymentMedium != null && paymentMedium.equals("CA")) {
            String orgDocNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
            if (orgDocNumber == null) {
                GlobalVariables.getErrorMap().putError("organizationDocumentNumber", ArConstants.ERROR_ORGANIZATION_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH);
                isValid = false;
            }
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }
    
    private boolean checkReferenceDocument(CashControlDocument document) {
        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        String refDocNumber = document.getReferenceFinancialDocumentNumber();
        if (null == refDocNumber) {
            GlobalVariables.getErrorMap().putError("referenceFinancialDocumentNumber", ArConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL);
            isValid = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }
    
    private boolean checkUserOrgOptions(CashControlDocument document) {
        boolean success = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        ChartUser user = ValueFinderUtil.getCurrentChartUser();
        String chartCode = user.getChartOfAccountsCode();
        String orgCode = user.getUserOrganizationCode();
        OrganizationOptionsService service = SpringContext.getBean(OrganizationOptionsService.class);

        if (null == service.getByPrimaryKey(chartCode, orgCode)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ORGANIZATION_CODE, ArConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG);
            success = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return success;
    }
    
    
    public boolean processAddCashControlDetailBusinessRules(CashControlDocument document, CashControlDetail cashControlDetail) {
        boolean success = validateCashControlDetail(document, cashControlDetail);

        return success;
    }
    
    private boolean validateCashControlDetail(CashControlDocument document, CashControlDetail cashControlDetail) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        int originalErrorCount = errorMap.getErrorCount();
        checkLineAmount(document, cashControlDetail);

        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(cashControlDetail);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);

        return isValid;
    }
    
    private boolean validateCashControlDetails(CashControlDocument cashControlDocument)
    {
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < cashControlDocument.getCashControlDetails().size(); i++) {
            String propertyName = KFSPropertyConstants.CASH_CONTROL_DETAIL + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            isValid &= validateCashControlDetail(cashControlDocument,cashControlDocument.getCashControlDetail(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }

        // don't bother checking the total if some deposits are broken
        if (isValid && cashControlDocument.getTotalDollarAmount().isZero()) {
            isValid = false;
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CASH_CONTROL_DETAIL, CashReceipt.ERROR_ZERO_TOTAL, "Cash Control Total");
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }

}

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

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.service.OrganizationOptionsService;
import org.kuali.module.ar.service.impl.OrganizationOptionsServiceImpl;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

public class CashControlDocumentRule extends TransactionalDocumentRuleBase {

    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        
        CashControlDocument ccDocument = (CashControlDocument)document;
        isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);       
        
        return true;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CashControlDocument ccDocument = (CashControlDocument)document;
        
        isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument); 
        
        return isValid;
    }
    
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        CashControlDocument ccDocument = (CashControlDocument)approveEvent.getDocument();
        
        isValid &= checkReferenceDocument(ccDocument);
        isValid &= checkUserOrgOptions(ccDocument);
        isValid &= checkPaymentMedium(ccDocument);
        isValid &= checkOrgDocNumber(ccDocument);   
        
        return isValid;
    }
    
  
    private boolean checkLineAmounts(CashControlDocument document, CashControlDetail detail) {
        boolean isValid = false;
        if (detail.getFinancialDocumentLineAmount().isZero()){
            GlobalVariables.getErrorMap().putError("financialDocumentLineAmount", KFSKeyConstants.ERROR_ZERO_AMOUNT);
            return false;
        }
        else if (detail.getFinancialDocumentLineAmount().isNegative() && document.isHasNegativeCashControlDetail()) {
            GlobalVariables.getErrorMap().putError("financialDocumentLineAmount", KFSKeyConstants.ERROR_INVALID_NEGATIVE_AMOUNT);
            return false;
        }
        
        if (detail.getFinancialDocumentLineAmount().isNegative()) {
            document.setHasNegativeCashControlDetail(true);
        }
        
        return isValid; 
    }
    
    private boolean checkPaymentMedium(CashControlDocument document) {
        boolean isValid = false;
        
        String paymentMedium = document.getCustomerPaymentMediumCode();
        
        return isValid;
    }
    
    private boolean checkOrgDocNumber(CashControlDocument document) {
        boolean isValid = true;
        
        String paymentMedium = document.getCustomerPaymentMediumCode();
        if (paymentMedium.equals("CA")) {
            String orgDocNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
            if (orgDocNumber == null) {
                GlobalVariables.getErrorMap().putError("organizationDocumentNumber", ArConstants.ERROR_ORGANIZATION_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH);
                isValid = false;
            }
        }
        return isValid;
    }
    
    private boolean checkReferenceDocument(CashControlDocument document) {
        boolean isValid = true;
        
        String refDocNumber = document.getReferenceFinancialDocumentNumber();
        if (null == refDocNumber) {
            GlobalVariables.getErrorMap().putError("referenceFinancialDocumentNumber", ArConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL);
            isValid = false;
        }
        return isValid;
    }
    
    private boolean checkUserOrgOptions(CashControlDocument document) {
       boolean success = true;
        ChartUser user = ValueFinderUtil.getCurrentChartUser();
        String chartCode = user.getChartOfAccountsCode();
        String orgCode = user.getUserOrganizationCode();
        OrganizationOptionsService service = new OrganizationOptionsServiceImpl();
        
        if (null != service.getByPrimaryKey(chartCode, orgCode)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ORGANIZATION_CODE, ArConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG);
            success = false;
        }
        return success;
    }
    
    public boolean processAddCashControlDetailBusinessRules(CashControlDocument document, CashControlDetail detail) {
        boolean success = checkLineAmounts(document, detail);
        
        
        return success;
    }
}

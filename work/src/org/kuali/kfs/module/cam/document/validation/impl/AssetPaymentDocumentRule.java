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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rule.event.BlanketApproveDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.rules.AccountingDocumentRuleBase.AccountingLineAction;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborJournalVoucherDetail;
import org.kuali.module.labor.bo.PositionData;
import org.kuali.module.gl.bo.UniversityDate;

public class AssetPaymentDocumentRule extends AccountingDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentDocumentRule.class);

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        LOG.info("*** AssetPaymentRule - processCustomSaveDocumentBusinessRules(Document document)");
        boolean valid = super.processCustomSaveDocumentBusinessRules(document);
        return valid;
    }

    /*
     * This method was overrided because the asset payment only uses source and not target lines.
     * Not gl pending entries are needed.
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.info("*** AssetPaymentRule - processCustomRouteDocumentBusinessRules(Document document)");
        //boolean valid = super.processCustomRouteDocumentBusinessRules(document);
        
        boolean valid = this.isSourceAccountingLinesRequiredNumberForRoutingMet((AccountingDocument)document);
        return valid;        
    }


    /**
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.info("*** AssetPaymentRule - processCustomAddAccountingLineBusinessRules.");

        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail)accountingLine;
        Map<String,Object> keyToFind= new HashMap<String,Object>();

        //Document type existence check.
        String expenditureFinancialDocumentTypeCode= assetPaymentDetail.getExpenditureFinancialDocumentTypeCode();
        
        //Validating the fiscal year and fiscal month (period)
        boolean isValid=true;
        
        //Validating the fiscal year and month because, the object code validation needs to have this a valid fiscal year
        if (isValid) {            
            keyToFind= new HashMap<String,Object>();          
            keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR             , assetPaymentDetail.getFinancialDocumentPostingYear());
            keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD, assetPaymentDetail.getFinancialDocumentPostingPeriodCode());
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(UniversityDate.class,keyToFind) == 0){
                String labelFiscalYear  = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR).getLabel();
                String labelFiscalMonth = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH).getLabel();

                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalYear);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalMonth);
                isValid = false;                
            }
        }

        /*if (isValid) {
            isValid=super.processCustomAddAccountingLineBusinessRules(financialDocument, accountingLine);
        }*/

        if (isValid) {
            if (!StringUtils.isBlank(expenditureFinancialDocumentTypeCode)) {
                keyToFind= new HashMap<String,Object>();            
                keyToFind.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, expenditureFinancialDocumentTypeCode);

                if (SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, keyToFind) == null) {            
                    String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(DocumentType.class.getName()).getAttributeDefinition(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE).getLabel();
                    GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                    isValid = false;
                }
            }
        }

        //Validating posting date which must exists in the university date table
        if (isValid) {            
            keyToFind= new HashMap<String,Object>();          
            keyToFind.put(KFSPropertyConstants.UNIVERSITY_DATE, assetPaymentDetail.getExpenditureFinancialDocumentPostedDate());
            if (SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(UniversityDate.class,keyToFind) == null){
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, KFSKeyConstants.ERROR_EXISTENCE, label);
                isValid = false;                
            }
        }

        return isValid;
    }
    
    @Override    
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        LOG.info("*** AssetPaymentRule - processCustomApproveAccountingLineBusinessRules.");                
        boolean valid =super.processCustomApproveDocumentBusinessRules(approveEvent);
        return valid;
    }

}

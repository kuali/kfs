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
package org.kuali.module.cams.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.gl.bo.UniversityDate;

public class AssetPaymentFiscalPeriodValidation extends GenericValidation {

    private AccountingLine accountingLine;

    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    public void setAccountingLine(AccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }

    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLine();
        Map<String, Object> keyToFind = new HashMap<String, Object>();
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, assetPaymentDetail.getFinancialDocumentPostingYear());
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD, assetPaymentDetail.getFinancialDocumentPostingPeriodCode());

        if (SpringContext.getBean(BusinessObjectService.class).countMatching(UniversityDate.class, keyToFind) == 0) {
            String labelFiscalYear = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR).getLabel();
            String labelFiscalMonth = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH).getLabel();

            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalYear);
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalMonth);
            result = false;
        }
        return result;
    }


}

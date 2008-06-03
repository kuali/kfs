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

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.service.OriginationCodeService;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;

public class AssetPaymentOrginCodeValidation extends GenericValidation {

    private AccountingLine accountingLine;

    public boolean validate(AttributedDocumentEvent event) {
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLine();
        boolean result = true;
        if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode())) {
            if (SpringContext.getBean(OriginationCodeService.class).getByPrimaryKey(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode()) == null) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(OriginationCode.class.getName()).getAttributeDefinition(RicePropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        }
        return result;
    }

    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    public void setAccountingLine(AccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }


}

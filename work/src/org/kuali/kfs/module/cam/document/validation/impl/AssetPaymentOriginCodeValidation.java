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
package org.kuali.kfs.module.cam.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.OriginationCodeService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class validates asset payment document origin code
 */
public class AssetPaymentOriginCodeValidation extends GenericValidation {

    private OriginationCodeService originationCodeService;
    private DataDictionaryService dataDictionaryService;
    private AccountingLine accountingLineForValidation;

    /**
     * Validates origin code
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // skip check if accounting line is from CAB
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return true;
        }
        
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLineForValidation();
        boolean result = true;
        if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode())) {
            if (originationCodeService.getByPrimaryKey(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode()) == null) {
                String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(OriginationCode.class.getName()).getAttributeDefinition(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        }
        return result;
    }

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLine) {
        this.accountingLineForValidation = accountingLine;
    }

    public OriginationCodeService getOriginationCodeService() {
        return originationCodeService;
    }

    public void setOriginationCodeService(OriginationCodeService originationCodeService) {
        this.originationCodeService = originationCodeService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


}

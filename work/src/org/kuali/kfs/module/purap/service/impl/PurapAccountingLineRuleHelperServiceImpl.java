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
package org.kuali.kfs.module.purap.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingLineRuleHelperService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PurapAccountingLineRuleHelperServiceImpl extends AccountingLineRuleHelperServiceImpl implements PurapAccountingLineRuleHelperService{
    private PurchasingAccountsPayableDocument document;
    
    
    
    public PurchasingAccountsPayableDocument getDocument() {
        return document;
    }

    public void setDocument(PurchasingAccountsPayableDocument document) {
        this.document = document;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     * in purap implementation this does nothing since it is handled in our rule classes
     */
    @Override
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName) {
        
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        ObjectCode objectCodeForValidation = (SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(universityFiscalYear, objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode()));
//      setSubObjectCode(SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, this.getChartOfAccountsCode(), this.getAccountNumber(), this.getFinancialObjectCode(), this.getFinancialSubObjectCode()));
            


        // check active status
        if (!objectCodeForValidation.isFinancialObjectActiveCode()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubObjectCode(org.kuali.kfs.coa.businessobject.SubObjCd, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, DataDictionary dataDictionary, String errorPropertyName) {


        String label = getSubObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        SubObjectCode subObjectCodeForValidation = (SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, subObjectCode.getChartOfAccountsCode(), subObjectCode.getAccountNumber(), subObjectCode.getFinancialObjectCode(), subObjectCode.getFinancialSubObjectCode()));


        // check active flag
        if (!subObjectCodeForValidation.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidAccount(org.kuali.kfs.coa.businessobject.Account, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    @Override
    public boolean isValidAccount(Account account, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(account)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        return true;
    }
    
}

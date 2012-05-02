/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
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
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingAccountingLineRuleHelperServiceImpl extends PurapAccountingLineRuleHelperServiceImpl {

    /**
     * @see org.kuali.kfs.module.purap.service.impl.PurapAccountingLineRuleHelperServiceImpl#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     * override the default implementation and throw our own error message for accounts that are expired.
     */
    @Override
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean retVal = true;
        Account account = line.getAccount();
        if (!ObjectUtils.isNull(account) && account.isExpired()) {            
            GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, account.getAccountNumber());
            retVal = false;
        }
        return retVal;
    }

    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        ObjectCode objectCodeForValidation = (SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(universityFiscalYear, objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode()));

        // check active status
        if (objectCodeForValidation == null || !objectCodeForValidation.isFinancialObjectActiveCode()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }

    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getSubObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        SubObjectCode subObjectCodeForValidation = (SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, subObjectCode.getChartOfAccountsCode(), subObjectCode.getAccountNumber(), subObjectCode.getFinancialObjectCode(), subObjectCode.getFinancialSubObjectCode()));


        // check active flag
        if (!subObjectCodeForValidation.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }
        return true;
    }

}

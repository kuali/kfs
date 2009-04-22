/*
 * Copyright 2009 The Kuali Foundation.
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
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class AccountsPayableAccountingLineRuleHelperServiceImpl extends AccountingLineRuleHelperServiceImpl {

    private AccountsPayableDocument document;
    
    public AccountsPayableDocument getDocument() {
        return document;
    }

    public void setDocument(AccountsPayableDocument document) {
        this.document = document;
    }

    /**
     * @see org.kuali.kfs.module.purap.service.impl.PurapAccountingLineRuleHelperServiceImpl#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     * override the default implementation and throw our own error message for accounts that are expired.
     */
    @Override
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
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

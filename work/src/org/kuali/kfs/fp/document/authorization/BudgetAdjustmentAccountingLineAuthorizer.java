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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;

/**
 * The line authorizer for Budget Adjustment documents, which makes the base amount read only if it can't be edited for the given fiscal year
 */
public class BudgetAdjustmentAccountingLineAuthorizer extends AccountingLineAuthorizerBase {

    /**
     * Overridden to make base amount read only if it is not available to be edited for the given fiscal year 
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getReadOnlyBlocks(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Set<String> getReadOnlyBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser) {
        Set<String> readOnlyBlocks = super.getReadOnlyBlocks(accountingDocument, accountingLine, newLine, currentUser);
        if (!SpringContext.getBean(FiscalYearFunctionControlService.class).isBaseAmountChangeAllowed(((BudgetAdjustmentDocument) accountingDocument).getPostingYear())) {
            readOnlyBlocks.add(getBaseAmountPropertyName());
        }
        return readOnlyBlocks;
    }

    /**
     * @return the property name of the base amount field, which will be set to read only under certain conditions
     */
    protected String getBaseAmountPropertyName() {
        return "baseBudgetAdjustmentAmount";
    }
}


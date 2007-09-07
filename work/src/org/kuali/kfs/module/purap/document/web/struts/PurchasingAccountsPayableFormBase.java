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
package org.kuali.module.purap.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.util.SummaryAccount;

/**
 * 
 * This class is to contain any common functionality between purchasing and accounts payable forms. 
 */
public class PurchasingAccountsPayableFormBase extends KualiAccountingDocumentFormBase {

    private List<SummaryAccount> summaryAccounts;
    
    

    public PurchasingAccountsPayableFormBase() {
        super();
        clearSummaryAccounts();
    }

    /**
     * this method updates the summaryAccounts that are contained in the form
     * currently we are only calling this on load and when refreshAccountSummary is called.
     */
    public void refreshAccountSummmary() {
        clearSummaryAccounts();
        summaryAccounts.addAll(SpringContext.getBean(PurapAccountingService.class).generateSummaryAccounts(((PurchasingAccountsPayableDocument)this.getDocument()).getItems()));
    }

    public void clearSummaryAccounts() {
        summaryAccounts = new TypedArrayList(SummaryAccount.class);
    }

    /**
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#getBaselineSourceAccountingLines()
     */
    @Override
    public List getBaselineSourceAccountingLines() {
        List<AccountingLine> accounts = super.getBaselineSourceAccountingLines();
        if(ObjectUtils.isNull(accounts)|| accounts.isEmpty()) {
            accounts = new ArrayList<AccountingLine>();
            for (PurApItem item : ((PurchasingAccountsPayableDocument)getDocument()).getItems()) {
                List<PurApAccountingLine> lines = item.getBaselineSourceAccountingLines();
                for (PurApAccountingLine line : lines) {
                    accounts.add(line);
                }

            }
        }
        return accounts;
    }

    public List<SummaryAccount> getSummaryAccounts() {
        return summaryAccounts;
    }

    public void setSummaryAccounts(List<SummaryAccount> summaryAccounts) {
        this.summaryAccounts = summaryAccounts;
    }
    
}

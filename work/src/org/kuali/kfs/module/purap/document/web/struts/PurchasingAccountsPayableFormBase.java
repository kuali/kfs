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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * Struts Action Form for Purchasing and Accounts Payable documents.
 */
public class PurchasingAccountsPayableFormBase extends KualiAccountingDocumentFormBase {

    private transient List<SummaryAccount> summaryAccounts;

    /**
     * Constructs a PurchasingAccountsPayableFormBase instance and initializes summary accounts.
     */
    public PurchasingAccountsPayableFormBase() {
        super();
        clearSummaryAccounts();
    }

    /**
     * Updates the summaryAccounts that are contained in the form. Currently we are only calling this on load and when
     * refreshAccountSummary is called.
     */
    public void refreshAccountSummmary() {
        clearSummaryAccounts();
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) getDocument();
        summaryAccounts.addAll(SpringContext.getBean(PurapAccountingService.class).generateSummaryAccounts(purapDocument));
    }

    /**
     * Initializes summary accounts.
     */
    public void clearSummaryAccounts() {
        summaryAccounts = new TypedArrayList(SummaryAccount.class);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#getBaselineSourceAccountingLines()
     */
    @Override
    public List getBaselineSourceAccountingLines() {
        List<AccountingLine> accounts = super.getBaselineSourceAccountingLines();
        if (ObjectUtils.isNull(accounts) || accounts.isEmpty()) {
            accounts = new ArrayList<AccountingLine>();
            for (PurApItem item : ((PurchasingAccountsPayableDocument) getDocument()).getItems()) {
                List<PurApAccountingLine> lines = item.getBaselineSourceAccountingLines();
                for (PurApAccountingLine line : lines) {
                    accounts.add(line);
                }

            }
        }
        return accounts;
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        //fix document item/account references if necessary
        PurchasingAccountsPayableDocument purapDoc = (PurchasingAccountsPayableDocument)this.getDocument();
        purapDoc.fixItemReferences();
    }

    public List<SummaryAccount> getSummaryAccounts() {
        return summaryAccounts;
    }

    public void setSummaryAccounts(List<SummaryAccount> summaryAccounts) {
        this.summaryAccounts = summaryAccounts;
    }

    protected void addExtraButton(String property, String source, String altText) {
    
        ExtraButton newButton = new ExtraButton();
    
        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);
    
        extraButtons.add(newButton);
    }
}

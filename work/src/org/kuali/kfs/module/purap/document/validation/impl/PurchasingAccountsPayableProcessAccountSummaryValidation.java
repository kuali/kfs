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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PurchasingAccountsPayableProcessAccountSummaryValidation extends GenericValidation {

    private PurapAccountingService purapAccountingService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccounts((PurchasingAccountsPayableDocument)event.getDocument());
        for (SummaryAccount summaryAccount : summaryAccounts) {
            //TODO: ctk - do we need all these null checks
            if(ObjectUtils.isNotNull(summaryAccount) && ObjectUtils.isNotNull(summaryAccount.getAccount()) && ObjectUtils.isNotNull(summaryAccount.getAccount().getAmount()) &&
                    summaryAccount.getAccount().getAmount().isNegative()) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_SUMMARY_TAB_ERRORS, PurapKeyConstants.ERROR_ITEM_ACCOUNT_NEGATIVE,summaryAccount.getAccount().getAccountNumber());
            }
        }
        return valid;
    }

    public PurapAccountingService getPurapAccountingService() {
        return purapAccountingService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

}

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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;

import org.kuali.kfs.fp.document.ServiceBillingDocument;
import org.kuali.kfs.fp.document.validation.impl.ServiceBillingDocumentRuleUtil;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;

public class ServiceBillingDocumentPresentationController extends AccountingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)
     */
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        return super.canErrorCorrect(document) && this.canUseAllIncomeSectionAccounts(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canCopy(Document document) {
        return super.canCopy(document) && this.canUseAllIncomeSectionAccounts(document);
    }

    /**
     * @param serviceBillingDocument
     * @param user
     * @return whether the given user is allowed to use all of the accounts in the given SB doc's income accounting lines section
     */
    private boolean canUseAllIncomeSectionAccounts(Document document) {
        ServiceBillingDocument serviceBillingDocument = (ServiceBillingDocument) document;
        if (!super.canErrorCorrect(serviceBillingDocument) && !super.canCopy(serviceBillingDocument)) {
            return false;
        }

        Person user = GlobalVariables.getUserSession().getPerson();
        for (SourceAccountingLine sourceAccountingLine : ((List<SourceAccountingLine>) serviceBillingDocument.getSourceAccountingLines())) {
            if (!ServiceBillingDocumentRuleUtil.serviceBillingIncomeAccountIsAccessible(sourceAccountingLine, null, user)) {
                return false;
            }
        }

        return true;
    }
}

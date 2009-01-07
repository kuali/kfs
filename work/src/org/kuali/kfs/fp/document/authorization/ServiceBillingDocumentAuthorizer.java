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
package org.kuali.kfs.fp.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.fp.document.ServiceBillingDocument;
import org.kuali.kfs.fp.document.validation.impl.ServiceBillingDocumentRuleUtil;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.TransactionalDocument;

/**
 * Authorization permissions specific to the Service Billing document.
 */
public class ServiceBillingDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(ServiceBillingDocumentAuthorizer.class);

    /**
     * Overrides to always return false because there is never FO routing or FO approval for SB docs.
     * 
     * @see FinancialDocumentAuthorizer#userOwnsAnyAccountingLine(KualiUser, List)
     */
    protected boolean userOwnsAnyAccountingLine(Person user, List accountingLines) {
        return false;
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the SB doc.
     * 
     * @see org.kuali.rice.kns.authorization.TransactionalDocumentAuthorizer#getEditableAccounts(org.kuali.rice.kns.document.TransactionalDocument,
     *      KualiUser)
     */
    public Map getEditableAccounts(TransactionalDocument document, Person user) {
        return new HashMap();
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the SB doc.
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#getEditableAccounts(java.util.List,
     *      org.kuali.module.chart.bo.ChartUser)
     */
    @Override
    public Map getEditableAccounts(List<AccountingLine> lines, Person user) {
        return new HashMap();
    }

 // TODO fix for kim
//    /**
//     * Overrides the error-correct flag for SB income account control. Unlike a copy, an SB error correction's income accounts
//     * cannot be changed, so if this user isn't authorized for all those income accounts then he won't be able to save or submit the
//     * error correction. We avoid this frustration by hiding that button in the first place.
//     * 
//     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//        boolean canUseAllIncomeSectionAccountsBool = false;
//        if (flags.getCanErrorCorrect() || flags.getCanCopy()) {
//            // canUseAllIncomeSectionAccounts may be an expensive operation, so we only invoke it exactly once only if it's
//            // absolutely necessary
//            // (i.e. can error correct or copy flags are true)
//            // if any of these flags are false, we rely on short circuiting to ignore the value of this variable when
//            // any of the flags are false
//            canUseAllIncomeSectionAccountsBool = canUseAllIncomeSectionAccounts((ServiceBillingDocument) document, user);
//        }
//        flags.setCanErrorCorrect(flags.getCanErrorCorrect() && canUseAllIncomeSectionAccountsBool);
//        flags.setCanCopy(flags.getCanCopy() && canUseAllIncomeSectionAccountsBool);
//        return flags;
//    }

    /**
     * @param serviceBillingDocument
     * @param user
     * @return whether the given user is allowed to use all of the accounts in the given SB doc's income accounting lines section
     */
    private static boolean canUseAllIncomeSectionAccounts(ServiceBillingDocument serviceBillingDocument, Person user) {
        for (SourceAccountingLine sourceAccountingLine : ((List<SourceAccountingLine>) serviceBillingDocument.getSourceAccountingLines())) {
            if (!ServiceBillingDocumentRuleUtil.serviceBillingIncomeAccountIsAccessible(sourceAccountingLine, null, user)) {
                return false;
            }
        }
        return true;
    }
}


/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.KualiGroup;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.module.financial.bo.ServiceBillingControl;
import org.kuali.module.financial.document.ServiceBillingDocument;
import org.kuali.module.financial.rules.ServiceBillingDocumentRuleUtil;

/**
 * Authorization permissions specific to the Service Billing document.
 * 
 * 
 */
public class ServiceBillingDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(ServiceBillingDocumentAuthorizer.class);

    /**
     * Overrides to always return false because there is never FO routing or FO approval for SB docs.
     * 
     * @see FinancialDocumentAuthorizer#userOwnsAnyAccountingLine(KualiUser, List)
     */
    protected boolean userOwnsAnyAccountingLine(UniversalUser user, List accountingLines) {
        return false;
    }

    /**
     * Overrides parent to return an empty Map since FO routing doesn't apply to the SB doc.
     * 
     * @see org.kuali.core.authorization.TransactionalDocumentAuthorizer#getEditableAccounts(org.kuali.core.document.TransactionalDocument,
     *      KualiUser)
     */
    public Map getEditableAccounts(TransactionalDocument document, UniversalUser user) {
        return new HashMap();
    }

    /**
     * SB docs use FP_SB_CTRL_T to limit income account users by workgroup. Thus a user who is not in any of the workgroups in that
     * table will not be able to add any income accounting lines. This method uses those groups directly for initiation authority,
     * instead of another group, so the administrator does not need to add users to multiple SB groups.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    public void canInitiate(String documentTypeName, UniversalUser user) {
    	boolean canInitiate = false;
        ServiceBillingControl[] controls = SpringServiceLocator.getServiceBillingControlService().getAll();
        for (int i = 0; i < controls.length; i++) {
            if (user.isMember( controls[i].getWorkgroupName() )) {
                canInitiate = true;
            }
        }
        if (!canInitiate) {
            // TODO: Give better message listing the required control workgroup names using DocumentInitiationAuthorizationException
            throw new DocumentTypeAuthorizationException(user.getPersonUserIdentifier(), "initiate", documentTypeName);
    	}
    }

    /**
     * Overrides the error-correct flag for SB income account control. Unlike a copy, an SB error correction's income accounts
     * cannot be changed, so if this user isn't authorized for all those income accounts then he won't be able to save or submit the
     * error correction. We avoid this frustration by hiding that button in the first place.
     * 
     * @see org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        TransactionalDocumentActionFlags flags = (TransactionalDocumentActionFlags) super.getDocumentActionFlags(document, user);
        boolean canUseAllIncomeSectionAccountsBool = false;
        if (flags.getCanErrorCorrect() || flags.getCanCopy()) {
            // canUseAllIncomeSectionAccounts may be an expensive operation, so we only invoke it exactly once only if it's
            // absolutely necessary
            // (i.e. can error correct or copy flags are true)
            // if any of these flags are false, we rely on short circuiting to ignore the value of this variable when
            // any of the flags are false
            canUseAllIncomeSectionAccountsBool = canUseAllIncomeSectionAccounts((ServiceBillingDocument) document, user);
        }
        flags.setCanErrorCorrect(flags.getCanErrorCorrect() && canUseAllIncomeSectionAccountsBool);
        flags.setCanCopy(flags.getCanCopy() && canUseAllIncomeSectionAccountsBool);
        return flags;
    }

    /**
     * @param serviceBillingDocument
     * @param user
     * @return whether the given user is allowed to use all of the accounts in the given SB doc's income accounting lines section
     */
    private static boolean canUseAllIncomeSectionAccounts(ServiceBillingDocument serviceBillingDocument, UniversalUser user) {
        for (SourceAccountingLine sourceAccountingLine : ((List<SourceAccountingLine>) serviceBillingDocument.getSourceAccountingLines())) {
            if (!ServiceBillingDocumentRuleUtil.serviceBillingIncomeAccountIsAccessible(sourceAccountingLine, null, user)) {
                return false;
            }
        }
        return true;
    }
}

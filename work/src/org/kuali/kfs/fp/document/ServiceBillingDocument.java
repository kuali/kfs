/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.document;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This is the business object that represents the ServiceBillingDocument in Kuali. See
 * {@link org.kuali.kfs.fp.document.validation.impl.ServiceBillingDocumentRule} for details on how it differs from
 * {@link InternalBillingDocument}.
 */
public class ServiceBillingDocument extends InternalBillingDocument implements CapitalAssetEditable {

    /**
     * This method further restricts the valid accounting line types exclusively to those with income, expense
     * or liability (in the case of income lines) object type codes
     * only. This is done by calling isIncome() and isExpense() passing the accounting line.
     *
     * @param financialDocument The document used to determine if the accounting line is a debit line.
     * @param accountingLine The accounting line to be analyzed.
     * @return True if the accounting line passed in is an expense, income or liability (in the case of income lines)
     *  (accounting line and meets the rules defined by super.isDebit() method.
     * @see org.kuali.kfs.fp.document.validation.impl.InternalBillingDocumentRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AccountingLine accountingLine = (AccountingLine) postable;
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        Collection<String> objectTypes = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(ServiceBillingDocument.class, "OBJECT_TYPES");
        if (!objectTypes.contains(accountingLine.getObjectTypeCode())) {
            if (accountingLine.getFinancialDocumentLineTypeCode().equals(KFSConstants.TARGET_ACCT_LINE_TYPE_CODE) || !isDebitUtils.isLiability(accountingLine)) {
                throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
            }
        }

        return super.isDebit(postable);
    }

    /**
     * This method sets extra accounting line fields in explicit general ledger pending entries. Internal billing transactions don't
     * have this field.
     *
     * @param financialDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry to be customized.
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        String description = postable.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(description)) {
            explicitEntry.setTransactionLedgerEntryDescription(description);
        }
    }
}

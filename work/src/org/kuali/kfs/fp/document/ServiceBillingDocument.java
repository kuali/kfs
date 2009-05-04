/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.BasicFormatWithLineDescriptionAccountingLineParser;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is the business object that represents the ServiceBillingDocument in Kuali. See
 * {@link org.kuali.kfs.fp.document.validation.impl.ServiceBillingDocumentRule} for details on how it differs from
 * {@link InternalBillingDocument}.
 */
public class ServiceBillingDocument extends InternalBillingDocument implements CapitalAssetEditable {

    private CapitalAssetInformation capitalAssetInformation;
    
    /**
     * @see org.kuali.kfs.fp.document.InternalBillingDocument#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BasicFormatWithLineDescriptionAccountingLineParser();
    }
    
    /**
     * This method further restricts the valid accounting line types exclusively to those with income or expense 
     * object type codes only.  This is done by calling isIncome() and isExpense() passing the accounting line.  
     * 
     * @param financialDocument The document used to determine if the accounting line is a debit line.
     * @param accountingLine The accounting line to be analyzed.
     * @return True if the accounting line passed in is an expense or income accounting line and meets the rules defined
     * by super.isDebit() method.
     * 
     * @see org.kuali.kfs.fp.document.validation.impl.InternalBillingDocumentRule#isDebit(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        AccountingLine accountingLine = (AccountingLine)postable;
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if (!isDebitUtils.isIncome(accountingLine) && !isDebitUtils.isExpense(accountingLine)) {
            throw new IllegalStateException(isDebitUtils.getDebitCalculationIllegalStateExceptionMessage());
        }

        return super.isDebit(postable);
    }
    
    /**
     * This method sets extra accounting line fields in explicit general ledger pending entries. Internal billing transactions 
     * don't have this field.
     * 
     * @param financialDocument The accounting document containing the general ledger pending entries being customized.
     * @param accountingLine The accounting line the explicit general ledger pending entry was generated from.
     * @param explicitEntry The explicit general ledger pending entry to be customized.
     * 
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
    
    /**
     * Gets the capitalAssetInformation attribute. 
     * @return Returns the capitalAssetInformation.
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return ObjectUtils.isNull(capitalAssetInformation)? null : capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    @Deprecated
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }
}

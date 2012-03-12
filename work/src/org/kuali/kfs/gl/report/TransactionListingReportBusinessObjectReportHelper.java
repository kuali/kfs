/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.report;

import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

public class TransactionListingReportBusinessObjectReportHelper extends BusinessObjectReportHelper {
    protected static final int TRANSACTION_LEDGER_ENTRY_DESCRIPTION_MAX_LENGTH = 31;
    
    /**
     * @see org.kuali.kfs.sys.report.BusinessObjectReportHelper#retrievePropertyValue(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    protected Object retrievePropertyValue(BusinessObject businessObject, String propertyName) {
        if ("debitOrBudgetAmount".equals(propertyName) && businessObject instanceof Transaction) {
            Transaction e = (Transaction) businessObject;
            if (KFSConstants.GL_DEBIT_CODE.equals(e.getTransactionDebitCreditCode())) {
                // return the debit amount
                return e.getTransactionLedgerEntryAmount();
            } else if (!KFSConstants.GL_DEBIT_CODE.equals(e.getTransactionDebitCreditCode()) &&
                    !KFSConstants.GL_CREDIT_CODE.equals(e.getTransactionDebitCreditCode())) {
                // return the budget amount
                return e.getTransactionLedgerEntryAmount();
            }
            return KualiDecimal.ZERO;
        }
        if ("creditAmount".equals(propertyName) && businessObject instanceof Transaction) {
            Transaction e = (Transaction) businessObject;
            if (KFSConstants.GL_CREDIT_CODE.equals(e.getTransactionDebitCreditCode())) {
                return e.getTransactionLedgerEntryAmount();
            }
            return KualiDecimal.ZERO;
        }
        // Truncate the description
        if ("transactionLedgerEntryDescription".equals(propertyName) && businessObject instanceof Transaction) {
            Transaction e = (Transaction) businessObject;
            if (ObjectUtils.isNull(e.getTransactionLedgerEntryDescription()) ||
                    (e.getTransactionLedgerEntryDescription().length() <= TRANSACTION_LEDGER_ENTRY_DESCRIPTION_MAX_LENGTH)) {
                return e.getTransactionLedgerEntryDescription();
            } else {
                return e.getTransactionLedgerEntryDescription().substring(0, TRANSACTION_LEDGER_ENTRY_DESCRIPTION_MAX_LENGTH);
            }
        }
        return super.retrievePropertyValue(businessObject, propertyName);
    }

    /**
     * @see org.kuali.kfs.sys.report.BusinessObjectReportHelper#retrievePropertyValueMaximumLength(java.lang.Class, java.lang.String)
     */
    @Override
    protected int retrievePropertyValueMaximumLength(Class<? extends BusinessObject> businessObjectClass, String propertyName) {
        if ("debitOrBudgetAmount".equals(propertyName) || "creditAmount".equals(propertyName)) {
            return super.retrievePropertyValueMaximumLength(businessObjectClass, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        }
        if ("transactionLedgerEntryDescription".equals(propertyName)) {
            return TRANSACTION_LEDGER_ENTRY_DESCRIPTION_MAX_LENGTH;
        }
        return super.retrievePropertyValueMaximumLength(businessObjectClass, propertyName);
    }

}

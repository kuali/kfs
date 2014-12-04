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

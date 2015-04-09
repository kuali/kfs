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
package org.kuali.kfs.module.ld.businessobject.inquiry;

import java.util.Properties;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LedgerEntryForExpenseTransfer;
import org.kuali.kfs.sys.KFSConstants;

/**
 * Inquirable Implementation for Ledger Balance for Expense Transfer. This class is used to generate the URL for the user-defined
 * attributes for the Ledger Balance screen. It is entended the KualiInquirableImpl class, so it covers both the default
 * implementation and customized implemetnation.
 */
public class LedgerBalanceForExpenseTransferInquirableImpl extends LedgerBalanceInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerBalanceForExpenseTransferInquirableImpl.class);

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return LaborConstants.BalanceInquiries.LEDGER_ENTRY_LOOKUPABLE_FOR_EXPENSE_TRANSFER;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return LedgerEntryForExpenseTransfer.class;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());

        String periodCode = (String) getUserDefinedAttributeMap().get(attributeName);
        parameter.put(KFSConstants.UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME, periodCode);
    }
}

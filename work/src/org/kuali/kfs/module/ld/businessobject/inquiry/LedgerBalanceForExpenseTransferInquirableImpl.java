/*
 * Copyright 2007 The Kuali Foundation
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

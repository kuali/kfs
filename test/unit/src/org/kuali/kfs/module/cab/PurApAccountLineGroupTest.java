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
package org.kuali.kfs.module.cab;

import org.kuali.kfs.module.cab.businessobject.AccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PurApAccountLineGroup;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountHistory;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class PurApAccountLineGroupTest extends KualiTestBase {
    private class PurApAccountLineGroupTestable extends AccountLineGroup {
        public PurApAccountLineGroupTestable(PurApAccountingLineBase entry) {
        }
    }

    @Override
    protected void setUp() throws Exception {
    }

    public void testCombineEntry() throws Exception {
    }

    private PurApAccountLineGroup createAccountLineGroup(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode, KualiDecimal amount) {
        PurApAccountingLineBase entry = createEntry(i, chartCode, acctNum, subAcctNum, objCd, subObjCd, fiscalPrd, docNum, refDocNum, dbtCrdtCode, amount);
        PurApAccountLineGroup first = new PurApAccountLineGroup(entry);
        return first;
    }

    private PurApAccountingLineBase createEntry(Integer i, String chartCode, String acctNum, String subAcctNum, String objCd, String subObjCd, String fiscalPrd, String docNum, String refDocNum, String dbtCrdtCode, KualiDecimal amount) {
        PurApAccountingLineBase entry = new PaymentRequestAccountHistory();
        entry.setPostingYear(i);
        entry.setChartOfAccountsCode(chartCode);
        entry.setAccountNumber(acctNum);
        entry.setSubAccountNumber(subAcctNum);
        entry.setFinancialObjectCode(objCd);
        entry.setFinancialSubObjectCode(subObjCd);
        entry.setPostingPeriodCode(fiscalPrd);
        entry.setDocumentNumber(docNum);
        // entry.setReferenceFinancialDocumentNumber(refDocNum);
        // entry.setTransactionDebitCreditCode(dbtCrdtCode);
        // entry.setTransactionLedgerEntryAmount(amount);
        return entry;
    }


}

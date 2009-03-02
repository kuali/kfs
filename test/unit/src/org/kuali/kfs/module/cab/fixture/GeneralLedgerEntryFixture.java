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
package org.kuali.kfs.module.cab.fixture;

import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kns.util.KualiDecimal;

public enum GeneralLedgerEntryFixture {
    REC1 {
        public GeneralLedgerEntry newRecord() {
            GeneralLedgerEntry glEntry = new GeneralLedgerEntry();
            glEntry.setGeneralLedgerAccountIdentifier(1000L);
            glEntry.setUniversityFiscalYear(2009);
            glEntry.setUniversityFiscalPeriodCode("01");
            glEntry.setChartOfAccountsCode("EA");
            glEntry.setAccountNumber("0366500");
            glEntry.setFinancialObjectCode("7015");
            glEntry.setFinancialDocumentTypeCode("PREQ");
            glEntry.setDocumentNumber("33");
            glEntry.setTransactionLedgerEntryAmount(new KualiDecimal(11800));
            glEntry.setReferenceFinancialDocumentNumber("22");
            glEntry.setTransactionDebitCreditCode("D");
            glEntry.setActive(true);
            return glEntry;
        }
    };

    public abstract GeneralLedgerEntry newRecord();

    public static GeneralLedgerEntry createGeneralLedgerEntry() {
        return REC1.newRecord();
    }


}

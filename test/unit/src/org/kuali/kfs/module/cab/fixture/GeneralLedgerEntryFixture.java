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
package org.kuali.kfs.module.cab.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;

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
            glEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            glEntry.refreshReferenceObject("financialObject");
            return glEntry;
        }
    },

    REC2 {
        public GeneralLedgerEntry newRecord() {
            GeneralLedgerEntry glEntry = new GeneralLedgerEntry();
            glEntry.setGeneralLedgerAccountIdentifier(1001L);
            glEntry.setUniversityFiscalYear(2009);
            glEntry.setUniversityFiscalPeriodCode("02");
            glEntry.setChartOfAccountsCode("EA");
            glEntry.setAccountNumber("0308000");
            glEntry.setFinancialObjectCode("7000");
            glEntry.setFinancialDocumentTypeCode("PREQ");
            glEntry.setDocumentNumber("33");
            glEntry.setTransactionLedgerEntryAmount(new KualiDecimal(500));
            glEntry.setReferenceFinancialDocumentNumber("22");
            glEntry.setTransactionDebitCreditCode("D");
            glEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            glEntry.refreshReferenceObject("financialObject");
            return glEntry;
        }
    },

    REC3 {
        public GeneralLedgerEntry newRecord() {
            GeneralLedgerEntry glEntry = new GeneralLedgerEntry();
            glEntry.setGeneralLedgerAccountIdentifier(1002L);
            glEntry.setUniversityFiscalYear(2009);
            glEntry.setUniversityFiscalPeriodCode("02");
            glEntry.setChartOfAccountsCode("BL");
            glEntry.setAccountNumber("2224711");
            glEntry.setFinancialObjectCode("7300");
            glEntry.setFinancialDocumentTypeCode("PREQ");
            glEntry.setDocumentNumber("34");
            glEntry.setTransactionLedgerEntryAmount(new KualiDecimal(1500));
            glEntry.setReferenceFinancialDocumentNumber("22");
            glEntry.setTransactionDebitCreditCode("D");
            glEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            glEntry.refreshReferenceObject("financialObject");
            return glEntry;
        }
    },

    REC4 {
        public GeneralLedgerEntry newRecord() {
            GeneralLedgerEntry glEntry = new GeneralLedgerEntry();
            glEntry.setGeneralLedgerAccountIdentifier(1003L);
            glEntry.setUniversityFiscalYear(2009);
            glEntry.setUniversityFiscalPeriodCode("02");
            glEntry.setChartOfAccountsCode("BL");
            glEntry.setAccountNumber("1024700");
            glEntry.setFinancialObjectCode("7000");
            glEntry.setFinancialDocumentTypeCode("CM");
            glEntry.setDocumentNumber("44");
            glEntry.setTransactionLedgerEntryAmount(new KualiDecimal(1000));
            glEntry.setReferenceFinancialDocumentNumber("22");
            glEntry.setTransactionDebitCreditCode("C");
            glEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            glEntry.refreshReferenceObject("financialObject");
            return glEntry;
        }
    },
    REC5 {
        public GeneralLedgerEntry newRecord() {
            GeneralLedgerEntry glEntry = new GeneralLedgerEntry();
            glEntry.setGeneralLedgerAccountIdentifier(1004L);
            glEntry.setUniversityFiscalYear(2009);
            glEntry.setUniversityFiscalPeriodCode("02");
            glEntry.setChartOfAccountsCode("BL");
            glEntry.setAccountNumber("1024700");
            glEntry.setFinancialObjectCode("7000");
            glEntry.setFinancialDocumentTypeCode("CM");
            glEntry.setDocumentNumber("44");
            glEntry.setTransactionLedgerEntryAmount(new KualiDecimal(500));
            glEntry.setReferenceFinancialDocumentNumber("22");
            glEntry.setTransactionDebitCreditCode("C");
            glEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            glEntry.refreshReferenceObject("financialObject");
            return glEntry;
        }
    };

    public abstract GeneralLedgerEntry newRecord();

    public static List<GeneralLedgerEntry> createGeneralLedgerEntry() {
        List<GeneralLedgerEntry> glEntries = new ArrayList<GeneralLedgerEntry>();
        glEntries.add(REC1.newRecord());
        glEntries.add(REC2.newRecord());
        glEntries.add(REC3.newRecord());
        glEntries.add(REC4.newRecord());
        glEntries.add(REC5.newRecord());
        return glEntries;
    }

}

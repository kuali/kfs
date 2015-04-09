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

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

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum EntryFixture {

    REC1 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("31");
            obj.setTransactionLedgerEntrySequenceNumber(5);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(11800));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC2 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("31");
            obj.setTransactionLedgerEntrySequenceNumber(6);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(11800));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC3 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("31");
            obj.setTransactionLedgerEntrySequenceNumber(7);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7200));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC4 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("31");
            obj.setTransactionLedgerEntrySequenceNumber(8);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7200));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC5 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7000");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("31");
            obj.setTransactionLedgerEntrySequenceNumber(9);
            obj.setTransactionLedgerEntryDescription("Trade in allowance");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(3000));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC6 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("31");
            obj.setTransactionLedgerEntrySequenceNumber(10);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(3000));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC7 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7000");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("32");
            obj.setTransactionLedgerEntrySequenceNumber(3);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(14000));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC8 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("32");
            obj.setTransactionLedgerEntrySequenceNumber(4);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(14000));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC9 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("33");
            obj.setTransactionLedgerEntrySequenceNumber(5);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(11800));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("22");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC10 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("33");
            obj.setTransactionLedgerEntrySequenceNumber(6);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(11800));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("22");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC11 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("33");
            obj.setTransactionLedgerEntrySequenceNumber(7);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7200));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("22");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC12 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("33");
            obj.setTransactionLedgerEntrySequenceNumber(8);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7200));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("22");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC13 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7000");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("34");
            obj.setTransactionLedgerEntrySequenceNumber(3);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(14000));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("22");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC14 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("34");
            obj.setTransactionLedgerEntrySequenceNumber(4);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(14000));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("22");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC15 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("35");
            obj.setTransactionLedgerEntrySequenceNumber(5);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(11800));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("23");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC16 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("35");
            obj.setTransactionLedgerEntrySequenceNumber(6);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(11800));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("23");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC17 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("35");
            obj.setTransactionLedgerEntrySequenceNumber(7);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7200));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("23");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC18 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("35");
            obj.setTransactionLedgerEntrySequenceNumber(8);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7200));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("23");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC19 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7000");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("36");
            obj.setTransactionLedgerEntrySequenceNumber(3);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(14000));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("23");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC20 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("PREQ");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("36");
            obj.setTransactionLedgerEntrySequenceNumber(4);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(14000));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("23");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC21 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7000");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("EX");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("CM");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("41");
            obj.setTransactionLedgerEntrySequenceNumber(1);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7000));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode("R");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC22 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9892");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("EX");
            obj.setFinancialObjectTypeCode("FB");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("CM");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("41");
            obj.setTransactionLedgerEntrySequenceNumber(2);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7000));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode("R");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC23 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7000");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("CM");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("41");
            obj.setTransactionLedgerEntrySequenceNumber(3);
            obj.setTransactionLedgerEntryDescription("BESCO WATER TREATMENT INC");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7000));
            obj.setTransactionDebitCreditCode("C");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC24 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("9041");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("LI");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("CM");
            obj.setFinancialSystemOriginationCode("EP");
            obj.setDocumentNumber("41");
            obj.setTransactionLedgerEntrySequenceNumber(4);
            obj.setTransactionLedgerEntryDescription("TP Generated Offset");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7000));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode("PO");
            obj.setReferenceFinancialSystemOriginationCode("EP");
            obj.setReferenceFinancialDocumentNumber("21");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC25 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("BL");
            obj.setAccountNumber("1023200");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("INV");
            obj.setFinancialSystemOriginationCode("01");
            obj.setDocumentNumber("51");
            obj.setTransactionLedgerEntrySequenceNumber(1);
            obj.setTransactionLedgerEntryDescription("Customer Invoice");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7800));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode(" ");
            obj.setReferenceFinancialSystemOriginationCode(" ");
            obj.setReferenceFinancialDocumentNumber(" ");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC26 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("BL");
            obj.setAccountNumber("1023200");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("8118");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("AS");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("INV");
            obj.setFinancialSystemOriginationCode("01");
            obj.setDocumentNumber("51");
            obj.setTransactionLedgerEntrySequenceNumber(2);
            obj.setTransactionLedgerEntryDescription("Customer Invoice");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(7800));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode(" ");
            obj.setReferenceFinancialSystemOriginationCode(" ");
            obj.setReferenceFinancialDocumentNumber(" ");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC27 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("BL");
            obj.setAccountNumber("0212001");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("7015");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("EE");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("INV");
            obj.setFinancialSystemOriginationCode("01");
            obj.setDocumentNumber("52");
            obj.setTransactionLedgerEntrySequenceNumber(1);
            obj.setTransactionLedgerEntryDescription("Customer Invoice");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(5200));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode(" ");
            obj.setReferenceFinancialSystemOriginationCode(" ");
            obj.setReferenceFinancialDocumentNumber(" ");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    },
    REC28 {

        @Override
        public Entry newRecord() {
            Entry obj = new Entry();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            java.sql.Date date = new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setUniversityFiscalYear(2009);
            obj.setChartOfAccountsCode("BL");
            obj.setAccountNumber("0212001");
            obj.setSubAccountNumber("-----");
            obj.setFinancialObjectCode("8118");
            obj.setFinancialSubObjectCode("---");
            obj.setFinancialBalanceTypeCode("AC");
            obj.setFinancialObjectTypeCode("AS");
            obj.setUniversityFiscalPeriodCode("01");
            obj.setFinancialDocumentTypeCode("INV");
            obj.setFinancialSystemOriginationCode("01");
            obj.setDocumentNumber("52");
            obj.setTransactionLedgerEntrySequenceNumber(2);
            obj.setTransactionLedgerEntryDescription("Customer Invoice");
            obj.setTransactionLedgerEntryAmount(new KualiDecimal(5200));
            obj.setTransactionDebitCreditCode("D");
            obj.setTransactionDate(date);
            obj.setProjectCode("----------");
            obj.setReferenceFinancialDocumentTypeCode(" ");
            obj.setReferenceFinancialSystemOriginationCode(" ");
            obj.setReferenceFinancialDocumentNumber(" ");
            obj.setTransactionEncumbranceUpdateCode(" ");
            obj.setTransactionPostingDate(date);
            obj.setTransactionDateTimeStamp(timeStamp);
            return obj;
        };
    };
    public abstract Entry newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        recs.add(REC4.newRecord());
        recs.add(REC5.newRecord());
        recs.add(REC6.newRecord());
        recs.add(REC7.newRecord());
        recs.add(REC8.newRecord());
        recs.add(REC9.newRecord());
        recs.add(REC10.newRecord());
        recs.add(REC11.newRecord());
        recs.add(REC12.newRecord());
        recs.add(REC13.newRecord());
        recs.add(REC14.newRecord());
        recs.add(REC15.newRecord());
        recs.add(REC16.newRecord());
        recs.add(REC17.newRecord());
        recs.add(REC18.newRecord());
        recs.add(REC19.newRecord());
        recs.add(REC20.newRecord());
        recs.add(REC21.newRecord());
        recs.add(REC22.newRecord());
        recs.add(REC23.newRecord());
        recs.add(REC24.newRecord());
        recs.add(REC25.newRecord());
        recs.add(REC26.newRecord());
        recs.add(REC27.newRecord());
        recs.add(REC28.newRecord());
        return recs;
    }
}

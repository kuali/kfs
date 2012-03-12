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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountRevision;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PaymentRequestAccountRevisionFixture {

    REC1 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(1);
            obj.setItemIdentifier(311);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(750));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC2 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(2);
            obj.setItemIdentifier(312);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(250));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC3 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(3);
            obj.setItemIdentifier(313);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(10800));
            obj.setAccountLinePercent(new BigDecimal(60));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC4 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(4);
            obj.setItemIdentifier(313);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(7200));
            obj.setAccountLinePercent(new BigDecimal(40));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC5 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(5);
            obj.setItemIdentifier(314);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(14000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC6 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(6);
            obj.setItemIdentifier(315);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(750));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC7 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(7);
            obj.setItemIdentifier(316);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(250));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC8 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(8);
            obj.setItemIdentifier(317);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(10800));
            obj.setAccountLinePercent(new BigDecimal(60));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC9 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(9);
            obj.setItemIdentifier(317);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(7200));
            obj.setAccountLinePercent(new BigDecimal(40));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC10 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(10);
            obj.setItemIdentifier(318);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(14000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC11 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(11);
            obj.setItemIdentifier(319);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(750));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC12 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(12);
            obj.setItemIdentifier(320);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(250));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC13 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(13);
            obj.setItemIdentifier(321);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(10800));
            obj.setAccountLinePercent(new BigDecimal(60));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC14 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(14);
            obj.setItemIdentifier(321);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(7200));
            obj.setAccountLinePercent(new BigDecimal(40));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC15 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(15);
            obj.setItemIdentifier(322);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(14000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    },
    REC16 {

        @Override
        public PaymentRequestAccountRevision newRecord() {
            PaymentRequestAccountRevision obj = new PaymentRequestAccountRevision();
            java.sql.Timestamp timeStamp = new java.sql.Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            obj.setAccountRevisionIdentifier(16);
            obj.setItemIdentifier(323);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(-3000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setAccountRevisionTimestamp(timeStamp);
            obj.setPostingYear(2009);
            obj.setPostingPeriodCode("01");
            return obj;
        };
    };
    public abstract PaymentRequestAccountRevision newRecord();

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
        return recs;
    }
}

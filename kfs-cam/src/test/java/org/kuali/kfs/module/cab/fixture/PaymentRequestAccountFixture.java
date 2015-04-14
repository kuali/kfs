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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PaymentRequestAccountFixture {

    REC1 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(411);
            obj.setItemIdentifier(311);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(750));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC2 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(412);
            obj.setItemIdentifier(312);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(250));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC3 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(413);
            obj.setItemIdentifier(313);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(10800));
            obj.setAccountLinePercent(new BigDecimal(60));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC4 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(414);
            obj.setItemIdentifier(313);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(7200));
            obj.setAccountLinePercent(new BigDecimal(40));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC5 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(415);
            obj.setItemIdentifier(314);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(14000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC6 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(426);
            obj.setItemIdentifier(323);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(-3000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC7 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(416);
            obj.setItemIdentifier(315);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(750));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC8 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(417);
            obj.setItemIdentifier(316);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(250));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC9 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(418);
            obj.setItemIdentifier(317);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(10800));
            obj.setAccountLinePercent(new BigDecimal(60));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC10 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(419);
            obj.setItemIdentifier(317);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(7200));
            obj.setAccountLinePercent(new BigDecimal(40));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC11 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(420);
            obj.setItemIdentifier(318);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(14000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC12 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(421);
            obj.setItemIdentifier(319);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(750));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC13 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(422);
            obj.setItemIdentifier(320);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(250));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC14 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(423);
            obj.setItemIdentifier(321);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366500");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(10800));
            obj.setAccountLinePercent(new BigDecimal(60));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC15 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(424);
            obj.setItemIdentifier(321);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366501");
            obj.setFinancialObjectCode("7015");
            obj.setAmount(new KualiDecimal(7200));
            obj.setAccountLinePercent(new BigDecimal(40));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    },
    REC16 {
        @Override
        public PaymentRequestAccount newRecord() {
            PaymentRequestAccount obj = new PaymentRequestAccount();
            obj.setAccountIdentifier(425);
            obj.setItemIdentifier(322);
            obj.setChartOfAccountsCode("EA");
            obj.setAccountNumber("0366503");
            obj.setFinancialObjectCode("7000");
            obj.setAmount(new KualiDecimal(14000));
            obj.setAccountLinePercent(new BigDecimal(100));
            obj.setDisencumberedAmount(new KualiDecimal(0));
            return obj;
        };
    };
    public abstract PaymentRequestAccount newRecord();

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

/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject;

import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class represents a G/L Summary object which contains monthly amounts
 */
public class GlSummary extends Balance{
    private String fundGroup;
    
    private KualiDecimal accountLineAnnualBalanceAmount;
    private KualiDecimal beginningBalanceLineAmount;
    private KualiDecimal contractsGrantsBeginningBalanceAmount;
    private KualiDecimal month1Amount;
    private KualiDecimal month2Amount;
    private KualiDecimal month3Amount;
    private KualiDecimal month4Amount;
    private KualiDecimal month5Amount;
    private KualiDecimal month6Amount;
    private KualiDecimal month7Amount;
    private KualiDecimal month8Amount;
    private KualiDecimal month9Amount;
    private KualiDecimal month10Amount;
    private KualiDecimal month11Amount;
    private KualiDecimal month12Amount;
    private KualiDecimal month13Amount;

    public GlSummary() {
        super();
    }

    public GlSummary(Object[] data) {
        this.setFundGroup((String) data[0]);

        this.setAccountLineAnnualBalanceAmount((KualiDecimal) data[1]);
        this.setBeginningBalanceLineAmount((KualiDecimal) data[2]);
        this.setContractsGrantsBeginningBalanceAmount((KualiDecimal) data[3]);
        this.setMonth1Amount((KualiDecimal) data[4]);
        this.setMonth2Amount((KualiDecimal) data[5]);
        this.setMonth3Amount((KualiDecimal) data[6]);
        this.setMonth4Amount((KualiDecimal) data[7]);
        this.setMonth5Amount((KualiDecimal) data[8]);
        this.setMonth6Amount((KualiDecimal) data[9]);
        this.setMonth7Amount((KualiDecimal) data[10]);
        this.setMonth8Amount((KualiDecimal) data[11]);
        this.setMonth9Amount((KualiDecimal) data[12]);
        this.setMonth10Amount((KualiDecimal) data[13]);
        this.setMonth11Amount((KualiDecimal) data[14]);
        this.setMonth12Amount((KualiDecimal) data[15]);
        this.setMonth13Amount((KualiDecimal) data[16]);
    }

    public void add(GlSummary anotherSummary) {
        beginningBalanceLineAmount = beginningBalanceLineAmount.add(anotherSummary.beginningBalanceLineAmount);
        contractsGrantsBeginningBalanceAmount = contractsGrantsBeginningBalanceAmount.add(anotherSummary.contractsGrantsBeginningBalanceAmount);
        accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount.add(anotherSummary.accountLineAnnualBalanceAmount);
        month1Amount = month1Amount.add(anotherSummary.month1Amount);
        month2Amount = month2Amount.add(anotherSummary.month2Amount);
        month3Amount = month3Amount.add(anotherSummary.month3Amount);
        month4Amount = month4Amount.add(anotherSummary.month4Amount);
        month5Amount = month5Amount.add(anotherSummary.month5Amount);
        month6Amount = month6Amount.add(anotherSummary.month6Amount);
        month7Amount = month7Amount.add(anotherSummary.month7Amount);
        month8Amount = month8Amount.add(anotherSummary.month8Amount);
        month9Amount = month9Amount.add(anotherSummary.month9Amount);
        month10Amount = month10Amount.add(anotherSummary.month10Amount);
        month11Amount = month11Amount.add(anotherSummary.month11Amount);
        month12Amount = month12Amount.add(anotherSummary.month12Amount);
        month13Amount = month13Amount.add(anotherSummary.month13Amount);
    }

    public KualiDecimal getYearBalance() {
        KualiDecimal yearbalance = KualiDecimal.ZERO;
        
        yearbalance = yearbalance.add(this.month1Amount);
        yearbalance = yearbalance.add(this.month2Amount);
        yearbalance = yearbalance.add(this.month3Amount);
        yearbalance = yearbalance.add(this.month4Amount);
        yearbalance = yearbalance.add(this.month5Amount);
        yearbalance = yearbalance.add(this.month6Amount);
        yearbalance = yearbalance.add(this.month7Amount);
        yearbalance = yearbalance.add(this.month8Amount);
        yearbalance = yearbalance.add(this.month9Amount);
        yearbalance = yearbalance.add(this.month10Amount);
        yearbalance = yearbalance.add(this.month11Amount);
        yearbalance = yearbalance.add(this.month12Amount);
        yearbalance = yearbalance.add(this.month13Amount);
        
        return yearbalance;
    }
}

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
package org.kuali.kfs.gl.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class represents a G/L Summary object which contains monthly amounts
 */
public class GlSummary extends Balance{
    
    /**
     * Constructs a GlSummary.java.
     */
    public GlSummary() {
        super();
    }

    /**
     * Constructs a GlSummary.java.
     * @param data
     */
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

    /**
     * @param anotherSummary
     */
    public void add(GlSummary anotherSummary) {
        setBeginningBalanceLineAmount(getBeginningBalanceLineAmount().add(anotherSummary.getBeginningBalanceLineAmount()));
        setContractsGrantsBeginningBalanceAmount(getContractsGrantsBeginningBalanceAmount().add(anotherSummary.getContractsGrantsBeginningBalanceAmount()));
        setAccountLineAnnualBalanceAmount(getAccountLineAnnualBalanceAmount().add(anotherSummary.getAccountLineAnnualBalanceAmount()));
        setMonth1Amount(getMonth1Amount().add(anotherSummary.getMonth1Amount()));
        setMonth2Amount(getMonth2Amount().add(anotherSummary.getMonth2Amount()));
        setMonth3Amount(getMonth3Amount().add(anotherSummary.getMonth3Amount()));
        setMonth4Amount(getMonth4Amount().add(anotherSummary.getMonth4Amount()));
        setMonth5Amount(getMonth5Amount().add(anotherSummary.getMonth5Amount()));
        setMonth6Amount(getMonth6Amount().add(anotherSummary.getMonth6Amount()));
        setMonth7Amount(getMonth7Amount().add(anotherSummary.getMonth7Amount()));
        setMonth8Amount(getMonth8Amount().add(anotherSummary.getMonth8Amount()));
        setMonth9Amount(getMonth9Amount().add(anotherSummary.getMonth9Amount()));
        setMonth10Amount(getMonth10Amount().add(anotherSummary.getMonth10Amount()));
        setMonth11Amount(getMonth11Amount().add(anotherSummary.getMonth11Amount()));
        setMonth12Amount(getMonth12Amount().add(anotherSummary.getMonth12Amount()));
        setMonth13Amount(getMonth13Amount().add(anotherSummary.getMonth13Amount()));
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.Balance#getYearBalance()
     */
    @Override
    public KualiDecimal getYearBalance() {
        KualiDecimal yearbalance = KualiDecimal.ZERO;
        
        yearbalance = yearbalance.add(this.getMonth1Amount());
        yearbalance = yearbalance.add(this.getMonth2Amount());
        yearbalance = yearbalance.add(this.getMonth3Amount());
        yearbalance = yearbalance.add(this.getMonth4Amount());
        yearbalance = yearbalance.add(this.getMonth5Amount());
        yearbalance = yearbalance.add(this.getMonth6Amount());
        yearbalance = yearbalance.add(this.getMonth7Amount());
        yearbalance = yearbalance.add(this.getMonth8Amount());
        yearbalance = yearbalance.add(this.getMonth9Amount());
        yearbalance = yearbalance.add(this.getMonth10Amount());
        yearbalance = yearbalance.add(this.getMonth11Amount());
        yearbalance = yearbalance.add(this.getMonth12Amount());
        yearbalance = yearbalance.add(this.getMonth13Amount());
        
        return yearbalance;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.Balance#getYearToDayBalance()
     */
    @Override
    public KualiDecimal getYearToDayBalance() {
        KualiDecimal yearToDayBalance = KualiDecimal.ZERO;
        
        yearToDayBalance = yearToDayBalance.add(this.getAccountLineAnnualBalanceAmount());
        yearToDayBalance = yearToDayBalance.add(this.getBeginningBalanceLineAmount());
        yearToDayBalance = yearToDayBalance.add(this.getContractsGrantsBeginningBalanceAmount());        
        
        return yearToDayBalance;
    }
    
    
}

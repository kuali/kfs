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

import java.util.LinkedHashMap;

/**
 * This class represents a poster output summary entry
 */
public class PosterOutputSummaryEntry extends PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal {
    private String fundGroup;
    
    /**
     * Constructs a PosterOutputSummaryEntry object
     * @param balanceTypeCode the balance type code to set
     * @param universityFiscalYear the fiscal year to set
     * @param fiscalPeriodCode the fiscal period code to set
     * @param fundGroupCode the fund group code to set
     */
    public PosterOutputSummaryEntry(String balanceTypeCode, Integer universityFiscalYear, String fiscalPeriodCode, String fundGroupCode) {
        super(balanceTypeCode, universityFiscalYear, fiscalPeriodCode);
        this.fundGroup = fundGroupCode;
    }

    public String getFundGroup() {
        return fundGroup;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("universityFiscalYear", this.getUniversityFiscalYear());
        pks.put("fiscalPeriodCode",this.getFiscalPeriodCode());
        pks.put("balanceTypeCode",this.getBalanceTypeCode());
        pks.put("fundGroup",this.getFundGroup());
        pks.put("objectTypeCode",this.getObjectTypeCode());
        pks.put("creditAmount",this.getCreditAmount());
        pks.put("debitAmount",this.getDebitAmount());
        pks.put("budgetAmount",this.getBudgetAmount());
        pks.put("netAmount",this.getNetAmount());
        return pks;
    }
    
}

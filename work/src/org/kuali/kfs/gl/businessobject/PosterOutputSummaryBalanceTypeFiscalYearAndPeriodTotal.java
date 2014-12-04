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

import org.kuali.kfs.sys.KFSKeyConstants;

/**
 * A poster output summary total line, for transactions with a given fiscal year, fiscal period, and balance type
 */
public class PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal extends PosterOutputSummaryBalanceTypeFiscalYearTotal {
    private String fiscalPeriodCode;
    
    /**
     * Constructs a PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal
     * @param balanceTypeCode the balance type code to total
     * @param universityFiscalYear the fiscal year to total
     * @param universityFiscalPeriodCode the fiscal period code to total
     */
    public PosterOutputSummaryBalanceTypeFiscalYearAndPeriodTotal(String balanceTypeCode, Integer universityFiscalYear, String universityFiscalPeriodCode) {
        super(balanceTypeCode, universityFiscalYear);
        this.fiscalPeriodCode = universityFiscalPeriodCode;
    }
    
    /**
     * @return the fiscal period associated with this total line
     */
    public String getFiscalPeriodCode() {
        return fiscalPeriodCode;
    }
    
    /**
     * @see org.kuali.kfs.gl.businessobject.PosterOutputSummaryBalanceTypeFiscalYearTotal#getSummaryMessageName()
     */
    
    protected String getSummaryMessageName() {
        return KFSKeyConstants.MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_BALANCE_TYPE_FISCAL_YEAR_AND_PERIOD_TOTAL;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.PosterOutputSummaryBalanceTypeFiscalYearTotal#getSummaryMessageParameters()
     */
    @Override
    protected String[] getSummaryMessageParameters() {
        return new String[] { this.getFiscalPeriodCode(), this.getUniversityFiscalYear().toString(), this.getBalanceTypeCode() };
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("universityFiscalYear", this.getUniversityFiscalYear());
        pks.put("fiscalPeriodCode",this.getFiscalPeriodCode());
        pks.put("balanceTypeCode",this.getBalanceTypeCode());
        pks.put("objectTypeCode",this.getObjectTypeCode());
        pks.put("creditAmount",this.getCreditAmount());
        pks.put("debitAmount",this.getDebitAmount());
        pks.put("budgetAmount",this.getBudgetAmount());
        pks.put("netAmount",this.getNetAmount());
        return pks;
    }
}

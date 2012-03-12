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

/*
 * Copyright 2006 The Kuali Foundation
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

/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo.codes;

import org.kuali.core.bo.KualiCodeBase;


/**
 * This class is the business object for the Balance Type object.
 */
public class BalanceTyp extends KualiCodeBase {

    private String financialBalanceTypeShortNm;
    private boolean financialOffsetGenerationIndicator;
    private boolean finBalanceTypeEncumIndicator;

    /**
     * Constructs a BalanceTyp.java.
     */
    public BalanceTyp() {
        super.setActive(true); // always active, plus no column in the table
    }

    /**
     * @param typeCode
     */
    public BalanceTyp(String typeCode) {
        this();
        setCode(typeCode);
    }

    /**
     * @return Returns the financialBalanceTypeName.
     */

    public String getFinancialBalanceTypeName() {
        return this.getName();
    }

    /**
     * @param financialBalanceTypeName The financialBalanceTypeName to set.
     */
    public void setFinancialBalanceTypeName(String financialBalanceTypeName) {
        this.setName(financialBalanceTypeName);
    }

    /**
     * @return Returns the financialBalanceTypeCode.
     */

    public String getFinancialBalanceTypeCode() {
        return this.getCode();
    }

    /**
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.setCode(financialBalanceTypeCode);
    }

    /**
     * @return Returns the finBalanceTypeEncumIndicator.
     */
    public boolean isFinBalanceTypeEncumIndicator() {
        return finBalanceTypeEncumIndicator;
    }

    /**
     * @param finBalanceTypeEncumIndicator The finBalanceTypeEncumIndicator to set.
     */
    public void setFinBalanceTypeEncumIndicator(boolean finBalanceTypeEncumIndicator) {
        this.finBalanceTypeEncumIndicator = finBalanceTypeEncumIndicator;
    }

    /**
     * @return Returns the financialBalanceTypeShortNm.
     */

    public String getFinancialBalanceTypeShortNm() {
        return financialBalanceTypeShortNm;
    }

    /**
     * @param financialBalanceTypeShortNm The financialBalanceTypeShortNm to set.
     */
    public void setFinancialBalanceTypeShortNm(String financialBalanceTypeShortNm) {
        this.financialBalanceTypeShortNm = financialBalanceTypeShortNm;
    }

    /**
     * @return Returns the financialOffsetGenerationIndicator.
     */

    public boolean isFinancialOffsetGenerationIndicator() {
        return financialOffsetGenerationIndicator;
    }

    /**
     * @param financialOffsetGenerationIndicator The financialOffsetGenerationIndicator to set.
     */
    public void setFinancialOffsetGenerationIndicator(boolean financialOffsetGenerationIndicator) {
        this.financialOffsetGenerationIndicator = financialOffsetGenerationIndicator;
    }
}
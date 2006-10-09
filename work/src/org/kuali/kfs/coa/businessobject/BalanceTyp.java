/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.bo.codes;

import org.kuali.core.bo.KualiCodeBase;


/**
 * This class is the business object for the Balance Type object.
 * 
 * 
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
     * 
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
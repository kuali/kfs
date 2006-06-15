package org.kuali.module.chart.bo;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

import org.kuali.core.bo.KualiCodeBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ObjectType extends KualiCodeBase {

    /**
     * Default no-arg constructor.
     */
    public ObjectType() {
        super.setActive(true); // always active
    }

    public ObjectType(String string) { // FIXME eliminate this constructor
        throw new RuntimeException();
    }

    private String finObjectTypeDebitcreditCd;
    private boolean finObjectTypeIcrSelectionIndicator;
    private boolean fundBalanceIndicator;
    private String financialReportingSortCode;


    /**
     * Gets the finObjectTypeDebitcreditCd attribute.
     * 
     * @return - Returns the finObjectTypeDebitcreditCd
     * 
     */
    public String getFinObjectTypeDebitcreditCd() {
        return finObjectTypeDebitcreditCd;
    }

    /**
     * Sets the finObjectTypeDebitcreditCd attribute.
     * 
     * @param finObjectTypeDebitcreditCd The finObjectTypeDebitcreditCd to set.
     * 
     */
    public void setFinObjectTypeDebitcreditCd(String finObjectTypeDebitcreditCd) {
        this.finObjectTypeDebitcreditCd = finObjectTypeDebitcreditCd;
    }

    /**
     * Gets the finObjectTypeIcrSelectionIndicator attribute.
     * 
     * @return - Returns the finObjectTypeIcrSelectionIndicator
     * 
     */
    public boolean isFinObjectTypeIcrSelectionIndicator() {
        return finObjectTypeIcrSelectionIndicator;
    }

    /**
     * Sets the finObjectTypeIcrSelectionIndicator attribute.
     * 
     * @param finObjectTypeIcrSelectionIndicator The finObjectTypeIcrSelectionIndicator to set.
     * 
     */
    public void setFinObjectTypeIcrSelectionIndicator(boolean finObjectTypeIcrSelectionIndicator) {
        this.finObjectTypeIcrSelectionIndicator = finObjectTypeIcrSelectionIndicator;
    }

    /**
     * Gets the fundBalanceIndicator attribute.
     * 
     * @return - Returns the fundBalanceIndicator
     * 
     */
    public boolean isFundBalanceIndicator() {
        return fundBalanceIndicator;
    }

    /**
     * Sets the fundBalanceIndicator attribute.
     * 
     * @param fundBalanceIndicator The fundBalanceIndicator to set.
     * 
     */
    public void setFundBalanceIndicator(boolean fundBalanceIndicator) {
        this.fundBalanceIndicator = fundBalanceIndicator;
    }

    /**
     * Gets the financialReportingSortCode attribute.
     * 
     * @return - Returns the financialReportingSortCode
     * 
     */
    public String getFinancialReportingSortCode() {
        return financialReportingSortCode;
    }

    /**
     * Sets the financialReportingSortCode attribute.
     * 
     * @param financialReportingSortCode The financialReportingSortCode to set.
     * 
     */
    public void setFinancialReportingSortCode(String financialReportingSortCode) {
        this.financialReportingSortCode = financialReportingSortCode;
    }

}

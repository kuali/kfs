/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/ObjectType.java,v $
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
package org.kuali.module.chart.bo;

import org.kuali.core.bo.KualiCodeBase;

/**
 * 
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
     * @return Returns the finObjectTypeDebitcreditCd
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
     * @return Returns the finObjectTypeIcrSelectionIndicator
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
     * @return Returns the fundBalanceIndicator
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
     * @return Returns the financialReportingSortCode
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

/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.bean;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

public class AccountingLineDistributionKey implements Comparable<AccountingLineDistributionKey> {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String subAccountNumber;
    private String financialSubObjectCode;
    private String projectCode;
    private String organizationReferenceId;
    private String cardType;
    
    /**
     * This constructor is used for key involving object and card type
     * 
     * @param financialObjectCode
     * @param cardType
     */
    public AccountingLineDistributionKey(String financialObjectCode, String cardType) {
        this.financialObjectCode = financialObjectCode;
        this.cardType = cardType;
    }
    
    /**
     * This constructor is used for comparison for accounting line only items
     * 
     * @param line
     */
    public AccountingLineDistributionKey(SourceAccountingLine line) {
        this.chartOfAccountsCode = line.getChartOfAccountsCode();
        this.accountNumber = line.getAccountNumber();
        this.financialObjectCode = line.getFinancialObjectCode();
        this.subAccountNumber = line.getSubAccountNumber();
        this.financialSubObjectCode = line.getFinancialSubObjectCode();
        this.projectCode = line.getProjectCode();
        this.organizationReferenceId = line.getOrganizationReferenceId();
    }

    public String getCardType() {
        return cardType;
    }
    
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        //for Map containKeys and List - contains checking
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        //for Map containKeys checking
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AccountingLineDistributionKey o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }

}
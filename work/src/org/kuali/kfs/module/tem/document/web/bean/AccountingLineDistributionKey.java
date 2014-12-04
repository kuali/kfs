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

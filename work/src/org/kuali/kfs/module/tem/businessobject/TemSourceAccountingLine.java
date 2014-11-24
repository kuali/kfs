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
package org.kuali.kfs.module.tem.businessobject;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

public class TemSourceAccountingLine extends SourceAccountingLine implements TemAccountingLine {
    private String cardType;

    /**
     * Gets the cardType attribute.
     * @return Returns the cardType.
     */
    @Override
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the cardType attribute value.
     * @param cardType The cardType to set.
     */
    @Override
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public TemSourceAccountingLine() {
        super();
    }


    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map getValuesMap() {
        Map temp = super.getValuesMap();
        return temp;
    }

    /**
     * Override needed for PURAP GL entry creation (hjs) - please do not add "amount" to this method
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AccountingLine)) {
            return false;
        }
        AccountingLine accountingLine = (AccountingLine) obj;
        return new EqualsBuilder().append(this.getChartOfAccountsCode(), accountingLine.getChartOfAccountsCode()).append(this.getAccountNumber(), accountingLine.getAccountNumber()).append(this.getSubAccountNumber(), accountingLine.getSubAccountNumber()).append(this.getFinancialObjectCode(), accountingLine.getFinancialObjectCode()).append(this.getFinancialSubObjectCode(), accountingLine.getFinancialSubObjectCode()).append(this.getProjectCode(), accountingLine.getProjectCode()).append(this.getOrganizationReferenceId(), accountingLine.getOrganizationReferenceId()).append(this.getAmount(), accountingLine.getAmount()).isEquals();
    }
}

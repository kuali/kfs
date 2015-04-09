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

import java.math.BigDecimal;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

public class TemDistributionAccountingLine extends SourceAccountingLine implements TemAccountingLine {
    private BigDecimal accountLinePercent = new BigDecimal(0);
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

    public TemDistributionAccountingLine() {
        super();
    }

    /**
     * Gets the accountLinePercent attribute.
     * @return Returns the accountLinePercent.
     */
    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    /**
     * Sets the accountLinePercent attribute value.
     * @param accountLinePercent The accountLinePercent to set.
     */
    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }


    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map getValuesMap() {
        Map temp = super.getValuesMap();
        temp.put("accountLinePercent", accountLinePercent);
        return temp;
    }
}

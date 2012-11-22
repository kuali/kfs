/*
 * Copyright 2011 The Kuali Foundation.
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

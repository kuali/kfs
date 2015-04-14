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

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Bean class used to hold values for creating the view for Accounting Distribution on the
 * {@link TravelReimbursementDocument} page
 */
public class AccountingDistribution extends PersistableBusinessObjectBase implements java.io.Serializable {
    private String objectCode;
    private String objectCodeName;
    private String cardType;
    private KualiDecimal subTotal;
    private KualiDecimal remainingAmount;
    private Boolean selected = Boolean.TRUE;
    private Boolean disabled = Boolean.FALSE;

    /**
     * Gets the selected attribute.
     * Defaults to false if remaining amount is zero.
     * @return Returns the selected.
     */
    public Boolean getSelected() {
        return selected && remainingAmount.isGreaterThan(KualiDecimal.ZERO);
    }

    /**
     * Sets the selected attribute value.
     * @param selected The selected to set.
     */
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public AccountingDistribution() {
        setSubTotal(KualiDecimal.ZERO);
        setRemainingAmount(KualiDecimal.ZERO);
    }

    /**
     *
     * @param objectCode new objectCode to assign
     */
    public void setObjectCode(final String objectCode) {
        this.objectCode = objectCode;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCodeName(final String objectCodeName) {
        this.objectCodeName = objectCodeName;
    }

    public String getObjectCodeName() {
        return objectCodeName;
    }

    /**
     * Gets the cardType attribute.
     * @return Returns the cardType.
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the cardType attribute value.
     * @param cardType The cardType to set.
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setSubTotal(final KualiDecimal subTotal) {
        if (subTotal != null) {
            this.subTotal = subTotal;
        }
    }

    public KualiDecimal getSubTotal() {
        return this.subTotal;
    }

    public void setRemainingAmount(final KualiDecimal remainingAmount) {
        if (remainingAmount != null && KualiDecimal.ZERO.isLessEqual(remainingAmount)) {
            this.remainingAmount = remainingAmount;
        }
    }

    public KualiDecimal getRemainingAmount() {
        return this.remainingAmount;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}

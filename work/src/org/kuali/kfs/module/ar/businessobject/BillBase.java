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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Base class so the Bill and InvoiceBill BOs don't have to duplicate code.
 */
public class BillBase extends PersistableBusinessObjectBase {

    private Long billIdentifier;
    private Long billNumber;
    private String billDescription;
    private Date billDate;
    private KualiDecimal estimatedAmount;

    /**
     * Gets the billDate attribute.
     *
     * @return Returns the billDate.
     */
    public Date getBillDate() {
        return billDate;
    }

    /**
     * Gets the billDescription attribute.
     *
     * @return Returns the billDescription.
     */
    public String getBillDescription() {
        return billDescription;
    }

    /**
     * Gets the billIdentifier attribute.
     *
     * @return Returns the billIdentifier.
     */
    public Long getBillIdentifier() {
        return billIdentifier;
    }

    /**
     * Gets the billNumber attribute.
     *
     * @return Returns the billNumber.
     */
    public Long getBillNumber() {
        return billNumber;
    }

    /**
     * Gets the estimatedAmount attribute.
     *
     * @return Returns the estimatedAmount.
     */
    public KualiDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    /**
     * Sets the billDate attribute value.
     *
     * @param billDate The billDate to set.
     */
    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    /**
     * Sets the billDescription attribute value.
     *
     * @param billDescription The billDescription to set.
     */
    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    /**
     * Sets the billIdentifier attribute value.
     *
     * @param billIdentifier The billIdentifier to set.
     */
    public void setBillIdentifier(Long billIdentifier) {
        this.billIdentifier = billIdentifier;
    }

    /**
     * Sets the billNumber attribute value.
     *
     * @param billNumber The billNumber to set.
     */
    public void setBillNumber(Long billNumber) {
        this.billNumber = billNumber;
    }

    /**
     * Sets the estimatedAmount attribute value.
     *
     * @param estimatedAmount The estimatedAmount to set.
     */
    public void setEstimatedAmount(KualiDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("billDescription", this.billDescription);

        if (this.billNumber != null) {
            m.put("billNumber", this.billNumber.toString());
        }

        if (this.billIdentifier != null) {
            m.put("billIdentifier", this.billIdentifier.toString());
        }

        if (this.billDate != null) {
            m.put("billDate", this.billDate.toString());
        }

        if (this.estimatedAmount != null) {
            m.put("estimatedAmount", this.estimatedAmount.toString());
        }

        return m;
    }
}

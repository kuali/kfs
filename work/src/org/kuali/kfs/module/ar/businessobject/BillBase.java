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

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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Method of Invoice Transmission
 */
public class InvoiceTransmissionMethod extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String invoiceTransmissionMethodCode;
    private String invoiceTransmissionMethodDescription;
    private boolean active;


    public String getInvoiceTransmissionMethodCode() {
        return invoiceTransmissionMethodCode;
    }

    public void setInvoiceTransmissionMethodCode(String invoiceTransmissionMethodCode) {
        this.invoiceTransmissionMethodCode = invoiceTransmissionMethodCode;
    }

    public String getInvoiceTransmissionMethodDescription() {
        return invoiceTransmissionMethodDescription;
    }

    public void setInvoiceTransmissionMethodDescription(String invoiceTransmissionMethodDescription) {
        this.invoiceTransmissionMethodDescription = invoiceTransmissionMethodDescription;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("invoiceTransmissionMethodCode", this.invoiceTransmissionMethodCode);
        m.put("invoiceTransmissionMethodDescription", this.invoiceTransmissionMethodDescription);
        return m;
    }

}

/*
 * Copyright 2006-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Electronic Invoice Reject Reason Type Code Business Object.
 */
public class ElectronicInvoiceRejectReasonType extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String invoiceRejectReasonTypeCode;
    private String invoiceRejectReasonTypeDescription;
    /*
     * Indicates whether this reject reason will cause a INVOICE (if true) or a FILE (if false) reject document.
     */
    private boolean invoiceFailureIndicator;
    private boolean performMatchingIndicator;
    private boolean active;

    /**
     * Default constructor.
     */
    public ElectronicInvoiceRejectReasonType() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.active = dataObjectMaintenanceCodeActiveIndicator;
    }

    public String getInvoiceRejectReasonTypeCode() {
        return invoiceRejectReasonTypeCode;
    }

    public void setInvoiceRejectReasonTypeCode(String invoiceRejectReasonTypeCode) {
        this.invoiceRejectReasonTypeCode = invoiceRejectReasonTypeCode;
    }

    public String getInvoiceRejectReasonTypeDescription() {
        return invoiceRejectReasonTypeDescription;
    }

    public void setInvoiceRejectReasonTypeDescription(String invoiceRejectReasonTypeDescription) {
        this.invoiceRejectReasonTypeDescription = invoiceRejectReasonTypeDescription;
    }

    public boolean isInvoiceFailureIndicator() {
        return invoiceFailureIndicator;
    }

    public void setInvoiceFailureIndicator(boolean invoiceFailureIndicator) {
        this.invoiceFailureIndicator = invoiceFailureIndicator;
    }

    public boolean isPerformMatchingIndicator() {
        return performMatchingIndicator;
    }

    public void setPerformMatchingIndicator(boolean performMatchingIndicator) {
        this.performMatchingIndicator = performMatchingIndicator;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("invoiceRejectReasonTypeCode", this.invoiceRejectReasonTypeCode);
        return m;
    }
}


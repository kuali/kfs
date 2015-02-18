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


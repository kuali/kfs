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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Vendor Stipulation.
 */
public class PurchaseOrderVendorStipulation extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer purchaseOrderVendorStipulationIdentifier;
    private String vendorStipulationDescription;
    private String vendorStipulationAuthorEmployeeIdentifier;
    private Date vendorStipulationCreateDate;

    private PurchaseOrderDocument purchaseOrder;

    public PurchaseOrderVendorStipulation() {
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getPurchaseOrderVendorStipulationIdentifier() {
        return purchaseOrderVendorStipulationIdentifier;
    }

    public void setPurchaseOrderVendorStipulationIdentifier(Integer purchaseOrderVendorStipulationIdentifier) {
        this.purchaseOrderVendorStipulationIdentifier = purchaseOrderVendorStipulationIdentifier;
    }

    public String getVendorStipulationDescription() {
        return vendorStipulationDescription;
    }

    public void setVendorStipulationDescription(String vendorStipulationDescription) {
        this.vendorStipulationDescription = vendorStipulationDescription;
    }

    public String getVendorStipulationAuthorEmployeeIdentifier() {
        return vendorStipulationAuthorEmployeeIdentifier;
    }

    public void setVendorStipulationAuthorEmployeeIdentifier(String vendorStipulationAuthorEmployeeIdentifier) {
        this.vendorStipulationAuthorEmployeeIdentifier = vendorStipulationAuthorEmployeeIdentifier;
    }

    public Date getVendorStipulationCreateDate() {
        return vendorStipulationCreateDate;
    }

    public void setVendorStipulationCreateDate(Date vendorStipulationCreateDate) {
        this.vendorStipulationCreateDate = vendorStipulationCreateDate;
    }

    public PurchaseOrderDocument getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the purchaseOrder attribute.
     * 
     * @param purchaseOrder The purchaseOrder to set.
     * @deprecated
     */
    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.purchaseOrderVendorStipulationIdentifier != null) {
            m.put("purchaseOrderVendorStipulationIdentifier", this.purchaseOrderVendorStipulationIdentifier.toString());
        }
        return m;
    }

}

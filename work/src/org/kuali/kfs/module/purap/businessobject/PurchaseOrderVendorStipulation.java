/*
 * Copyright 2006 The Kuali Foundation
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

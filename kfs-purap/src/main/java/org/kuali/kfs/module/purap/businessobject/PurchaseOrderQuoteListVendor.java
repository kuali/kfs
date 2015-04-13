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

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Purchase Order Quote List Vendor Business Object.
 */
public class PurchaseOrderQuoteListVendor extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected Integer purchaseOrderQuoteListIdentifier;
    protected Integer vendorHeaderGeneratedIdentifier;
    protected Integer vendorDetailAssignedIdentifier;
    protected boolean active;
    
    protected PurchaseOrderQuoteList purchaseOrderQuoteList;
    protected VendorDetail vendorDetail;

    /**
     * Default constructor.
     */
    public PurchaseOrderQuoteListVendor() {

    }

    public Integer getPurchaseOrderQuoteListIdentifier() {
        return purchaseOrderQuoteListIdentifier;
    }

    public void setPurchaseOrderQuoteListIdentifier(Integer purchaseOrderQuoteListIdentifier) {
        this.purchaseOrderQuoteListIdentifier = purchaseOrderQuoteListIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public PurchaseOrderQuoteList getPurchaseOrderQuoteList() {
        return purchaseOrderQuoteList;
    }

    /**
     * Sets the purchaseOrderQuoteList attribute value.
     * 
     * @param purchaseOrderQuoteList The purchaseOrderQuoteList to set.
     * @deprecated
     */
    public void setPurchaseOrderQuoteList(PurchaseOrderQuoteList purchaseOrderQuoteList) {
        this.purchaseOrderQuoteList = purchaseOrderQuoteList;
    }

    public VendorDetail getVendorDetail() {
        if (vendorHeaderGeneratedIdentifier != null && vendorDetailAssignedIdentifier != null && (vendorDetail == null || vendorDetail.getVendorHeaderGeneratedIdentifier() != vendorHeaderGeneratedIdentifier || vendorDetail.getVendorDetailAssignedIdentifier() != vendorDetailAssignedIdentifier)) {
            vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(vendorHeaderGeneratedIdentifier, vendorDetailAssignedIdentifier);    
        }
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.purchaseOrderQuoteListIdentifier != null) {
            m.put("purchaseOrderQuoteListIdentifier", this.purchaseOrderQuoteListIdentifier.toString());
        }
        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }
        if (this.vendorDetailAssignedIdentifier != null) {
            m.put("vendorDetailAssignedIdentifier", this.vendorDetailAssignedIdentifier.toString());
        }
        return m;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}

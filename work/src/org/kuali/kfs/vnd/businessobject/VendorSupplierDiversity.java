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

package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Relationship between a Vendor and a <code>SupplierDiversity</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.SupplierDiversity
 */
public class VendorSupplierDiversity extends PersistableBusinessObjectBase implements VendorRoutingComparable, MutableInactivatable {
    private static Logger LOG = Logger.getLogger(VendorSupplierDiversity.class);

    private Integer vendorHeaderGeneratedIdentifier;
    private String vendorSupplierDiversityCode;
    private boolean active;

    private VendorHeader vendorHeader;
    private SupplierDiversity vendorSupplierDiversity;


    /**
     * Default constructor.
     */
    public VendorSupplierDiversity() {

    }

    public Integer getVendorHeaderGeneratedIdentifier() {

        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public String getVendorSupplierDiversityCode() {

        return vendorSupplierDiversityCode;
    }

    public void setVendorSupplierDiversityCode(String vendorSupplierDiversityCode) {
        this.vendorSupplierDiversityCode = vendorSupplierDiversityCode;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public VendorHeader getVendorHeader() {

        return vendorHeader;
    }

    /**
     * Sets the vendorHeader attribute value.
     * 
     * @param vendorHeader The vendorHeader to set.
     * @deprecated
     */
    public void setVendorHeader(VendorHeader vendorHeader) {
        this.vendorHeader = vendorHeader;
    }

    public SupplierDiversity getVendorSupplierDiversity() {

        return vendorSupplierDiversity;
    }

    /**
     * Sets the vendorSupplierDiversity attribute value.
     * 
     * @param vendorSupplierDiversity The vendorSupplierDiversity to set.
     * @deprecated
     */
    public void setVendorSupplierDiversity(SupplierDiversity vendorSupplierDiversity) {
        this.vendorSupplierDiversity = vendorSupplierDiversity;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }
        m.put("vendorSupplierDiversityCode", this.vendorSupplierDiversityCode);

        return m;
    }

    /**
     * @see org.kuali.kfs.vnd.document.routing.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting(Object toCompare) {
        LOG.debug("Entering isEqualForRouting.");
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorSupplierDiversity)) {

            return false;
        }
        else {
            VendorSupplierDiversity vsd = (VendorSupplierDiversity) toCompare;

            return new EqualsBuilder().append(this.getVendorHeaderGeneratedIdentifier(), vsd.getVendorHeaderGeneratedIdentifier()).append(this.getVendorSupplierDiversityCode(), vsd.getVendorSupplierDiversityCode()).isEquals();
        }
    }

}

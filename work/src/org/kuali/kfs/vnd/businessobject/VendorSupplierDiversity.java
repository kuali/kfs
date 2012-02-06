/*
 * Copyright 2007 The Kuali Foundation
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

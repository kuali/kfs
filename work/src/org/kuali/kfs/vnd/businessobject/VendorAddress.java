/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.vendor.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * Address to be associated with a particular Vendor.
 */
public class VendorAddress extends PersistableBusinessObjectBase implements VendorRoutingComparable, Inactivateable {
    private static Logger LOG = Logger.getLogger(VendorAddress.class);

    private Integer vendorAddressGeneratedIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorAddressTypeCode;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorZipCode;
    private String vendorCountryCode;
    private String vendorAttentionName;
    private String vendorAddressInternationalProvinceName;
    private String vendorAddressEmailAddress;
    private String vendorBusinessToBusinessUrlAddress;
    private String vendorFaxNumber;
    private boolean vendorDefaultAddressIndicator;
    private boolean active;

    private List<VendorDefaultAddress> vendorDefaultAddresses;

    private VendorDetail vendorDetail;
    private AddressType vendorAddressType;
    private State vendorState;
    private Country vendorCountry;

    /**
     * Default constructor.
     */
    public VendorAddress() {
        vendorDefaultAddresses = new TypedArrayList(VendorDefaultAddress.class);
    }

    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
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

    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    public String getVendorAddressEmailAddress() {
        return vendorAddressEmailAddress;
    }

    public void setVendorAddressEmailAddress(String vendorAddressEmailAddress) {
        this.vendorAddressEmailAddress = vendorAddressEmailAddress;
    }

    public String getVendorAddressTypeCode() {
        return vendorAddressTypeCode;
    }

    public void setVendorAddressTypeCode(String vendorAddressTypeCode) {
        this.vendorAddressTypeCode = vendorAddressTypeCode;
    }

    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    public String getVendorCityName() {
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public String getVendorStateCode() {
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    public String getVendorZipCode() {
        return vendorZipCode;
    }

    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }

    public String getVendorBusinessToBusinessUrlAddress() {
        return vendorBusinessToBusinessUrlAddress;
    }

    public void setVendorBusinessToBusinessUrlAddress(String vendorBusinessToBusinessUrlAddress) {
        this.vendorBusinessToBusinessUrlAddress = vendorBusinessToBusinessUrlAddress;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute.
     * 
     * @param vendorDetail The vendorDetail to set.
     * @deprecated
     */
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public AddressType getVendorAddressType() {
        return vendorAddressType;
    }

    /**
     * Sets the vendorAddressType attribute.
     * 
     * @param vendorAddressType The vendorAddressType to set.
     * @deprecated
     */
    public void setVendorAddressType(AddressType vendorAddressType) {
        this.vendorAddressType = vendorAddressType;
    }

    public State getVendorState() {
        return vendorState;
    }

    /**
     * Sets the vendorState attribute.
     * 
     * @param vendorState The vendorState to set.
     * @deprecated
     */
    public void setVendorState(State vendorState) {
        this.vendorState = vendorState;
    }

    public Country getVendorCountry() {
        return vendorCountry;
    }

    /**
     * Sets the vendorCountry attribute.
     * 
     * @param vendorCountry The vendorCountry to set.
     * @deprecated
     */
    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    /**
     * Gets the vendorFaxNumber attribute.
     * 
     * @return Returns the vendorFaxNumber.
     */
    public String getVendorFaxNumber() {
        return vendorFaxNumber;
    }

    /**
     * Sets the vendorFaxNumber attribute value.
     * 
     * @param vendorFaxNumber The vendorFaxNumber to set.
     */
    public void setVendorFaxNumber(String vendorFaxNumber) {
        this.vendorFaxNumber = vendorFaxNumber;
    }

    /**
     * Gets the vendorDefaultAddressIndicator attribute.
     * 
     * @return Returns the vendorDefaultAddressIndicator.
     */
    public boolean isVendorDefaultAddressIndicator() {
        return vendorDefaultAddressIndicator;
    }

    /**
     * Sets the vendorDefaultAddressIndicator attribute value.
     * 
     * @param vendorDefaultAddressIndicator The vendorDefaultAddressIndicator to set.
     */
    public void setVendorDefaultAddressIndicator(boolean vendorDefaultAddressIndicator) {
        this.vendorDefaultAddressIndicator = vendorDefaultAddressIndicator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<VendorDefaultAddress> getVendorDefaultAddresses() {
        return vendorDefaultAddresses;
    }

    public void setVendorDefaultAddresses(List<VendorDefaultAddress> vendorDefaultAddresses) {
        this.vendorDefaultAddresses = vendorDefaultAddresses;
    }

    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting(Object toCompare) {
        LOG.debug("Entering isEqualForRouting.");
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorAddress)) {
            LOG.debug("Exiting isEqualForRouting");
            return false;
        }
        else {
            VendorAddress va = (VendorAddress) toCompare;
            boolean eq = new EqualsBuilder().append(this.getVendorAddressGeneratedIdentifier(), va.getVendorAddressGeneratedIdentifier()).append(this.getVendorHeaderGeneratedIdentifier(), va.getVendorHeaderGeneratedIdentifier()).append(this.getVendorDetailAssignedIdentifier(), va.getVendorDetailAssignedIdentifier()).append(this.getVendorAddressTypeCode(), va.getVendorAddressTypeCode()).append(this.getVendorLine1Address(), va.getVendorLine1Address()).append(this.getVendorLine2Address(), va.getVendorLine2Address()).append(this.getVendorCityName(), va.getVendorCityName()).append(this.getVendorStateCode(), va.getVendorStateCode()).append(this.getVendorZipCode(), va.getVendorZipCode()).append(this.getVendorCountryCode(), va.getVendorCountryCode()).append(this.getVendorAttentionName(), va.getVendorAttentionName()).append(this.getVendorAddressInternationalProvinceName(), va.getVendorAddressInternationalProvinceName()).append(this.getVendorAddressEmailAddress(),
                    va.getVendorAddressEmailAddress()).append(this.getVendorBusinessToBusinessUrlAddress(), va.getVendorBusinessToBusinessUrlAddress()).append(this.getVendorFaxNumber(), va.getVendorFaxNumber()).append(this.isVendorDefaultAddressIndicator(), va.isVendorDefaultAddressIndicator()).isEquals();
            eq &= SpringContext.getBean(VendorService.class).equalMemberLists(this.getVendorDefaultAddresses(), va.getVendorDefaultAddresses());
            LOG.debug("Exiting isEqualForRouting.");
            return eq;
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorAddressGeneratedIdentifier != null) {
            m.put("vendorAddressGeneratedIdentifier", this.vendorAddressGeneratedIdentifier.toString());
        }
        return m;
    }

}

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
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * 
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
    
//    private String[] vendorDefaultAddresses = {};
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

    /**
     * Gets the vendorAddressGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorAddressGeneratedIdentifier
     * 
     */
    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    /**
     * Sets the vendorAddressGeneratedIdentifier attribute.
     * 
     * @param vendorAddressGeneratedIdentifier The vendorAddressGeneratedIdentifier to set.
     * 
     */
    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }


    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorHeaderGeneratedIdentifier
     * 
     */
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     * 
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     * 
     */
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     * 
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }


    /**
     * Gets the vendorAddressInternationalProvinceName attribute.
     * 
     * @return Returns the vendorAddressInternationalProvinceName
     * 
     */
    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    /**
     * Sets the vendorAddressInternationalProvinceName attribute.
     * 
     * @param vendorAddressInternationalProvinceName The vendorAddressInternationalProvinceName to set.
     * 
     */
    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }


    /**
     * Gets the vendorAddressEmailAddress attribute.
     * 
     * @return Returns the vendorAddressEmailAddress
     * 
     */
    public String getVendorAddressEmailAddress() {
        return vendorAddressEmailAddress;
    }

    /**
     * Sets the vendorAddressEmailAddress attribute.
     * 
     * @param vendorAddressEmailAddress The vendorAddressEmailAddress to set.
     * 
     */
    public void setVendorAddressEmailAddress(String vendorAddressEmailAddress) {
        this.vendorAddressEmailAddress = vendorAddressEmailAddress;
    }


    /**
     * Gets the vendorAddressTypeCode attribute.
     * 
     * @return Returns the vendorAddressTypeCode
     * 
     */
    public String getVendorAddressTypeCode() {
        return vendorAddressTypeCode;
    }

    /**
     * Sets the vendorAddressTypeCode attribute.
     * 
     * @param vendorAddressTypeCode The vendorAddressTypeCode to set.
     * 
     */
    public void setVendorAddressTypeCode(String vendorAddressTypeCode) {
        this.vendorAddressTypeCode = vendorAddressTypeCode;
    }


    /**
     * Gets the vendorLine1Address attribute.
     * 
     * @return Returns the vendorLine1Address
     * 
     */
    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    /**
     * Sets the vendorLine1Address attribute.
     * 
     * @param vendorLine1Address The vendorLine1Address to set.
     * 
     */
    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }


    /**
     * Gets the vendorLine2Address attribute.
     * 
     * @return Returns the vendorLine2Address
     * 
     */
    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    /**
     * Sets the vendorLine2Address attribute.
     * 
     * @param vendorLine2Address The vendorLine2Address to set.
     * 
     */
    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }


    /**
     * Gets the vendorCityName attribute.
     * 
     * @return Returns the vendorCityName
     * 
     */
    public String getVendorCityName() {
        return vendorCityName;
    }

    /**
     * Sets the vendorCityName attribute.
     * 
     * @param vendorCityName The vendorCityName to set.
     * 
     */
    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }


    /**
     * Gets the vendorStateCode attribute.
     * 
     * @return Returns the vendorStateCode
     * 
     */
    public String getVendorStateCode() {
        return vendorStateCode;
    }

    /**
     * Sets the vendorStateCode attribute.
     * 
     * @param vendorStateCode The vendorStateCode to set.
     * 
     */
    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }


    /**
     * Gets the vendorZipCode attribute.
     * 
     * @return Returns the vendorZipCode
     * 
     */
    public String getVendorZipCode() {
        return vendorZipCode;
    }

    /**
     * Sets the vendorZipCode attribute.
     * 
     * @param vendorZipCode The vendorZipCode to set.
     * 
     */
    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }


    /**
     * Gets the vendorCountryCode attribute.
     * 
     * @return Returns the vendorCountryCode
     * 
     */
    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    /**
     * Sets the vendorCountryCode attribute.
     * 
     * @param vendorCountryCode The vendorCountryCode to set.
     * 
     */
    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }


    /**
     * Gets the vendorAttentionName attribute.
     * 
     * @return Returns the vendorAttentionName
     * 
     */
    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    /**
     * Sets the vendorAttentionName attribute.
     * 
     * @param vendorAttentionName The vendorAttentionName to set.
     * 
     */
    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }


    /**
     * Gets the vendorBusinessToBusinessUrlAddress attribute.
     * 
     * @return Returns the vendorBusinessToBusinessUrlAddress
     * 
     */
    public String getVendorBusinessToBusinessUrlAddress() {
        return vendorBusinessToBusinessUrlAddress;
    }

    /**
     * Sets the vendorBusinessToBusinessUrlAddress attribute.
     * 
     * @param vendorBusinessToBusinessUrlAddress The vendorBusinessToBusinessUrlAddress to set.
     * 
     */
    public void setVendorBusinessToBusinessUrlAddress(String vendorBusinessToBusinessUrlAddress) {
        this.vendorBusinessToBusinessUrlAddress = vendorBusinessToBusinessUrlAddress;
    }


    /**
     * Gets the vendorDetail attribute.
     * 
     * @return Returns the vendorDetail
     * 
     */
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


    /**
     * Gets the vendorAddressType attribute.
     * 
     * @return Returns the vendorAddressType
     * 
     */
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

    /**
     * Gets the vendorState attribute.
     * 
     * @return Returns the vendorState
     * 
     */
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

    /**
     * Gets the vendorCountry attribute.
     * 
     * @return Returns the vendorCountry
     * 
     */
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

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }    
    
    /**
     * Gets the vendorDefaultAddresses attribute. 
     * @return Returns the vendorDefaultAddresses.
     */
    public List<VendorDefaultAddress> getVendorDefaultAddresses() {
        return vendorDefaultAddresses;
    }

    /**
     * Sets the vendorDefaultAddresses attribute value.
     * @param vendorDefaultAddresses The vendorDefaultAddresses to set.
     */
    public void setVendorDefaultAddresses(List<VendorDefaultAddress> vendorDefaultAddresses) {
        this.vendorDefaultAddresses = vendorDefaultAddresses;
    }
    
    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting( Object toCompare ) {
        LOG.debug( "Entering isEqualForRouting." );
        if( ( ObjectUtils.isNull( toCompare ) ) || !( toCompare instanceof VendorAddress ) ) {
            LOG.debug( "Exiting isEqualForRouting" );
            return false;
        } else {
            VendorAddress va = (VendorAddress)toCompare;
            boolean eq = new EqualsBuilder()
                .append( this.getVendorAddressGeneratedIdentifier(), va.getVendorAddressGeneratedIdentifier() )
                .append( this.getVendorHeaderGeneratedIdentifier(), va.getVendorHeaderGeneratedIdentifier() )
                .append( this.getVendorDetailAssignedIdentifier(), va.getVendorDetailAssignedIdentifier() )
                .append( this.getVendorAddressTypeCode(), va.getVendorAddressTypeCode() )
                .append( this.getVendorLine1Address(), va.getVendorLine1Address() )
                .append( this.getVendorLine2Address(), va.getVendorLine2Address() )
                .append( this.getVendorCityName(), va.getVendorCityName() )
                .append( this.getVendorStateCode(), va.getVendorStateCode() )
                .append( this.getVendorZipCode(), va.getVendorZipCode() )
                .append( this.getVendorCountryCode(), va.getVendorCountryCode() )
                .append( this.getVendorAttentionName(), va.getVendorAttentionName() )
                .append( this.getVendorAddressInternationalProvinceName(),
                        va.getVendorAddressInternationalProvinceName() )
                .append( this.getVendorAddressEmailAddress(), va.getVendorAddressEmailAddress() )
                .append( this.getVendorBusinessToBusinessUrlAddress(),
                        va.getVendorBusinessToBusinessUrlAddress() )
                .append( this.getVendorFaxNumber(), va.getVendorFaxNumber() )
                .append( this.isVendorDefaultAddressIndicator(), va.isVendorDefaultAddressIndicator() )
                .isEquals();
            eq &= SpringServiceLocator.getVendorService().equalMemberLists( this.getVendorDefaultAddresses(),
                    va.getVendorDefaultAddresses() );
            LOG.debug( "Exiting isEqualForRouting." );
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

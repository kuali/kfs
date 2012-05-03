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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * Address to be associated with a particular Vendor.
 */
public class VendorAddress extends PersistableBusinessObjectBase implements VendorRoutingComparable, MutableInactivatable {
    private static final Logger LOG = Logger.getLogger(VendorAddress.class);

    protected Integer vendorAddressGeneratedIdentifier;
    protected Integer vendorHeaderGeneratedIdentifier;
    protected Integer vendorDetailAssignedIdentifier;
    protected String vendorAddressTypeCode;
    protected String vendorLine1Address;
    protected String vendorLine2Address;
    protected String vendorCityName;
    protected String vendorStateCode;
    protected String vendorZipCode;
    protected String vendorCountryCode;
    protected String vendorAttentionName;
    protected String vendorAddressInternationalProvinceName;
    protected String vendorAddressEmailAddress;
    protected String vendorBusinessToBusinessUrlAddress;
    protected String vendorFaxNumber;
    protected boolean vendorDefaultAddressIndicator;
    protected boolean active;

    protected List<VendorDefaultAddress> vendorDefaultAddresses;

    protected VendorDetail vendorDetail;
    protected AddressType vendorAddressType;
    protected StateEbo vendorState;
    protected CountryEbo vendorCountry;

    /**
     * Default constructor.
     */
    public VendorAddress() {
        vendorDefaultAddresses = new ArrayList<VendorDefaultAddress>();
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

    public StateEbo getVendorState() {
        if ( StringUtils.isBlank(vendorStateCode) || StringUtils.isBlank(vendorCountryCode ) ) {
            vendorState = null;
        } else {
            if ( vendorState == null || !StringUtils.equals( vendorState.getCode(),vendorStateCode) || !StringUtils.equals(vendorState.getCountryCode(), vendorCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, vendorCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, vendorStateCode);
                    vendorState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return vendorState;
    }

    /**
     * Sets the vendorState attribute.
     *
     * @param vendorState The vendorState to set.
     * @deprecated
     */
    public void setVendorState(StateEbo vendorState) {
        this.vendorState = vendorState;
    }

    public CountryEbo getVendorCountry() {
        if ( StringUtils.isBlank(vendorCountryCode) ) {
            vendorCountry = null;
        } else {
            if ( vendorCountry == null || !StringUtils.equals( vendorCountry.getCode(),vendorCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, vendorCountryCode);
                    vendorCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return vendorCountry;
    }

    /**
     * Sets the vendorCountry attribute.
     *
     * @param vendorCountry The vendorCountry to set.
     * @deprecated
     */
    public void setVendorCountry(CountryEbo vendorCountry) {
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

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
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
     * @see org.kuali.kfs.vnd.document.routing.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    @Override
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

}

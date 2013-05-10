/*
 * Copyright 2011 The Kuali Foundation.
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

import java.util.Date;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class DebarredVendorMatch extends PersistableBusinessObjectBase {
    public static final String VENDOR_TYPE = "vendorHeader.vendorTypeCode";
    public static final String CONFIRM_STATUS = "confirmStatusCode";
    public static final String EXCLUSION_STATUS = "vendorExclusionStatus";
    public static final String DEBARRED_VENDOR_ID = "debarredVendorId";

    private int debarredVendorId;
    private Date loadDate;
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String province;
    private String zip;
    private String aliases;
    private String description;
    private String confirmStatusCode;
    private Date lastUpdatedTimeStamp;
    private String lastUpdatedPrincipalName;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private long addressGeneratedId;

    private VendorDetail vendorDetail;
    private VendorAddress vendorAddress;
    private VendorHeader vendorHeader;

    private String vendorExclusionStatus; // not persisted in the db
    private String concatenatedId; // not persisted in the db
    private String concatenatedAliases; // not persisted in the db

    /**
     * Gets the debarredVendorId attribute.
     * @return Returns the debarredVendorId.
     */
    public int getDebarredVendorId() {
        return debarredVendorId;
    }

    /**
     * Sets the debarredVendorId attribute value.
     * @param debarredVendorId The debarredVendorId to set.
     */
    public void setDebarredVendorId(int debarredVendorId) {
        this.debarredVendorId = debarredVendorId;
    }

    /**
     * Gets the loadDate attribute.
     * @return Returns the loadDate.
     */
    public Date getLoadDate() {
        return loadDate;
    }

    /**
     * Sets the loadDate attribute value.
     * @param loadDate The loadDate to set.
     */
    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }

    /**
     * Gets the name attribute.
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute value.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address1 attribute.
     * @return Returns the address1.
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Sets the address1 attribute value.
     * @param address1 The address1 to set.
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Gets the address2 attribute.
     * @return Returns the address2.
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the address2 attribute value.
     * @param address2 The address2 to set.
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Gets the city attribute.
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city attribute value.
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state attribute.
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state attribute value.
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the province attribute.
     * @return Returns the province.
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the province attribute value.
     * @param country The province to set.
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Gets the zip attribute.
     * @return Returns the zip.
     */
    public String getZip() {
        return zip;
    }

    /**
     * Sets the zip attribute value.
     * @param zip The zip to set.
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * Gets the aliases attribute.
     * @return Returns the aliases.
     */
    public String getAliases() {
        return aliases;
    }

    /**
     * Sets the aliases attribute value.
     * @param aliases The aliases to set.
     */
    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    /**
     * Gets the description attribute.
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the confirmStatusCode attribute.
     * @return Returns the confirmStatusCode.
     */
    public String getConfirmStatusCode() {
        return confirmStatusCode;
    }

    /**
     * Sets the confirmStatusCode attribute value.
     * @param confirmStatusCode The confirmStatusCode to set.
     */
    public void setConfirmStatusCode(String confirmStatusCode) {
        this.confirmStatusCode = confirmStatusCode;
    }

    /**
     * Gets the lastUpdatedTimeStamp attribute.
     * @return Returns the lastUpdatedTimeStamp.
     */
    public Date getLastUpdatedTimeStamp() {
        return lastUpdatedTimeStamp;
    }

    /**
     * Sets the lastUpdatedTimeStamp attribute value.
     * @param lastUpdatedTimeStamp The lastUpdatedTimeStamp to set.
     */
    public void setLastUpdatedTimeStamp(Date lastUpdatedTimeStamp) {
        this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
    }

    /**
     * Gets the lastUpdatedPrincipalName attribute.
     * @return Returns the lastUpdatedPrincipalName.
     */
    public String getLastUpdatedPrincipalName() {
        return lastUpdatedPrincipalName;
    }

    /**
     * Sets the lastUpdatedPrincipalName attribute value.
     * @param lastUpdatedPrincipalName The lastUpdatedPrincipalName to set.
     */
    public void setLastUpdatedPrincipalName(String lastUpdatedPrincipalName) {
        this.lastUpdatedPrincipalName = lastUpdatedPrincipalName;
    }

    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * @return Returns the vendorHeaderGeneratedIdentifier.
     */
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute value.
     * @param vendorHeaderGeneratedIdentifier The headerGeneratedId to set.
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * @return Returns the vendorDetailAssignedIdentifier.
     */
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute value.
     * @param vendorDetailAssignedIdentifier The detailAssignedId to set.
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    /**
     * Gets the addressGeneratedId attribute.
     * @return Returns the addressGeneratedId.
     */
    public long getAddressGeneratedId() {
        return addressGeneratedId;
    }

    /**
     * Sets the addressGeneratedId attribute value.
     * @param addressGeneratedId The addressGeneratedId to set.
     */
    public void setAddressGeneratedId(long addressGeneratedId) {
        this.addressGeneratedId = addressGeneratedId;
    }

    /**
     * Gets the vendorDetail attribute.
     * @return Returns the vendorDetail.
     */
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute value.
     * @param vendorDetail The vendorDetail to set.
     */
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * Gets the vendorAddress attribute.
     * @return Returns the vendorAddress.
     */
    public VendorAddress getVendorAddress() {
        return vendorAddress;
    }

    /**
     * Sets the vendorAddress attribute value.
     * @param vendorAddress The vendorAddress to set.
     */
    public void setVendorAddress(VendorAddress vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    /**
     * Gets the vendorHeader attribute.
     * @return Returns the vendorHeader.
     */
    public VendorHeader getVendorHeader() {
        return vendorHeader;
    }

    /**
     * Sets the vendorHeader attribute value.
     * @param vendorHeader The vendorHeader to set.
     */
    public void setVendorHeader(VendorHeader vendorHeader) {
        this.vendorHeader = vendorHeader;
    }

    /**
     * Gets the vendorExclusionStatus attribute.
     * @return Returns the vendorExclusionStatus.
     */
    public String getVendorExclusionStatus() {
        return vendorExclusionStatus;
    }

    /**
     * Sets the vendorExclusionStatus attribute value.
     * @param vendorExclusionStatus The vendorExclusionStatus to set.
     */
    public void setVendorExclusionStatus(String vendorExclusionStatus) {
        this.vendorExclusionStatus = vendorExclusionStatus;
    }

    /**
     * Gets the concatenatedId attribute.
     * @return Returns the concatenatedId.
     */
    public String getConcatenatedId() {
        return concatenatedId;
    }

    /**
     * Sets the concatenatedId attribute value.
     * @param concatenatedId The concatenatedId to set.
     */
    public void setConcatenatedId(String concatenatedId) {
        this.concatenatedId = concatenatedId;
    }

    /**
     * Gets the concatenatedAliases attribute.
     * @return Returns the concatenatedAliases.
     */
    public String getConcatenatedAliases() {
        return concatenatedAliases;
    }

    /**
     * Sets the concatenatedAliases attribute value.
     * @param concatenatedAliases The concatenatedAliases to set.
     */
    public void setConcatenatedAliases(String concatenatedAliases) {
        this.concatenatedAliases = concatenatedAliases;
    }
}

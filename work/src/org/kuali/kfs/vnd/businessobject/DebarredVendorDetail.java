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

public class DebarredVendorDetail extends PersistableBusinessObjectBase {
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
     * Gets the stateOrCountry attribute.
     * @return Returns the stateOrCountry.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the stateOrCountry attribute value.
     * @param stateOrCountry The stateOrCountry to set.
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
     * Gets the alias attribute.
     * @return Returns the alias.
     */
    public String getAliases() {
        return aliases;
    }

    /**
     * Sets the alias attribute value.
     * @param alias The alias to set.
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
}

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

import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Billing Address Business Object.
 */
public class BillingAddress extends PersistableBusinessObjectBase implements MutableInactivatable{

    protected String billingCampusCode;
    protected String billingName;
    protected String billingLine1Address;
    protected String billingLine2Address;
    protected String billingCityName;
    protected String billingStateCode;
    protected String billingPostalCode;
    protected String billingCountryCode;
    protected String billingPhoneNumber;
    protected String billingEmailAddress;
    protected boolean active;

    protected CampusParameter billingCampus;

    /**
     * Default constructor.
     */
    public BillingAddress() {

    }

    public String getBillingCampusCode() {
        return billingCampusCode;
    }

    public void setBillingCampusCode(String billingCampusCode) {
        this.billingCampusCode = billingCampusCode;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public String getBillingLine1Address() {
        return billingLine1Address;
    }

    public void setBillingLine1Address(String billingLine1Address) {
        this.billingLine1Address = billingLine1Address;
    }

    public String getBillingLine2Address() {
        return billingLine2Address;
    }

    public void setBillingLine2Address(String billingLine2Address) {
        this.billingLine2Address = billingLine2Address;
    }

    public String getBillingCityName() {
        return billingCityName;
    }

    public void setBillingCityName(String billingCityName) {
        this.billingCityName = billingCityName;
    }

    public String getBillingStateCode() {
        return billingStateCode;
    }

    public void setBillingStateCode(String billingStateCode) {
        this.billingStateCode = billingStateCode;
    }

    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    public void setBillingPostalCode(String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    public String getBillingCountryCode() {
        return billingCountryCode;
    }

    public void setBillingCountryCode(String billingCountryCode) {
        this.billingCountryCode = billingCountryCode;
    }

    public String getBillingPhoneNumber() {
        return billingPhoneNumber;
    }

    public void setBillingPhoneNumber(String billingPhoneNumber) {
        this.billingPhoneNumber = billingPhoneNumber;
    }
    
    public String getBillingEmailAddress() {
        return billingEmailAddress;
    }

    public void setBillingEmailAddress(String billingEmailAddress) {
        this.billingEmailAddress = billingEmailAddress;
    }

    public CampusParameter getBillingCampus() {
        return billingCampus;
    }

    /**
     * @deprecated
     */
    public void setBillingCampus(CampusParameter billingCampus) {
        this.billingCampus = billingCampus;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

}

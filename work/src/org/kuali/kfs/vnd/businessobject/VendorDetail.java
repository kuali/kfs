/*
 * Copyright 2007 The Kuali Foundation.
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

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * Contains all data for a specific parent or division Vendor, including a link to the <code>VendorHeader</code>, which only
 * contains information about the parent company, but can be shared between division Vendors.
 * 
 * @see org.kuali.module.vendor.bo.VendorHeader
 */
public class VendorDetail extends PersistableBusinessObjectBase implements VendorRoutingComparable {
    private static Logger LOG = Logger.getLogger(VendorDetail.class);

    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorNumber; // not persisted in the db
    private boolean vendorParentIndicator;
    private String vendorName;
    private String vendorFirstName; // not persisted in the db
    private String vendorLastName; // not persisted in the db
    private String vendorStateForLookup; // not persisted in the db

    private boolean activeIndicator;
    private String vendorInactiveReasonCode;
    private String vendorDunsNumber;
    private String vendorPaymentTermsCode;
    private String vendorShippingTitleCode;
    private String vendorShippingPaymentTermsCode;
    private Boolean vendorConfirmationIndicator;
    private Boolean vendorPrepaymentIndicator;
    private Boolean vendorCreditCardIndicator;
    private KualiDecimal vendorMinimumOrderAmount;
    private String vendorUrlAddress;
    private String vendorRemitName;
    private Boolean vendorRestrictedIndicator;
    private String vendorRestrictedReasonText;
    private Date vendorRestrictedDate;
    private String vendorRestrictedPersonIdentifier;
    private String vendorSoldToNumber; // not persisted in the db
    private Integer vendorSoldToGeneratedIdentifier;
    private Integer vendorSoldToAssignedIdentifier;
    private String vendorSoldToName;
    private boolean vendorFirstLastNameIndicator;

    private List<VendorAddress> vendorAddresses;
    private List<VendorAlias> vendorAliases;
    private List<VendorContact> vendorContacts;
    private List<VendorContract> vendorContracts;
    private List<VendorCustomerNumber> vendorCustomerNumbers;
    private List<VendorPhoneNumber> vendorPhoneNumbers;
    private List<VendorShippingSpecialCondition> vendorShippingSpecialConditions;
    private List<VendorCommodityCode> vendorCommodities;
    
    private VendorHeader vendorHeader;
    private VendorInactiveReason vendorInactiveReason;
    private PaymentTermType vendorPaymentTerms;
    private ShippingTitle vendorShippingTitle;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private VendorDetail soldToVendorDetail;
    private UniversalUser vendorRestrictedPerson;

    private String defaultAddressLine1; // not persisted in the db
    private String defaultAddressLine2; // not persisted in the db
    private String defaultAddressCity; // not persisted in the db
    private String defaultAddressStateCode; // not persisted in the db
    private String defaultAddressInternationalProvince; // not persisted in the db
    private String defaultAddressPostalCode; // not persisted in the db
    private String defaultAddressCountryCode; // not persisted in the db

    /**
     * Default constructor.
     */
    public VendorDetail() {
        vendorHeader = new VendorHeader();
        vendorAddresses = new TypedArrayList(VendorAddress.class);
        vendorAliases = new TypedArrayList(VendorAlias.class);
        vendorContacts = new TypedArrayList(VendorContact.class);
        vendorContracts = new TypedArrayList(VendorContract.class);
        vendorCustomerNumbers = new TypedArrayList(VendorCustomerNumber.class);
        vendorPhoneNumbers = new TypedArrayList(VendorPhoneNumber.class);
        vendorShippingSpecialConditions = new TypedArrayList(VendorShippingSpecialCondition.class);
        vendorCommodities = new TypedArrayList(VendorCommodityCode.class);
        vendorParentIndicator = true;

    }

    public boolean isVendorDebarred() {

        return (ObjectUtils.isNotNull(getVendorHeader().getVendorDebarredIndicator()) && getVendorHeader().getVendorDebarredIndicator());
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

    /**
     * A concatenation of the vendorHeaderGeneratedIdentifier, a dash, and the vendorDetailAssignedIdentifier
     * 
     * @return Returns the vendorNumber.
     */
    public String getVendorNumber() {
        String headerId = "";
        String detailId = "";
        String vendorNumber = "";
        if (ObjectUtils.isNotNull(this.vendorHeaderGeneratedIdentifier)) {
            headerId = this.vendorHeaderGeneratedIdentifier.toString();
        }
        if (ObjectUtils.isNotNull(this.vendorDetailAssignedIdentifier)) {
            detailId = this.vendorDetailAssignedIdentifier.toString();
        }
        if (!StringUtils.isEmpty(headerId) && !StringUtils.isEmpty(detailId)) {
            vendorNumber = headerId + "-" + detailId;
        }

        return vendorNumber;
    }

    /**
     * Sets the vendorNumber attribute value.
     * 
     * @param vendorNumber The vendorNumber to set.
     */
    public void setVendorNumber(String vendorNumber) {
        if (!StringUtils.isEmpty(vendorNumber)) {
            int dashInd = vendorNumber.indexOf("-");
            if (vendorNumber.length() >= dashInd) {
                String vndrHdrGenId = vendorNumber.substring(0, dashInd);
                String vndrDetailAssgnedId = vendorNumber.substring(dashInd + 1);
                if (!StringUtils.isEmpty(vndrHdrGenId) && !StringUtils.isEmpty(vndrDetailAssgnedId)) {
                    this.vendorHeaderGeneratedIdentifier = new Integer(vndrHdrGenId);
                    this.vendorDetailAssignedIdentifier = new Integer(vndrDetailAssgnedId);
                }
            }
        }
        else {
            this.vendorNumber = vendorNumber;
        }
    }

    /**
     * Gets the vendorSoldToNumber attribute.
     * 
     * @return Returns the vendorSoldToNumber.
     */
    public String getVendorSoldToNumber() {
        String headerId = "";
        String detailId = "";
        String vendorSoldToNumber = "";

        if (this.vendorSoldToGeneratedIdentifier != null) {
            headerId = this.vendorSoldToGeneratedIdentifier.toString();
        }
        if (this.vendorSoldToAssignedIdentifier != null) {
            detailId = this.vendorSoldToAssignedIdentifier.toString();
        }
        if (!StringUtils.isEmpty(headerId) && !StringUtils.isEmpty(detailId)) {
            vendorSoldToNumber = headerId + "-" + detailId;
        }

        return vendorSoldToNumber;
    }

    /**
     * Sets the vendorSoldToNumber attribute value.
     * 
     * @param vendorSoldToNumber The vendorSoldToNumber to set.
     */
    public void setVendorSoldToNumber(String vendorSoldToNumber) {
        if (!StringUtils.isEmpty(vendorSoldToNumber)) {
            int dashInd = vendorSoldToNumber.indexOf("-");
            if (vendorSoldToNumber.length() >= dashInd) {
                String headerId = vendorSoldToNumber.substring(0, dashInd);
                String detailId = vendorSoldToNumber.substring(dashInd + 1);
                if (!StringUtils.isEmpty(headerId) && !StringUtils.isEmpty(detailId)) {
                    this.vendorSoldToGeneratedIdentifier = new Integer(headerId);
                    this.vendorSoldToAssignedIdentifier = new Integer(detailId);
                }
            }
        }
        else {
            this.vendorSoldToNumber = vendorSoldToNumber;
        }
    }

    public boolean isVendorParentIndicator() {

        return vendorParentIndicator;
    }

    public void setVendorParentIndicator(boolean vendorParentIndicator) {
        this.vendorParentIndicator = vendorParentIndicator;
    }

    public Integer getVendorSoldToGeneratedIdentifier() {

        return vendorSoldToGeneratedIdentifier;
    }

    public void setVendorSoldToGeneratedIdentifier(Integer vendorSoldToGeneratedIdentifier) {
        this.vendorSoldToGeneratedIdentifier = vendorSoldToGeneratedIdentifier;
    }

    public Integer getVendorSoldToAssignedIdentifier() {

        return vendorSoldToAssignedIdentifier;
    }

    public void setVendorSoldToAssignedIdentifier(Integer vendorSoldToAssignedIdentifier) {
        this.vendorSoldToAssignedIdentifier = vendorSoldToAssignedIdentifier;
    }

    public String getVendorName() {

        return vendorName;
    }

    public void setAltVendorName(String altVendorName) {
        this.vendorName = altVendorName;
    }

    public String getAltVendorName() {

        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public boolean isActiveIndicator() {

        return activeIndicator;
    }

    public void setActiveIndicator(boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    public String getVendorInactiveReasonCode() {

        return vendorInactiveReasonCode;
    }

    public void setVendorInactiveReasonCode(String vendorInactiveReasonCode) {
        this.vendorInactiveReasonCode = vendorInactiveReasonCode;
    }

    public String getVendorPaymentTermsCode() {

        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public String getVendorShippingTitleCode() {

        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public String getVendorShippingPaymentTermsCode() {

        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public Boolean getVendorConfirmationIndicator() {

        return vendorConfirmationIndicator;
    }

    public void setVendorConfirmationIndicator(Boolean vendorConfirmationIndicator) {
        this.vendorConfirmationIndicator = vendorConfirmationIndicator;
    }

    public Boolean getVendorPrepaymentIndicator() {

        return vendorPrepaymentIndicator;
    }

    public void setVendorPrepaymentIndicator(Boolean vendorPrepaymentIndicator) {
        this.vendorPrepaymentIndicator = vendorPrepaymentIndicator;
    }

    public Boolean getVendorCreditCardIndicator() {

        return vendorCreditCardIndicator;
    }

    public void setVendorCreditCardIndicator(Boolean vendorCreditCardIndicator) {
        this.vendorCreditCardIndicator = vendorCreditCardIndicator;
    }

    public KualiDecimal getVendorMinimumOrderAmount() {

        return vendorMinimumOrderAmount;
    }

    public void setVendorMinimumOrderAmount(KualiDecimal vendorMinimumOrderAmount) {
        this.vendorMinimumOrderAmount = vendorMinimumOrderAmount;
    }

    public String getVendorUrlAddress() {

        return vendorUrlAddress;
    }

    public void setVendorUrlAddress(String vendorUrlAddress) {
        this.vendorUrlAddress = vendorUrlAddress;
    }


    /**
     * Gets the vendorSoldToName attribute.
     * 
     * @return Returns the vendorSoldToName
     */
    public String getVendorSoldToName() {
        if (soldToVendorDetail != null) {

            return soldToVendorDetail.getVendorName();
        }
        else {

            return this.vendorSoldToName;
        }
    }

    public void setVendorSoldToName(String vendorSoldToName) {
        this.vendorSoldToName = vendorSoldToName;
    }

    public String getVendorRemitName() {

        return vendorRemitName;
    }

    public void setVendorRemitName(String vendorRemitName) {
        this.vendorRemitName = vendorRemitName;
    }

    public Boolean getVendorRestrictedIndicator() {

        return vendorRestrictedIndicator;
    }

    public void setVendorRestrictedIndicator(Boolean vendorRestrictedIndicator) {
        this.vendorRestrictedIndicator = vendorRestrictedIndicator;
    }

    public String getVendorRestrictedReasonText() {

        return vendorRestrictedReasonText;
    }

    public void setVendorRestrictedReasonText(String vendorRestrictedReasonText) {
        this.vendorRestrictedReasonText = vendorRestrictedReasonText;
    }

    public Date getVendorRestrictedDate() {

        return vendorRestrictedDate;
    }

    public void setVendorRestrictedDate(Date vendorRestrictedDate) {
        this.vendorRestrictedDate = vendorRestrictedDate;
    }

    public String getVendorRestrictedPersonIdentifier() {

        return vendorRestrictedPersonIdentifier;
    }

    public void setVendorRestrictedPersonIdentifier(String vendorRestrictedPersonIdentifier) {
        this.vendorRestrictedPersonIdentifier = vendorRestrictedPersonIdentifier;
    }

    public String getVendorDunsNumber() {

        return vendorDunsNumber;
    }

    public void setVendorDunsNumber(String vendorDunsNumber) {
        this.vendorDunsNumber = vendorDunsNumber;
    }

    public VendorHeader getVendorHeader() {

        return vendorHeader;
    }

    public void setVendorHeader(VendorHeader vendorHeader) {
        this.vendorHeader = vendorHeader;
    }

    public PaymentTermType getVendorPaymentTerms() {

        return vendorPaymentTerms;
    }

    /**
     * Sets the vendorPaymentTerms attribute.
     * 
     * @param vendorPaymentTerms The vendorPaymentTerms to set.
     * @deprecated
     */
    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }

    public ShippingTitle getVendorShippingTitle() {

        return vendorShippingTitle;
    }

    /**
     * Sets the vendorShippingTitle attribute.
     * 
     * @param vendorShippingTitle The vendorShippingTitle to set.
     * @deprecated
     */
    public void setVendorShippingTitle(ShippingTitle vendorShippingTitle) {
        this.vendorShippingTitle = vendorShippingTitle;
    }

    public ShippingPaymentTerms getVendorShippingPaymentTerms() {

        return vendorShippingPaymentTerms;
    }

    /**
     * Sets the vendorShippingPaymentTerms attribute.
     * 
     * @param vendorShippingPaymentTerms The vendorShippingPaymentTerms to set.
     * @deprecated
     */
    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public VendorInactiveReason getVendorInactiveReason() {

        return vendorInactiveReason;
    }

    /**
     * Sets the vendorInactiveReason attribute value.
     * 
     * @param vendorInactiveReason The vendorInactiveReason to set.
     * @deprecated
     */
    public void setVendorInactiveReason(VendorInactiveReason vendorInactiveReason) {
        this.vendorInactiveReason = vendorInactiveReason;
    }

    public List<VendorAddress> getVendorAddresses() {

        return vendorAddresses;
    }

    public void setVendorAddresses(List<VendorAddress> vendorAddresses) {
        this.vendorAddresses = vendorAddresses;
    }

    public List<VendorContact> getVendorContacts() {

        return vendorContacts;
    }

    public void setVendorContacts(List<VendorContact> vendorContacts) {
        this.vendorContacts = vendorContacts;
    }

    public List<VendorContract> getVendorContracts() {

        return vendorContracts;
    }

    public void setVendorContracts(List<VendorContract> vendorContracts) {
        this.vendorContracts = vendorContracts;
    }

    public List<VendorCustomerNumber> getVendorCustomerNumbers() {

        return vendorCustomerNumbers;
    }

    public void setVendorCustomerNumbers(List<VendorCustomerNumber> vendorCustomerNumbers) {
        this.vendorCustomerNumbers = vendorCustomerNumbers;
    }

    public List<VendorShippingSpecialCondition> getVendorShippingSpecialConditions() {

        return vendorShippingSpecialConditions;
    }

    public void setVendorShippingSpecialConditions(List<VendorShippingSpecialCondition> vendorShippingSpecialConditions) {
        this.vendorShippingSpecialConditions = vendorShippingSpecialConditions;
    }

    public List<VendorCommodityCode> getVendorCommodities() {
        return vendorCommodities;
    }

    public void setVendorCommodities(List<VendorCommodityCode> vendorCommodities) {
        this.vendorCommodities = vendorCommodities;
    }

    public List<VendorAlias> getVendorAliases() {

        return vendorAliases;
    }

    public void setVendorAliases(List<VendorAlias> vendorAliases) {
        this.vendorAliases = vendorAliases;
    }

    public List<VendorPhoneNumber> getVendorPhoneNumbers() {

        return vendorPhoneNumbers;
    }

    public void setVendorPhoneNumbers(List<VendorPhoneNumber> vendorPhoneNumbers) {
        this.vendorPhoneNumbers = vendorPhoneNumbers;
    }

    public String getVendorFirstName() {

        return vendorFirstName;
    }

    public void setVendorFirstName(String vendorFirstName) {
        this.vendorFirstName = vendorFirstName;
    }

    public String getVendorLastName() {

        return vendorLastName;
    }

    public void setVendorLastName(String vendorLastName) {
        this.vendorLastName = vendorLastName;
    }

    public VendorDetail getSoldToVendorDetail() {

        return soldToVendorDetail;
    }

    public void setSoldToVendorDetail(VendorDetail soldToVendorDetail) {
        this.soldToVendorDetail = soldToVendorDetail;
    }

    public boolean isVendorFirstLastNameIndicator() {

        return vendorFirstLastNameIndicator;
    }

    public void setVendorFirstLastNameIndicator(boolean vendorFirstLastNameIndicator) {
        this.vendorFirstLastNameIndicator = vendorFirstLastNameIndicator;
    }

    public String getVendorStateForLookup() {

        return vendorStateForLookup;
    }

    public void setVendorStateForLookup(String vendorStateForLookup) {
        this.vendorStateForLookup = vendorStateForLookup;
    }

    public UniversalUser getVendorRestrictedPerson() {
        vendorRestrictedPerson = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(vendorRestrictedPersonIdentifier, vendorRestrictedPerson);

        return vendorRestrictedPerson;
    }

    /**
     * Sets the vendorRestrictedPerson attribute.
     * 
     * @param vendorRestrictedPerson The vendorRestrictedPerson to set.
     * @deprecated
     */
    public void setVendorRestrictedPerson(UniversalUser vendorRestrictedPerson) {
        this.vendorRestrictedPerson = vendorRestrictedPerson;
    }

    public String getDefaultAddressLine1() {

        return defaultAddressLine1;
    }

    public void setDefaultAddressLine1(String defaultAddressLine1) {
        this.defaultAddressLine1 = defaultAddressLine1;
    }

    public String getDefaultAddressCity() {

        return defaultAddressCity;
    }

    public void setDefaultAddressCity(String defaultAddressCity) {
        this.defaultAddressCity = defaultAddressCity;
    }

    public String getDefaultAddressLine2() {

        return defaultAddressLine2;
    }

    public void setDefaultAddressLine2(String defaultAddressLine2) {
        this.defaultAddressLine2 = defaultAddressLine2;
    }

    public String getDefaultAddressPostalCode() {

        return defaultAddressPostalCode;
    }

    public void setDefaultAddressPostalCode(String defaultAddressPostalCode) {
        this.defaultAddressPostalCode = defaultAddressPostalCode;
    }

    public String getDefaultAddressStateCode() {

        return defaultAddressStateCode;
    }

    public void setDefaultAddressStateCode(String defaultAddressStateCode) {
        this.defaultAddressStateCode = defaultAddressStateCode;
    }

    public String getDefaultAddressInternationalProvince() {
        return defaultAddressInternationalProvince;
    }

    public void setDefaultAddressInternationalProvince(String defaultAddressInternationalProvince) {
        this.defaultAddressInternationalProvince = defaultAddressInternationalProvince;
    }

    public String getDefaultAddressCountryCode() {

        return defaultAddressCountryCode;
    }

    public void setDefaultAddressCountryCode(String defaultAddressCountryCode) {
        this.defaultAddressCountryCode = defaultAddressCountryCode;
    }

    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting(Object toCompare) {
        LOG.debug("Entering isEqualForRouting.");
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorDetail)) {

            return false;
        }
        else {
            VendorDetail detail = (VendorDetail) toCompare;

            return new EqualsBuilder().append(this.getVendorHeaderGeneratedIdentifier(), detail.getVendorHeaderGeneratedIdentifier()).append(this.getVendorDetailAssignedIdentifier(), detail.getVendorDetailAssignedIdentifier()).append(this.isVendorParentIndicator(), detail.isVendorParentIndicator()).append(this.getVendorName(), detail.getVendorName()).append(this.isActiveIndicator(), detail.isActiveIndicator()).append(this.getVendorInactiveReasonCode(), detail.getVendorInactiveReasonCode()).append(this.getVendorDunsNumber(), detail.getVendorDunsNumber()).append(this.getVendorPaymentTermsCode(), detail.getVendorPaymentTermsCode()).append(this.getVendorShippingTitleCode(), detail.getVendorShippingTitleCode()).append(this.getVendorShippingPaymentTermsCode(), detail.getVendorShippingPaymentTermsCode()).append(this.getVendorConfirmationIndicator(), detail.getVendorConfirmationIndicator()).append(this.getVendorPrepaymentIndicator(), detail.getVendorPrepaymentIndicator()).append(
                    this.getVendorCreditCardIndicator(), detail.getVendorCreditCardIndicator()).append(this.getVendorMinimumOrderAmount(), detail.getVendorMinimumOrderAmount()).append(this.getVendorUrlAddress(), detail.getVendorUrlAddress()).append(this.getVendorRemitName(), detail.getVendorRemitName()).append(this.getVendorRestrictedIndicator(), detail.getVendorRestrictedIndicator()).append(this.getVendorRestrictedReasonText(), detail.getVendorRestrictedReasonText()).append(this.getVendorRestrictedDate(), detail.getVendorRestrictedDate()).append(this.getVendorRestrictedPersonIdentifier(), detail.getVendorRestrictedPersonIdentifier()).append(this.getVendorSoldToGeneratedIdentifier(), detail.getVendorSoldToGeneratedIdentifier()).append(this.getVendorSoldToAssignedIdentifier(), detail.getVendorSoldToAssignedIdentifier()).append(this.getVendorSoldToName(), detail.getVendorSoldToName()).append(this.isVendorFirstLastNameIndicator(), detail.isVendorFirstLastNameIndicator()).isEquals();
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorHeaderGeneratedIdentifier != null) {
            m.put("vendorHeaderGeneratedIdentifier", this.vendorHeaderGeneratedIdentifier.toString());
        }
        if (this.vendorDetailAssignedIdentifier != null) {
            m.put("vendorDetailAssignedIdentifier", this.vendorDetailAssignedIdentifier.toString());
        }

        return m;
    }

}

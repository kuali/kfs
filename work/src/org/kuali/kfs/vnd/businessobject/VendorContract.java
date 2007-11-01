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

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.vendor.service.VendorService;
import org.kuali.module.vendor.util.VendorRoutingComparable;

/**
 * Purchasing Contracts with specific Vendors.
 */
public class VendorContract extends PersistableBusinessObjectBase implements VendorRoutingComparable, Inactivateable {
    private static Logger LOG = Logger.getLogger(VendorContract.class);

    private Integer vendorContractGeneratedIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorContractName;
    private String vendorContractDescription;
    private String vendorCampusCode;
    private Date vendorContractBeginningDate;
    private Date vendorContractEndDate;
    private Integer contractManagerCode;
    private String purchaseOrderCostSourceCode;
    private String vendorPaymentTermsCode;
    private String vendorShippingPaymentTermsCode;
    private String vendorShippingTitleCode;
    private Date vendorContractExtensionDate;
    private Boolean vendorB2bIndicator;
    private KualiDecimal organizationAutomaticPurchaseOrderLimit;
    private boolean active;

    private List<VendorContractOrganization> vendorContractOrganizations;

    private VendorDetail vendorDetail;
    private Campus vendorCampus;
    private ContractManager contractManager;
    private PurchaseOrderCostSource purchaseOrderCostSource;
    private PaymentTermType vendorPaymentTerms;
    private ShippingPaymentTerms vendorShippingPaymentTerms;
    private ShippingTitle vendorShippingTitle;

    /**
     * Default constructor.
     */
    public VendorContract() {
        vendorContractOrganizations = new TypedArrayList(VendorContractOrganization.class);
    }

    public Integer getVendorContractGeneratedIdentifier() {

        return vendorContractGeneratedIdentifier;
    }

    public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
        this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
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

    public String getVendorContractName() {

        return vendorContractName;
    }

    public void setVendorContractName(String vendorContractName) {
        this.vendorContractName = vendorContractName;
    }

    public String getVendorContractDescription() {

        return vendorContractDescription;
    }

    public void setVendorContractDescription(String vendorContractDescription) {
        this.vendorContractDescription = vendorContractDescription;
    }

    public String getVendorCampusCode() {

        return vendorCampusCode;
    }

    public void setVendorCampusCode(String vendorCampusCode) {
        this.vendorCampusCode = vendorCampusCode;
    }

    public Date getVendorContractBeginningDate() {

        return vendorContractBeginningDate;
    }

    public void setVendorContractBeginningDate(Date vendorContractBeginningDate) {
        this.vendorContractBeginningDate = vendorContractBeginningDate;
    }

    public Date getVendorContractEndDate() {

        return vendorContractEndDate;
    }

    public void setVendorContractEndDate(Date vendorContractEndDate) {
        this.vendorContractEndDate = vendorContractEndDate;
    }

    public Integer getContractManagerCode() {

        return contractManagerCode;
    }

    public void setContractManagerCode(Integer contractManagerCode) {
        this.contractManagerCode = contractManagerCode;
    }

    public String getPurchaseOrderCostSourceCode() {

        return purchaseOrderCostSourceCode;
    }

    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
        this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
    }

    public String getVendorPaymentTermsCode() {

        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public String getVendorShippingPaymentTermsCode() {

        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public String getVendorShippingTitleCode() {

        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public Date getVendorContractExtensionDate() {

        return vendorContractExtensionDate;
    }

    public void setVendorContractExtensionDate(Date vendorContractExtensionDate) {
        this.vendorContractExtensionDate = vendorContractExtensionDate;
    }

    public Boolean getVendorB2bIndicator() {

        return vendorB2bIndicator;
    }

    public void setVendorB2bIndicator(Boolean vendorB2bIndicator) {
        this.vendorB2bIndicator = vendorB2bIndicator;
    }

    public KualiDecimal getOrganizationAutomaticPurchaseOrderLimit() {

        return organizationAutomaticPurchaseOrderLimit;
    }

    public void setOrganizationAutomaticPurchaseOrderLimit(KualiDecimal organizationAutomaticPurchaseOrderLimit) {
        this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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


    public Campus getVendorCampus() {

        return vendorCampus;
    }

    /**
     * Sets the vendorCampus attribute.
     * 
     * @param vendorCampus The vendorCampus to set.
     * @deprecated
     */
    public void setVendorCampus(Campus vendorCampus) {
        this.vendorCampus = vendorCampus;
    }

    public ContractManager getContractManager() {

        return contractManager;
    }

    /**
     * Sets the contractManager attribute.
     * 
     * @param contractManager The contractManager to set.
     * @deprecated
     */
    public void setContractManager(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public PurchaseOrderCostSource getPurchaseOrderCostSource() {

        return purchaseOrderCostSource;
    }

    /**
     * Sets the purchaseOrderCostSource attribute.
     * 
     * @param purchaseOrderCostSource The purchaseOrderCostSource to set.
     * @deprecated
     */
    public void setPurchaseOrderCostSource(PurchaseOrderCostSource purchaseOrderCostSource) {
        this.purchaseOrderCostSource = purchaseOrderCostSource;
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

    public List<VendorContractOrganization> getVendorContractOrganizations() {

        return vendorContractOrganizations;
    }

    public void setVendorContractOrganizations(List<VendorContractOrganization> vendorContractOrganizations) {
        this.vendorContractOrganizations = vendorContractOrganizations;
    }

    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting(Object toCompare) {
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorContract)) {
            return false;
        }
        else {
            VendorContract vc = (VendorContract) toCompare;
            boolean eq = new EqualsBuilder().append(this.getVendorContractGeneratedIdentifier(), vc.getVendorContractGeneratedIdentifier()).append(this.getVendorHeaderGeneratedIdentifier(), vc.getVendorHeaderGeneratedIdentifier()).append(this.getVendorDetailAssignedIdentifier(), vc.getVendorDetailAssignedIdentifier()).append(this.getVendorContractName(), vc.getVendorContractName()).append(this.getVendorContractDescription(), vc.getVendorContractDescription()).append(this.getVendorCampusCode(), vc.getVendorCampusCode()).append(this.getVendorContractBeginningDate(), vc.getVendorContractBeginningDate()).append(this.getVendorContractEndDate(), vc.getVendorContractEndDate()).append(this.getContractManagerCode(), vc.getContractManagerCode()).append(this.getPurchaseOrderCostSourceCode(), vc.getPurchaseOrderCostSourceCode()).append(this.getVendorPaymentTermsCode(), vc.getVendorPaymentTermsCode()).append(this.getVendorShippingPaymentTermsCode(), vc.getVendorShippingPaymentTermsCode()).append(
                    this.getVendorShippingTitleCode(), vc.getVendorShippingTitleCode()).append(this.getVendorContractExtensionDate(), vc.getVendorContractExtensionDate()).append(this.getVendorB2bIndicator(), vc.getVendorB2bIndicator()).append(this.getOrganizationAutomaticPurchaseOrderLimit(), vc.getOrganizationAutomaticPurchaseOrderLimit()).isEquals();
            eq &= SpringContext.getBean(VendorService.class).equalMemberLists(this.getVendorContractOrganizations(), vc.getVendorContractOrganizations());

            return eq;
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorContractGeneratedIdentifier != null) {
            m.put("vendorContractGeneratedIdentifier", this.vendorContractGeneratedIdentifier.toString());
        }

        return m;
    }


}

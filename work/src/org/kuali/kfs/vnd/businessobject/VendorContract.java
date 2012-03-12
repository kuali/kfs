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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purchasing Contracts with specific Vendors.
 */
public class VendorContract extends PersistableBusinessObjectBase implements VendorRoutingComparable, MutableInactivatable {
    protected static final Logger LOG = Logger.getLogger(VendorContract.class);

    protected Integer vendorContractGeneratedIdentifier;
    protected Integer vendorHeaderGeneratedIdentifier;
    protected Integer vendorDetailAssignedIdentifier;
    protected String vendorNumber; // not persisted in db, only for lookup page
    protected String vendorContractName;
    protected String vendorContractDescription;
    protected String vendorCampusCode;
    protected Date vendorContractBeginningDate;
    protected Date vendorContractEndDate;
    protected Integer contractManagerCode;
    protected String purchaseOrderCostSourceCode;
    protected String vendorPaymentTermsCode;
    protected String vendorShippingPaymentTermsCode;
    protected String vendorShippingTitleCode;
    protected Date vendorContractExtensionDate;
    protected Boolean vendorB2bIndicator;
    protected KualiDecimal organizationAutomaticPurchaseOrderLimit;
    protected boolean active;

    protected List<VendorContractOrganization> vendorContractOrganizations;

    protected VendorDetail vendorDetail;
    protected CampusParameter vendorCampus;
    protected ContractManager contractManager;
    protected PurchaseOrderCostSource purchaseOrderCostSource;
    protected PaymentTermType vendorPaymentTerms;
    protected ShippingPaymentTerms vendorShippingPaymentTerms;
    protected ShippingTitle vendorShippingTitle;

    /**
     * Default constructor.
     */
    public VendorContract() {
        vendorContractOrganizations = new ArrayList<VendorContractOrganization>();
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

    @Override
    public boolean isActive() {

        return active;
    }

    @Override
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


    public CampusParameter getVendorCampus() {
        return vendorCampus;
    }

    /**
     * Sets the vendorCampus attribute.
     *
     * @param vendorCampus The vendorCampus to set.
     * @deprecated
     */
    public void setVendorCampus(CampusParameter vendorCampus) {
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
     * @see org.kuali.kfs.vnd.document.routing.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    @Override
    public boolean isEqualForRouting(Object toCompare) {
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorContract)) {
            return false;
        }
        else {
            VendorContract vc = (VendorContract) toCompare;
            boolean eq = new EqualsBuilder().append(this.getVendorContractGeneratedIdentifier(), vc.getVendorContractGeneratedIdentifier()).append(this.getVendorHeaderGeneratedIdentifier(), vc.getVendorHeaderGeneratedIdentifier()).append(this.getVendorDetailAssignedIdentifier(), vc.getVendorDetailAssignedIdentifier()).append(this.getVendorContractName(), vc.getVendorContractName()).append(this.getVendorContractDescription(), vc.getVendorContractDescription()).append(this.getVendorCampusCode(), vc.getVendorCampusCode()).append(this.getVendorContractBeginningDate(), vc.getVendorContractBeginningDate()).append(this.getVendorContractEndDate(), vc.getVendorContractEndDate()).append(this.getContractManagerCode(), vc.getContractManagerCode()).append(this.getPurchaseOrderCostSourceCode(), vc.getPurchaseOrderCostSourceCode()).append(this.getVendorPaymentTermsCode(), vc.getVendorPaymentTermsCode()).append(this.getVendorShippingPaymentTermsCode(), vc.getVendorShippingPaymentTermsCode())
                    .append(this.getVendorShippingTitleCode(), vc.getVendorShippingTitleCode()).append(this.getVendorContractExtensionDate(), vc.getVendorContractExtensionDate()).append(this.getVendorB2bIndicator(), vc.getVendorB2bIndicator()).append(this.getOrganizationAutomaticPurchaseOrderLimit(), vc.getOrganizationAutomaticPurchaseOrderLimit()).isEquals();
            eq &= SpringContext.getBean(VendorService.class).equalMemberLists(this.getVendorContractOrganizations(), vc.getVendorContractOrganizations());

            return eq;
        }
    }

}

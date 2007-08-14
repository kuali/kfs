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
 * 
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

	/**
	 * Gets the vendorContractGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorContractGeneratedIdentifier
	 * 
	 */
	public Integer getVendorContractGeneratedIdentifier() { 
		return vendorContractGeneratedIdentifier;
	}

	/**
	 * Sets the vendorContractGeneratedIdentifier attribute.
	 * 
	 * @param vendorContractGeneratedIdentifier The vendorContractGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorContractGeneratedIdentifier(Integer vendorContractGeneratedIdentifier) {
		this.vendorContractGeneratedIdentifier = vendorContractGeneratedIdentifier;
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
	 * Gets the vendorContractName attribute.
	 * 
	 * @return Returns the vendorContractName
	 * 
	 */
	public String getVendorContractName() { 
		return vendorContractName;
	}

	/**
	 * Sets the vendorContractName attribute.
	 * 
	 * @param vendorContractName The vendorContractName to set.
	 * 
	 */
	public void setVendorContractName(String vendorContractName) {
		this.vendorContractName = vendorContractName;
	}


	/**
	 * Gets the vendorContractDescription attribute.
	 * 
	 * @return Returns the vendorContractDescription
	 * 
	 */
	public String getVendorContractDescription() { 
		return vendorContractDescription;
	}

	/**
	 * Sets the vendorContractDescription attribute.
	 * 
	 * @param vendorContractDescription The vendorContractDescription to set.
	 * 
	 */
	public void setVendorContractDescription(String vendorContractDescription) {
		this.vendorContractDescription = vendorContractDescription;
	}


	/**
	 * Gets the vendorCampusCode attribute.
	 * 
	 * @return Returns the vendorCampusCode
	 * 
	 */
	public String getVendorCampusCode() { 
		return vendorCampusCode;
	}

	/**
	 * Sets the vendorCampusCode attribute.
	 * 
	 * @param vendorCampusCode The vendorCampusCode to set.
	 * 
	 */
	public void setVendorCampusCode(String vendorCampusCode) {
		this.vendorCampusCode = vendorCampusCode;
	}


	/**
	 * Gets the vendorContractBeginningDate attribute.
	 * 
	 * @return Returns the vendorContractBeginningDate
	 * 
	 */
	public Date getVendorContractBeginningDate() { 
		return vendorContractBeginningDate;
	}

	/**
	 * Sets the vendorContractBeginningDate attribute.
	 * 
	 * @param vendorContractBeginningDate The vendorContractBeginningDate to set.
	 * 
	 */
	public void setVendorContractBeginningDate(Date vendorContractBeginningDate) {
		this.vendorContractBeginningDate = vendorContractBeginningDate;
	}


	/**
	 * Gets the vendorContractEndDate attribute.
	 * 
	 * @return Returns the vendorContractEndDate
	 * 
	 */
	public Date getVendorContractEndDate() { 
		return vendorContractEndDate;
	}

	/**
	 * Sets the vendorContractEndDate attribute.
	 * 
	 * @param vendorContractEndDate The vendorContractEndDate to set.
	 * 
	 */
	public void setVendorContractEndDate(Date vendorContractEndDate) {
		this.vendorContractEndDate = vendorContractEndDate;
	}


	/**
	 * Gets the contractManagerCode attribute.
	 * 
	 * @return Returns the contractManagerCode
	 * 
	 */
	public Integer getContractManagerCode() { 
		return contractManagerCode;
	}

	/**
	 * Sets the contractManagerCode attribute.
	 * 
	 * @param contractManagerCode The contractManagerCode to set.
	 * 
	 */
	public void setContractManagerCode(Integer contractManagerCode) {
		this.contractManagerCode = contractManagerCode;
	}


	/**
	 * Gets the purchaseOrderCostSourceCode attribute.
	 * 
	 * @return Returns the purchaseOrderCostSourceCode
	 * 
	 */
	public String getPurchaseOrderCostSourceCode() { 
		return purchaseOrderCostSourceCode;
	}

	/**
	 * Sets the purchaseOrderCostSourceCode attribute.
	 * 
	 * @param purchaseOrderCostSourceCode The purchaseOrderCostSourceCode to set.
	 * 
	 */
	public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
		this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
	}


	/**
	 * Gets the vendorPaymentTermsCode attribute.
	 * 
	 * @return Returns the vendorPaymentTermsCode
	 * 
	 */
	public String getVendorPaymentTermsCode() { 
		return vendorPaymentTermsCode;
	}

	/**
	 * Sets the vendorPaymentTermsCode attribute.
	 * 
	 * @param vendorPaymentTermsCode The vendorPaymentTermsCode to set.
	 * 
	 */
	public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
		this.vendorPaymentTermsCode = vendorPaymentTermsCode;
	}


	/**
	 * Gets the vendorShippingPaymentTermsCode attribute.
	 * 
	 * @return Returns the vendorShippingPaymentTermsCode
	 * 
	 */
	public String getVendorShippingPaymentTermsCode() { 
		return vendorShippingPaymentTermsCode;
	}

	/**
	 * Sets the vendorShippingPaymentTermsCode attribute.
	 * 
	 * @param vendorShippingPaymentTermsCode The vendorShippingPaymentTermsCode to set.
	 * 
	 */
	public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
		this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
	}


	/**
	 * Gets the vendorShippingTitleCode attribute.
	 * 
	 * @return Returns the vendorShippingTitleCode
	 * 
	 */
	public String getVendorShippingTitleCode() { 
		return vendorShippingTitleCode;
	}

	/**
	 * Sets the vendorShippingTitleCode attribute.
	 * 
	 * @param vendorShippingTitleCode The vendorShippingTitleCode to set.
	 * 
	 */
	public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
		this.vendorShippingTitleCode = vendorShippingTitleCode;
	}

	/**
	 * Gets the vendorContractExtensionDate attribute.
	 * 
	 * @return Returns the vendorContractExtensionDate
	 * 
	 */
	public Date getVendorContractExtensionDate() { 
		return vendorContractExtensionDate;
	}

	/**
	 * Sets the vendorContractExtensionDate attribute.
	 * 
	 * @param vendorContractExtensionDate The vendorContractExtensionDate to set.
	 * 
	 */
	public void setVendorContractExtensionDate(Date vendorContractExtensionDate) {
		this.vendorContractExtensionDate = vendorContractExtensionDate;
	}


	/**
	 * Gets the vendorB2bIndicator attribute.
	 * 
	 * @return Returns the vendorB2bIndicator
	 * 
	 */
	public Boolean getVendorB2bIndicator() { 
		return vendorB2bIndicator;
	}
	

	/**
	 * Sets the vendorB2bIndicator attribute.
	 * 
	 * @param vendorB2bIndicator The vendorB2bIndicator to set.
	 * 
	 */
	public void setVendorB2bIndicator(Boolean vendorB2bIndicator) {
		this.vendorB2bIndicator = vendorB2bIndicator;
	}


	/**
	 * Gets the organizationAutomaticPurchaseOrderLimit attribute.
	 * 
	 * @return Returns the organizationAutomaticPurchaseOrderLimit
	 * 
	 */
	public KualiDecimal getOrganizationAutomaticPurchaseOrderLimit() { 
		return organizationAutomaticPurchaseOrderLimit;
	}

	/**
	 * Sets the organizationAutomaticPurchaseOrderLimit attribute.
	 * 
	 * @param organizationAutomaticPurchaseOrderLimit The organizationAutomaticPurchaseOrderLimit to set.
	 * 
	 */
	public void setOrganizationAutomaticPurchaseOrderLimit(KualiDecimal organizationAutomaticPurchaseOrderLimit) {
		this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
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
	 * Gets the vendorCampus attribute.
	 * 
	 * @return Returns the vendorCampus
	 * 
	 */
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

	/**
	 * Gets the contractManager attribute.
	 * 
	 * @return Returns the contractManager
	 * 
	 */
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

	/**
	 * Gets the purchaseOrderCostSource attribute.
	 * 
	 * @return Returns the purchaseOrderCostSource
	 * 
	 */
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

	/**
	 * Gets the vendorPaymentTerms attribute.
	 * 
	 * @return Returns the vendorPaymentTerms
	 * 
	 */
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

	/**
	 * Gets the vendorShippingPaymentTerms attribute.
	 * 
	 * @return Returns the vendorShippingPaymentTerms
	 * 
	 */
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

	/**
	 * Gets the vendorShippingTitle attribute.
	 * 
	 * @return Returns the vendorShippingTitle
	 * 
	 */
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

    /**
     * Gets the vendorContractOrganizations attribute. 
     * @return Returns the vendorContractOrganizations.
     */
    public List<VendorContractOrganization> getVendorContractOrganizations() {
        return vendorContractOrganizations;
    }

    /**
     * Sets the vendorContractOrganizations attribute value.
     * @param vendorContractOrganizations The vendorContractOrganizations to set.
     */
    public void setVendorContractOrganizations(List<VendorContractOrganization> vendorContractOrganizations) {
        this.vendorContractOrganizations = vendorContractOrganizations;
    }

    /**
     * @see org.kuali.module.vendor.util.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting( Object toCompare ) {
        if( ( ObjectUtils.isNull( toCompare ) ) || !( toCompare instanceof VendorContract ) ) {
            return false;
        } else {
            VendorContract vc = (VendorContract)toCompare;
            boolean eq = new EqualsBuilder()
                .append( this.getVendorContractGeneratedIdentifier(), vc.getVendorContractGeneratedIdentifier() )
                .append( this.getVendorHeaderGeneratedIdentifier(), vc.getVendorHeaderGeneratedIdentifier() )
                .append( this.getVendorDetailAssignedIdentifier(), vc.getVendorDetailAssignedIdentifier() )
                .append( this.getVendorContractName(), vc.getVendorContractName() )
                .append( this.getVendorContractDescription(), vc.getVendorContractDescription() )
                .append( this.getVendorCampusCode(), vc.getVendorCampusCode() )
                .append( this.getVendorContractBeginningDate(), vc.getVendorContractBeginningDate() )
                .append( this.getVendorContractEndDate(), vc.getVendorContractEndDate() )
                .append( this.getContractManagerCode(), vc.getContractManagerCode() )
                .append( this.getPurchaseOrderCostSourceCode(), vc.getPurchaseOrderCostSourceCode() )
                .append( this.getVendorPaymentTermsCode(), vc.getVendorPaymentTermsCode() )
                .append( this.getVendorShippingPaymentTermsCode(), vc.getVendorShippingPaymentTermsCode() )
                .append( this.getVendorShippingTitleCode(), vc.getVendorShippingTitleCode() )
                .append( this.getVendorContractExtensionDate(), vc.getVendorContractExtensionDate() )
                .append( this.getVendorB2bIndicator(), vc.getVendorB2bIndicator() )
                .append( this.getOrganizationAutomaticPurchaseOrderLimit(),
                        vc.getOrganizationAutomaticPurchaseOrderLimit() )
                .isEquals();
            eq &= SpringContext.getBean(VendorService.class).equalMemberLists( this.getVendorContractOrganizations(),
                    vc.getVendorContractOrganizations() );
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

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
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.module.vendor.service.impl.VendorServiceImpl;

/**
 * 
 */
public class VendorHeader extends PersistableBusinessObjectBase {
    private static Logger LOG = Logger.getLogger(VendorHeader.class);
    
	private Integer vendorHeaderGeneratedIdentifier;
	private String vendorTypeCode;
	private String vendorTaxNumber;
    private String vendorTaxTypeCode;
    private String vendorOwnershipCode;
	private String vendorOwnershipCategoryCode;
	private Date vendorFederalWithholdingTaxBeginningDate;
	private Date vendorFederalWithholdingTaxEndDate;
	private Boolean vendorW9ReceivedIndicator;
	private Boolean vendorW8BenReceivedIndicator;
    private Boolean vendorDebarredIndicator;
    private Boolean vendorForeignIndicator;
    
	private VendorType vendorType;
	private OwnershipType vendorOwnership;
	private OwnershipCategory vendorOwnershipCategory;
    private List<VendorSupplierDiversity> vendorSupplierDiversities;
    private List<VendorTaxChange> vendorTaxChanges;
    
	/**
	 * Default constructor.
	 */
	public VendorHeader() {
        vendorSupplierDiversities = new TypedArrayList(VendorSupplierDiversity.class);
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
	 * Gets the vendorTypeCode attribute.
	 * 
	 * @return Returns the vendorTypeCode
	 * 
	 */
	public String getVendorTypeCode() { 
		return vendorTypeCode;
	}

	/**
	 * Sets the vendorTypeCode attribute.
	 * 
	 * @param vendorTypeCode The vendorTypeCode to set.
	 * 
	 */
	public void setVendorTypeCode(String vendorTypeCode) {
		this.vendorTypeCode = vendorTypeCode;
	}


	/**
	 * Gets the vendorTaxNumber attribute.
	 * 
	 * @return Returns the vendorTaxNumber
	 * 
	 */
	public String getVendorTaxNumber() { 
		return vendorTaxNumber;
	}

	/**
	 * Sets the vendorTaxNumber attribute.
	 * 
	 * @param vendorTaxNumber The vendorTaxNumber to set.
	 * 
	 */
	public void setVendorTaxNumber(String vendorTaxNumber) {
		this.vendorTaxNumber = vendorTaxNumber;
	}

    /**
     * Gets the vendorTaxTypeCode attribute. 
     * @return Returns the vendorTaxTypeCode.
     */
    public String getVendorTaxTypeCode() {
        return vendorTaxTypeCode;
    }

    /**
     * Sets the vendorTaxTypeCode attribute value.
     * @param vendorTaxTypeCode The vendorTaxTypeCode to set.
     */
    public void setVendorTaxTypeCode(String vendorTaxTypeCode) {
        this.vendorTaxTypeCode = vendorTaxTypeCode;
    } 
    
	/**
	 * Gets the vendorOwnershipCode attribute.
	 * 
	 * @return Returns the vendorOwnershipCode
	 * 
	 */
	public String getVendorOwnershipCode() { 
		return vendorOwnershipCode;
	}

	/**
	 * Sets the vendorOwnershipCode attribute.
	 * 
	 * @param vendorOwnershipCode The vendorOwnershipCode to set.
	 * 
	 */
	public void setVendorOwnershipCode(String vendorOwnershipCode) {
		this.vendorOwnershipCode = vendorOwnershipCode;
	}


	/**
	 * Gets the vendorOwnershipCategoryCode attribute.
	 * 
	 * @return Returns the vendorOwnershipCategoryCode
	 * 
	 */
	public String getVendorOwnershipCategoryCode() { 
		return vendorOwnershipCategoryCode;
	}

	/**
	 * Sets the vendorOwnershipCategoryCode attribute.
	 * 
	 * @param vendorOwnershipCategoryCode The vendorOwnershipCategoryCode to set.
	 * 
	 */
	public void setVendorOwnershipCategoryCode(String vendorOwnershipCategoryCode) {
		this.vendorOwnershipCategoryCode = vendorOwnershipCategoryCode;
	}


	/**
	 * Gets the vendorFederalWithholdingTaxBeginningDate attribute.
	 * 
	 * @return Returns the vendorFederalWithholdingTaxBeginningDate
	 * 
	 */
	public Date getVendorFederalWithholdingTaxBeginningDate() { 
		return vendorFederalWithholdingTaxBeginningDate;
	}

	/**
	 * Sets the vendorFederalWithholdingTaxBeginningDate attribute.
	 * 
	 * @param vendorFederalWithholdingTaxBeginningDate The vendorFederalWithholdingTaxBeginningDate to set.
	 * 
	 */
	public void setVendorFederalWithholdingTaxBeginningDate(Date vendorFederalWithholdingTaxBeginningDate) {
		this.vendorFederalWithholdingTaxBeginningDate = vendorFederalWithholdingTaxBeginningDate;
	}


	/**
	 * Gets the vendorFederalWithholdingTaxEndDate attribute.
	 * 
	 * @return Returns the vendorFederalWithholdingTaxEndDate
	 * 
	 */
	public Date getVendorFederalWithholdingTaxEndDate() { 
		return vendorFederalWithholdingTaxEndDate;
	}

	/**
	 * Sets the vendorFederalWithholdingTaxEndDate attribute.
	 * 
	 * @param vendorFederalWithholdingTaxEndDate The vendorFederalWithholdingTaxEndDate to set.
	 * 
	 */
	public void setVendorFederalWithholdingTaxEndDate(Date vendorFederalWithholdingTaxEndDate) {
		this.vendorFederalWithholdingTaxEndDate = vendorFederalWithholdingTaxEndDate;
	}


	/**
	 * Gets the vendorW9ReceivedIndicator attribute.
	 * 
	 * @return Returns the vendorW9ReceivedIndicator
	 * 
	 */
	public Boolean getVendorW9ReceivedIndicator() { 
		return vendorW9ReceivedIndicator;
	}
	

	/**
	 * Sets the vendorW9ReceivedIndicator attribute.
	 * 
	 * @param vendorW9ReceivedIndicator The vendorW9ReceivedIndicator to set.
	 * 
	 */
	public void setVendorW9ReceivedIndicator(Boolean vendorW9ReceivedIndicator) {
		this.vendorW9ReceivedIndicator = vendorW9ReceivedIndicator;
	}


	/**
	 * Gets the vendorW8BenReceivedIndicator attribute.
	 * 
	 * @return Returns the vendorW8BenReceivedIndicator
	 * 
	 */
	public Boolean getVendorW8BenReceivedIndicator() { 
		return vendorW8BenReceivedIndicator;
	}
	

	/**
	 * Sets the vendorW8BenReceivedIndicator attribute.
	 * 
	 * @param vendorW8BenReceivedIndicator The vendorW8BenReceivedIndicator to set.
	 * 
	 */
	public void setVendorW8BenReceivedIndicator(Boolean vendorW8BenReceivedIndicator) {
		this.vendorW8BenReceivedIndicator = vendorW8BenReceivedIndicator;
	}

	/**
	 * Gets the vendorType attribute.
	 * 
	 * @return Returns the vendorType
	 * 
	 */
	public VendorType getVendorType() { 
		return vendorType;
	}

	/**
	 * Sets the vendorType attribute.
	 * 
	 * @param vendorType The vendorType to set.
	 * @deprecated
	 */
	public void setVendorType(VendorType vendorType) {
		this.vendorType = vendorType;
	}

	/**
	 * Gets the vendorOwnership attribute.
	 * 
	 * @return Returns the vendorOwnership
	 * 
	 */
	public OwnershipType getVendorOwnership() { 
		return vendorOwnership;
	}

	/**
	 * Sets the vendorOwnership attribute.
	 * 
	 * @param vendorOwnership The vendorOwnership to set.
	 * @deprecated
	 */
	public void setVendorOwnership(OwnershipType vendorOwnership) {
		this.vendorOwnership = vendorOwnership;
	}

	/**
	 * Gets the vendorOwnershipCategory attribute.
	 * 
	 * @return Returns the vendorOwnershipCategory
	 * 
	 */
	public OwnershipCategory getVendorOwnershipCategory() { 
		return vendorOwnershipCategory;
	}

	/**
	 * Sets the vendorOwnershipCategory attribute.
	 * 
	 * @param vendorOwnershipCategory The vendorOwnershipCategory to set.
	 * @deprecated
	 */
	public void setVendorOwnershipCategory(OwnershipCategory vendorOwnershipCategory) {
		this.vendorOwnershipCategory = vendorOwnershipCategory;
	}

    /**
     * Gets the vendorDebarredIndicator attribute. 
     * @return Returns the vendorDebarredIndicator.
     */
    public Boolean getVendorDebarredIndicator() {
        return vendorDebarredIndicator;
    }

    /**
     * Sets the vendorDebarredIndicator attribute value.
     * @param vendorDebarredIndicator The vendorDebarredIndicator to set.
     */
    public void setVendorDebarredIndicator(Boolean vendorDebarredIndicator) {
        this.vendorDebarredIndicator = vendorDebarredIndicator;
    }

    /**
     * Gets the vendorForeignIndicator attribute. 
     * @return Returns the vendorForeignIndicator.
     */
    public Boolean getVendorForeignIndicator() {
        return vendorForeignIndicator;
    }

    /**
     * Sets the vendorForeignIndicator attribute value.
     * @param vendorForeignIndicator The vendorForeignIndicator to set.
     */
    public void setVendorForeignIndicator(Boolean vendorForeignIndicator) {
        this.vendorForeignIndicator = vendorForeignIndicator;
    }

    /**
     * Gets the vendorSupplierDiversities attribute. 
     * @return Returns the vendorSupplierDiversities.
     */
    public List<VendorSupplierDiversity> getVendorSupplierDiversities() {
        return vendorSupplierDiversities;
    }

    /**
     * Sets the vendorSupplierDiversities attribute value.
     * @param vendorSupplierDiversities The vendorSupplierDiversities to set.
     */
    public void setVendorSupplierDiversities(List<VendorSupplierDiversity> vendorSupplierDiversities) {
        this.vendorSupplierDiversities = vendorSupplierDiversities;
    }

    /**
     * Gets the vendorTaxChanges attribute. 
     * @return Returns the vendorTaxChanges.
     */
    public List<VendorTaxChange> getVendorTaxChanges() {
        return vendorTaxChanges;
    }

    /**
     * Sets the vendorTaxChanges attribute value.
     * @param vendorTaxChanges The vendorTaxChanges to set.
     */
    public void setVendorTaxChanges(List<VendorTaxChange> vendorTaxChanges) {
        this.vendorTaxChanges = vendorTaxChanges;
    }

    /**
     * This method is a predicate to test equality of all the persisted attributes of an instance 
     * of this class, including member collections.  This is used to determine whether to route
     * 
     * @param       vh  Another VendorHeader object    
     * @return      True if all non-derived attributes of the given object are equal to this one's
     */
    public boolean isEqualForRouting( Object toCompare ) {
        LOG.debug( "Entering isEqualForRouting." );
        if( !( toCompare instanceof VendorHeader ) ) {
            return false;
        } else {
            VendorHeader vh = (VendorHeader)toCompare;
            return new EqualsBuilder()
                .append( this.getVendorTypeCode(), vh.getVendorTypeCode() )
                .append( this.getVendorTaxNumber(), vh.getVendorTaxNumber() )
                .append( this.getVendorOwnershipCode(), vh.getVendorOwnershipCode() )
                .append( this.getVendorOwnershipCategoryCode(), vh.getVendorOwnershipCategoryCode() )
                .append( this.getVendorFederalWithholdingTaxBeginningDate(),
                        vh.getVendorFederalWithholdingTaxBeginningDate() )
                .append( this.getVendorFederalWithholdingTaxEndDate(), 
                        vh.getVendorFederalWithholdingTaxEndDate() )
                .append( this.getVendorW9ReceivedIndicator(), vh.getVendorW9ReceivedIndicator() )
                .append( this.getVendorW8BenReceivedIndicator(), vh.getVendorW8BenReceivedIndicator() )
                .append( this.getVendorDebarredIndicator(), vh.getVendorDebarredIndicator() )
                .append( this.getVendorForeignIndicator(), vh.getVendorForeignIndicator() )
                .isEquals();
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
        return m;
    }
}

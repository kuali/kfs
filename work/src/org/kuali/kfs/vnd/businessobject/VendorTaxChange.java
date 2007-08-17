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

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.context.SpringContext;

/**
 * 
 */
public class VendorTaxChange extends PersistableBusinessObjectBase {

	private Integer vendorTaxChangeGeneratedIdentifier;
	private Integer vendorHeaderGeneratedIdentifier;
	private Date vendorTaxChangeDate;
	private String vendorPreviousTaxNumber;
	private String vendorPreviousTaxTypeCode;
	private String vendorTaxChangePersonIdentifier;

    private UniversalUser vendorTaxChangePerson;
    private VendorHeader vendorHeader;
    
    /**
	 * Default constructor.
	 */
	public VendorTaxChange() {

	}

    public VendorTaxChange( Integer vndrHdrGenId, Date taxChangeDate, String prevTaxNum, 
            String prevTaxTypeCode, String taxChangePersonId ) {
        this.vendorHeaderGeneratedIdentifier = vndrHdrGenId;
        this.vendorTaxChangeDate = taxChangeDate;
        this.vendorPreviousTaxNumber = prevTaxNum;
        this.vendorPreviousTaxTypeCode = prevTaxTypeCode;
        this.vendorTaxChangePersonIdentifier = taxChangePersonId;
    }

	/**
	 * Gets the vendorTaxChangeGeneratedIdentifier attribute.
	 * 
	 * @return Returns the vendorTaxChangeGeneratedIdentifier
	 * 
	 */
	public Integer getVendorTaxChangeGeneratedIdentifier() { 
		return vendorTaxChangeGeneratedIdentifier;
	}

	/**
	 * Sets the vendorTaxChangeGeneratedIdentifier attribute.
	 * 
	 * @param vendorTaxChangeGeneratedIdentifier The vendorTaxChangeGeneratedIdentifier to set.
	 * 
	 */
	public void setVendorTaxChangeGeneratedIdentifier(Integer vendorTaxChangeGeneratedIdentifier) {
		this.vendorTaxChangeGeneratedIdentifier = vendorTaxChangeGeneratedIdentifier;
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
	 * Gets the vendorTaxChangeDate attribute.
	 * 
	 * @return Returns the vendorTaxChangeDate
	 * 
	 */
	public Date getVendorTaxChangeDate() { 
		return vendorTaxChangeDate;
	}

	/**
	 * Sets the vendorTaxChangeDate attribute.
	 * 
	 * @param vendorTaxChangeDate The vendorTaxChangeDate to set.
	 * 
	 */
	public void setVendorTaxChangeDate(Date vendorTaxChangeDate) {
		this.vendorTaxChangeDate = vendorTaxChangeDate;
	}


	/**
	 * Gets the vendorPreviousTaxNumber attribute.
	 * 
	 * @return Returns the vendorPreviousTaxNumber
	 * 
	 */
	public String getVendorPreviousTaxNumber() { 
		return vendorPreviousTaxNumber;
	}

	/**
	 * Sets the vendorPreviousTaxNumber attribute.
	 * 
	 * @param vendorPreviousTaxNumber The vendorPreviousTaxNumber to set.
	 * 
	 */
	public void setVendorPreviousTaxNumber(String vendorPreviousTaxNumber) {
		this.vendorPreviousTaxNumber = vendorPreviousTaxNumber;
	}

    /**
     * Gets the vendorPreviousTaxTypeCode attribute. 
     * @return Returns the vendorPreviousTaxTypeCode.
     */
    public String getVendorPreviousTaxTypeCode() {
        return vendorPreviousTaxTypeCode;
    }

    /**
     * Sets the vendorPreviousTaxTypeCode attribute value.
     * @param vendorPreviousTaxTypeCode The vendorPreviousTaxTypeCode to set.
     */
    public void setVendorPreviousTaxTypeCode(String vendorPreviousTaxTypeCode) {
        this.vendorPreviousTaxTypeCode = vendorPreviousTaxTypeCode;
    }    

	/**
	 * Gets the vendorTaxChangePersonIdentifier attribute.
	 * 
	 * @return Returns the vendorTaxChangePersonIdentifier
	 * 
	 */
	public String getVendorTaxChangePersonIdentifier() { 
		return vendorTaxChangePersonIdentifier;
	}

	/**
	 * Sets the vendorTaxChangePersonIdentifier attribute.
	 * 
	 * @param vendorTaxChangePersonIdentifier The vendorTaxChangePersonIdentifier to set.
	 * 
	 */
	public void setVendorTaxChangePersonIdentifier(String vendorTaxChangePersonIdentifier) {
		this.vendorTaxChangePersonIdentifier = vendorTaxChangePersonIdentifier;
	}

	public UniversalUser getVendorTaxChangePerson() {
        vendorTaxChangePerson = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(vendorTaxChangePersonIdentifier, vendorTaxChangePerson);
        return vendorTaxChangePerson;
    }

    /**
     * Sets the vendorTaxChangePerson attribute.
     * 
     * @param vendorTaxChangePerson The vendorTaxChangePerson to set.
     * @deprecated
     */
    public void setVendorTaxChangePerson(UniversalUser vendorTaxChangePerson) {
        this.vendorTaxChangePerson = vendorTaxChangePerson;
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
     * @deprecated
     */
    public void setVendorHeader(VendorHeader vendorHeader) {
        this.vendorHeader = vendorHeader;
    }    
    
    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.vendorTaxChangeGeneratedIdentifier != null) {
            m.put("vendorTaxChangeGeneratedIdentifier", this.vendorTaxChangeGeneratedIdentifier.toString());
        }
	    return m;
    }

}

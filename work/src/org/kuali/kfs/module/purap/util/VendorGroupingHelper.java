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
package org.kuali.kfs.module.purap.util;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;

public class VendorGroupingHelper implements Comparable {
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorCountry;
    private String vendorPostalCode;
    
    public VendorGroupingHelper( PurchasingAccountsPayableDocument doc ) {
        vendorHeaderGeneratedIdentifier = doc.getVendorHeaderGeneratedIdentifier();
        vendorDetailAssignedIdentifier = doc.getVendorDetailAssignedIdentifier();
        vendorCountry = doc.getVendorCountryCode();
        vendorPostalCode = doc.getVendorPostalCode();
        if ( vendorPostalCode != null && vendorPostalCode.length() > 5 ) {
            vendorPostalCode = vendorPostalCode.substring(0, 5);
        }
    }
    
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public String getVendorCountry() {
        return vendorCountry;
    }

    public String getVendorPostalCode() {
        return vendorPostalCode;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return vendorHeaderGeneratedIdentifier + "-" + vendorDetailAssignedIdentifier + "-" + vendorCountry + "-" + vendorPostalCode;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
    	if ( !(object instanceof VendorGroupingHelper) ) {
    		return false;
    	}
    	VendorGroupingHelper rhs = (VendorGroupingHelper)object;
    	return new EqualsBuilder().append(
    			this.vendorPostalCode, rhs.vendorPostalCode ).append(
    			this.vendorHeaderGeneratedIdentifier, rhs.vendorHeaderGeneratedIdentifier ).append(
    			this.vendorDetailAssignedIdentifier, rhs.vendorDetailAssignedIdentifier ).append(
    			this.vendorCountry, rhs.vendorCountry ).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
    	return new HashCodeBuilder( -999235111, -1951404497 ).append( this.vendorPostalCode )
    	        .append( this.vendorHeaderGeneratedIdentifier )
    			.append( this.vendorDetailAssignedIdentifier ).append( this.vendorCountry )
    			.toHashCode();
    }

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object object) {
    	VendorGroupingHelper myClass = (VendorGroupingHelper)object;
    	return new CompareToBuilder().append( this.vendorPostalCode, myClass.vendorPostalCode )
    			.append( this.vendorHeaderGeneratedIdentifier,
    					myClass.vendorHeaderGeneratedIdentifier )
    			.append( this.vendorDetailAssignedIdentifier,
    					myClass.vendorDetailAssignedIdentifier ).append( this.vendorCountry,
    					myClass.vendorCountry ).toComparison();
    }

    
    
}

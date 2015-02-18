/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

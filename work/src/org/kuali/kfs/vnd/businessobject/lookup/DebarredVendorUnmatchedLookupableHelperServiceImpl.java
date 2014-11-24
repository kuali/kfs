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
package org.kuali.kfs.vnd.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.vnd.batch.service.VendorExcludeService;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class DebarredVendorUnmatchedLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    VendorExcludeService vendorExcludeService;
    VendorService vendorService;

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return getDebarredVendorUnmatchedSearchResults(fieldValues);
    }

    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getDebarredVendorUnmatchedSearchResults(fieldValues);
    }

    protected List<? extends BusinessObject> getDebarredVendorUnmatchedSearchResults(Map<String, String> fieldValues) {
        List<VendorDetail> vendorResultList = vendorExcludeService.getDebarredVendorsUnmatched();
        List<VendorDetail> filteredVendorList = new ArrayList<VendorDetail> ();
        VendorAddress defaultAddress;
        String vendorType = fieldValues.get("vendorTypeCode");
        for (VendorDetail vendor : vendorResultList) {
            if (!StringUtils.isEmpty(vendorType) && !vendor.getVendorHeader().getVendorTypeCode().equals(vendorType)) {
                continue;
            }
            defaultAddress = vendorService.getVendorDefaultAddress(vendor.getVendorAddresses(), vendor.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
            if (defaultAddress != null ) {
                vendor.setDefaultAddressLine1(defaultAddress.getVendorLine1Address());
                vendor.setDefaultAddressCity(defaultAddress.getVendorCityName());
                vendor.setDefaultAddressStateCode(defaultAddress.getVendorStateCode());
                vendor.setDefaultAddressCountryCode(defaultAddress.getVendorCountryCode());
            }
            filteredVendorList.add(vendor);
        }
        return filteredVendorList;
    }

    /**
     * Gets the vendorExcludeService attribute.
     * @return Returns the vendorExcludeService.
     */
    public VendorExcludeService getVendorExcludeService() {
        return vendorExcludeService;
    }

    /**
     * Sets the vendorExcludeService attribute value.
     * @param vendorExcludeService The vendorExcludeService to set.
     */
    public void setVendorExcludeService(VendorExcludeService vendorExcludeService) {
        this.vendorExcludeService = vendorExcludeService;
    }

    /**
     * Gets the vendorService attribute.
     * @return Returns the vendorService.
     */
    public VendorService getVendorService() {
        return vendorService;
    }

    /**
     * Sets the vendorService attribute value.
     * @param vendorService The vendorService to set.
     */
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }
}

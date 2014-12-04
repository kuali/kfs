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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorContact;
import org.kuali.kfs.vnd.businessobject.VendorContactPhoneNumber;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.BeanPropertyComparator;

public class VendorContactLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    /**
     * Overrides the getSearchResults in the super class so that we can do some customization in our vendor contact lookup. For
     * example, we want to be able to display the first phone number, fax number and toll free number in the vendor contact.
     * 
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<PersistableBusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean unbounded = false;
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        List<PersistableBusinessObject> searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);

        // loop through results
        for (PersistableBusinessObject object : searchResults) {
            VendorContact vendorContact = (VendorContact) object;

            for (VendorContactPhoneNumber phoneNumber : vendorContact.getVendorContactPhoneNumbers()) {
                String extension = phoneNumber.getVendorPhoneExtensionNumber();
                if (phoneNumber.getVendorPhoneType().getVendorPhoneTypeCode().equals(VendorConstants.PhoneTypes.PHONE) && StringUtils.isEmpty(vendorContact.getPhoneNumberForLookup())) {
                    vendorContact.setPhoneNumberForLookup(phoneNumber.getVendorPhoneNumber() + ((StringUtils.isNotEmpty(extension)) ? " x " + extension : null));
                }
                else if (phoneNumber.getVendorPhoneType().getVendorPhoneTypeCode().equals(VendorConstants.PhoneTypes.FAX) && StringUtils.isBlank(vendorContact.getFaxForLookup())) {
                    vendorContact.setFaxForLookup(phoneNumber.getVendorPhoneNumber() + ((StringUtils.isNotEmpty(extension)) ? " x " + extension : KFSConstants.EMPTY_STRING));
                }
                else if (phoneNumber.getVendorPhoneType().getVendorPhoneTypeCode().equals(VendorConstants.PhoneTypes.TOLL_FREE) && StringUtils.isBlank(vendorContact.getTollFreeForLookup())) {
                    vendorContact.setTollFreeForLookup(phoneNumber.getVendorPhoneNumber() + ((StringUtils.isNotEmpty(extension)) ? " x " + extension : KFSConstants.EMPTY_STRING));
                }
            }
        }

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return searchResults;
    }

}

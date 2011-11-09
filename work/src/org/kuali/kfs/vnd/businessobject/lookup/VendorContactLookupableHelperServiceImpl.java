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

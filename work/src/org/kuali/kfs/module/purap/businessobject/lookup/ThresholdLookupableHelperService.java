/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.lookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorKeyConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This lookupable helper service is used to support lookups on the Threshold BO because it deals with the vendor number,
 * which isn't really a field, but rather a combination of 2 fields.
 * 
 * This code mostly copies {@link org.kuali.core.lookup.KualiLookupableHelperServiceImpl}, but differs in that this class will
 * not remove search criteria containing values corresponding to hidden fields.
 * 
 */
public class ThresholdLookupableHelperService extends AbstractLookupableHelperServiceImpl {
    
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
        
        validateVendorNumber(fieldValues);
    }

    /**
     * Validates that the Vendor Number has no more than one dash in it, and does not consist solely of one dash. Then it calls
     * extractVendorNumberToVendorIds to obtain vendorHeaderGeneratedId and vendorDetailAssignedId and if either one of the ids
     * cannot be converted to integers, it will add error that the vendor number must be numerics or numerics separated by a dash.
     * 
     * @param fieldValues a Map containing only those key-value pairs that have been filled in on the lookup
     */
    protected void validateVendorNumber(Map fieldValues) {
        String vendorNumber = (String) fieldValues.get(VendorPropertyConstants.VENDOR_NUMBER);
        if (StringUtils.isNotBlank(vendorNumber)) {
            int dashPos1 = vendorNumber.indexOf(VendorConstants.DASH);
            if (dashPos1 > -1) { // There's a dash in the number.
                if (vendorNumber.indexOf(VendorConstants.DASH, dashPos1 + 1) > -1) { // There can't be more than one.
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_TOO_MANY_DASHES);
                }
                if (vendorNumber.matches("\\-*")) {
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_DASHES_ONLY);
                }
            }
            extractVendorNumberToVendorIds(fieldValues, vendorNumber);
        }
    }
    
    /**
     * Parses the vendorNumber string into vendorHeaderGeneratedIdentifier and vendorDetailAssignedIdentifier, validates that both
     * fields would be able to be converted into integers, if so it will add both fields into the search criterias map in the
     * fieldValues and remove the vendorNumber from the fieldValues. If the two fields cannot be converted into integers, this
     * method will add error message to the errorMap in GlobalVariables that the vendor number must be numeric or numerics separated
     * by a dash.
     * 
     * @param fieldValues a Map containing only those key-value pairs that have been filled in on the lookup
     * @param vendorNumber venodr number String
     */
    protected void extractVendorNumberToVendorIds(Map fieldValues, String vendorNumber) {
        String vendorHeaderGeneratedIdentifier = null;
        String vendorDetailAssignedIdentifier = null;
        int indexOfDash = vendorNumber.indexOf(VendorConstants.DASH);
        if (indexOfDash < 0) {
            vendorHeaderGeneratedIdentifier = vendorNumber;
        }
        else {
            vendorHeaderGeneratedIdentifier = vendorNumber.substring(0, indexOfDash);
            vendorDetailAssignedIdentifier = vendorNumber.substring(indexOfDash + 1, vendorNumber.length());
        }
        try {
            if (StringUtils.isNotEmpty(vendorHeaderGeneratedIdentifier)) {
                Integer.parseInt(vendorHeaderGeneratedIdentifier);
            }
            if (StringUtils.isNotEmpty(vendorDetailAssignedIdentifier)) {
                Integer.parseInt(vendorDetailAssignedIdentifier);
            }
            fieldValues.remove(VendorPropertyConstants.VENDOR_NUMBER);
            fieldValues.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier);
            fieldValues.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier);
        }
        catch (NumberFormatException headerExc) {
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_NUMERIC_DASH_SEPARATED);
        }
    }
    
    /**
     * Uses Lookup Service to provide a basic search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     *
     * @return List found business objects
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {        
        return getSearchResultsHelper(LookupUtils.forceUppercase(getBusinessObjectClass(), fieldValues), false);
    }
    

    /**
     * Uses Lookup Service to provide a basic unbounded search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * 
     * @return List found business objects
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResultsUnbounded(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getSearchResultsHelper(LookupUtils.forceUppercase(getBusinessObjectClass(), fieldValues), true);
    }

    /**
     * 
     * This method does the actual search, with the parameters specified, and returns the result.
     * 
     * NOTE that it will not do any upper-casing based on the DD forceUppercase. That is handled through an external call to
     * LookupUtils.forceUppercase().
     * 
     * @param fieldValues A Map of the fieldNames and fieldValues to be searched on.
     * @param unbounded Whether the results should be bounded or not to a certain max size.
     * @return A List of search results.
     * 
     */
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // pretty much the same code as exists in KualiLookupableHelperServiceImpl, except that we're not removing hidden fields

        boolean searchUsingOnlyPrimaryKeyValues = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(KNSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KNSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH));
        List searchResults;
        if (UniversalUser.class.equals(getBusinessObjectClass())) {
            searchResults = (List) getUniversalUserService().findUniversalUsers(fieldValues);
        }
        else if (getUniversalUserService().hasUniversalUserProperty(getBusinessObjectClass(), fieldValues)) {
            // TODO WARNING: this does not support nested joins, because i don't have a test case
            searchResults = (List) getUniversalUserService().findWithUniversalUserJoin(getBusinessObjectClass(), fieldValues, unbounded);
        }
        else {
            searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);
        }
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }
}

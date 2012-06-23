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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;

public class VendorContractLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private LookupDao lookupDao;
    private DateTimeService dateTimeService;

    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Overrides the getSearchResults in the super class so that we can do some customization in our vendor contract lookup.
     * 
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<PersistableBusinessObject> getSearchResults(Map<String, String> fieldValues) {

        boolean unbounded = false;
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        Date now = dateTimeService.getCurrentSqlDate();
        String nowString = dateTimeService.toDateString(now);

        // We ought to call the findCollectionBySearchHelper that would accept the additionalCriteria
        boolean usePrimaryKeyValuesOnly = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);
        List<PersistableBusinessObject> searchResults = (List) lookupDao.findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded, usePrimaryKeyValuesOnly);

        List<PersistableBusinessObject> finalSearchResults = new ArrayList();
        // loop through results to eliminate inactive or debarred vendors
        for (PersistableBusinessObject object : searchResults) {
            VendorContract vendorContract = (VendorContract) object;
            if (!vendorContract.getVendorDetail().isVendorDebarred()) {
                finalSearchResults.add(vendorContract);
            }
        }

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(finalSearchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return finalSearchResults;
    }

    /**
     * Overrides a method of the superclass and is now called instead of that one by the Search method of KualiLookupAction when the
     * Lookupable is of this class. This method first calls the method from the superclass, which should do all the required field
     * checking, and then goes through all the specific validations which aren't done at the JSP level. Both the superclass
     * method and the various validation methods side-effect the adding of errors to the global error map when the input is found to
     * have an issue.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        validateVendorNumber(fieldValues);

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("Error(s) in search criteria");
        }
    }

    /**
     * Validates that the Vendor Number has no more than one dash in it, and does not consist solely of one dash. Then it calls
     * extractVendorNumberToVendorIds to obtain vendorHeaderGeneratedId and vendorDetailAssignedId and if either one of the ids
     * cannot be converted to integers, it will add error that the vendor number must be numerics or numerics separated by a dash.
     *
     * @param fieldValues a Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateVendorNumber(Map fieldValues) {
        String vendorNumber = (String) fieldValues.get(VendorPropertyConstants.VENDOR_NUMBER);
        if (StringUtils.isNotBlank(vendorNumber)) {
            int dashPos1 = vendorNumber.indexOf(VendorConstants.DASH);
            if (dashPos1 > -1) { // There's a dash in the number.
                if (vendorNumber.indexOf(VendorConstants.DASH, dashPos1 + 1) > -1) { // There can't be more than one.
                    GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_TOO_MANY_DASHES);
                }
                if (vendorNumber.matches("\\-*")) {
                    GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_DASHES_ONLY);
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
     * @param vendorNumber vendor number String
     */
    private void extractVendorNumberToVendorIds(Map fieldValues, String vendorNumber) {
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
            GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_NUMERIC_DASH_SEPARATED);
        }
    }
    
}

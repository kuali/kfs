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
package org.kuali.module.vendor.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.format.Formatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorKeyConstants;
import org.kuali.module.vendor.VendorParameterConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;

public class VendorLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private VendorService vendorService;
    private ParameterService parameterService;

    private static String VNDR_LOOKUP_MIN_NAME_LENGTH;
    private static String VNDR_MIN_NUM_LOOKUP_CRITERIA;

    /**
     * Allows only active parent vendors to create new divisions
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject bo) {
        VendorDetail vendor = (VendorDetail) bo;
        StringBuffer actions = new StringBuffer(super.getActionUrls(bo));
        actions.append("&nbsp;&nbsp;");
        if (vendor.isVendorParentIndicator() && vendor.isActiveIndicator()) {
            // only allow active parent vendors to create new divisions
            actions.append(getMaintenanceUrl(bo, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION));
        }

        return actions.toString();
    }

    /**
     * Used by getActionUrls to print the url on the Vendor Lookup page for the links to edit a Vendor or to create a new division.
     * We won't provide a link to copy a vendor because we decided it wouldn't make sense to copy a vendor. We should display the
     * link to create a new division only if the vendor is a parent vendor, and also remove the vendor detail assigned id from the
     * query string in the link to create a new division. We'll add the vendor detail assigned id in the query string if the vendor
     * is not a parent, or if the vendor is a parent and the link is not the create new division link (i.e. if the link is "edit").
     * We'll always add the vendor header id in the query string in all links.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getMaintenanceUrl(org.kuali.core.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public String getMaintenanceUrl(BusinessObject bo, String methodToCall) {
        if (!methodToCall.equals(KFSConstants.COPY_METHOD)) {
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, bo.getClass().getName());

            List pkNames = getPersistenceStructureService().listPrimaryKeyFieldNames(getBusinessObjectClass());
            for (Iterator<String> iter = pkNames.iterator(); iter.hasNext();) {
                String fieldNm = iter.next();
                if (!fieldNm.equals(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID) || !((VendorDetail) bo).isVendorParentIndicator() || (((VendorDetail) bo).isVendorParentIndicator()) && !methodToCall.equals(KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION)) {
                    Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
                    if (fieldVal == null) {
                        fieldVal = KFSConstants.EMPTY_STRING;
                    }
                    if (fieldVal instanceof java.sql.Date) {
                        String formattedString = KFSConstants.EMPTY_STRING;
                        if (Formatter.findFormatter(fieldVal.getClass()) != null) {
                            Formatter formatter = Formatter.getFormatter(fieldVal.getClass());
                            formattedString = (String) formatter.format(fieldVal);
                            fieldVal = formattedString;
                        }
                    }
                    parameters.put(fieldNm, fieldVal.toString());
                }
            }
            if (methodToCall.equals(KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION)) {
                methodToCall = VendorConstants.CREATE_NEW_DIVISION;
            }
            String url = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);
            url = "<a href=\"" + url + "\">" + methodToCall + "</a>";
            return url;
        }
        else {

            return KFSConstants.EMPTY_STRING;
        }
    }

    /**
     * Overrides the getSearchResults in the super class so that we can do some customization in our vendor lookup. For example, for
     * vendor name as the search criteria, we want to search both the vendor detail table and the vendor alias table for the vendor
     * name. Display the vendor's default address state in the search results.
     * 
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean unbounded = false;
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String vendorName = fieldValues.get(VendorPropertyConstants.VENDOR_NAME);

        List<BusinessObject> searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);

        // re-run the query against the vendor name alias field if necessary and merge the results
        // this could double the returned results for the search, but there is no alternative at present
        // without refactoring of the lookup service
        if (StringUtils.isNotEmpty(vendorName)) {
            // if searching by vendorName, also search in list of alias names
            fieldValues.put(VendorPropertyConstants.VENDOR_ALIAS_NAME_FULL_PATH, vendorName);
            // also make sure that we only use active aliases to match the query string
            fieldValues.put(VendorPropertyConstants.VENDOR_ALIAS_ACTIVE, "Y");
            fieldValues.remove(VendorPropertyConstants.VENDOR_NAME);
            List<BusinessObject> searchResults2 = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);

            searchResults.addAll(searchResults2);
            if (searchResults instanceof CollectionIncomplete && searchResults2 instanceof CollectionIncomplete) {
                ((CollectionIncomplete) searchResults).setActualSizeIfTruncated(((CollectionIncomplete) searchResults).getActualSizeIfTruncated().longValue() + ((CollectionIncomplete) searchResults2).getActualSizeIfTruncated().longValue());
            }
        }

        List<BusinessObject> processedSearchResults = new ArrayList();

        // loop through results
        for (BusinessObject bo : searchResults) {
            VendorDetail vendor = (VendorDetail) bo;

            // if its a top level vendor, search for its divisions and add them to the appropriate list then add the vendor to the
            // return results
            // if its a division, see if we already have the parent and if not, retrieve it and its divisions then add the parent to
            // the return results


            // If this vendor is not already in the processedSearchResults, let's do further processing (e.g. setting the state for
            // lookup from default address, etc)
            // and then add it in the processedSearchResults.
            if (!processedSearchResults.contains(vendor)) {
                Map<String, String> tmpValues = new HashMap<String, String>();
                List<VendorDetail> relatedVendors = new ArrayList();
                tmpValues.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendor.getVendorHeaderGeneratedIdentifier().toString());
                relatedVendors = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), tmpValues, unbounded);

                for (VendorDetail tmpVendor : relatedVendors) {
                    if (tmpVendor != null && !processedSearchResults.contains(tmpVendor)) {
                        // populate state from default address
                        updatedefaultVendorAddress(tmpVendor);
                        processedSearchResults.add(tmpVendor);
                    }
                }

                if (!processedSearchResults.contains(vendor)) {
                    updatedefaultVendorAddress(vendor);
                    processedSearchResults.add(vendor);
                }
            }
        }

        for (BusinessObject bo : processedSearchResults) {
            VendorDetail vendor = (VendorDetail) bo;
            if (!vendor.isVendorParentIndicator()) {
                // find the parent object in the details collection and add that§
                for (BusinessObject tmpObject : processedSearchResults) {
                    VendorDetail tmpVendor = (VendorDetail) tmpObject;
                    if (tmpVendor.getVendorHeaderGeneratedIdentifier().equals(vendor.getVendorHeaderGeneratedIdentifier()) && tmpVendor.getVendorDetailAssignedIdentifier() == 0) {
                        vendor.setVendorName(tmpVendor.getVendorName() + " > " + vendor.getVendorName());
                        break;
                    }
                }
            }
        }

        searchResults.clear();
        searchResults.addAll(processedSearchResults);

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return searchResults;
    }

    /**
     * Populates address fields from default address
     * 
     * @param vendor venodrDetail
     */
    private void updatedefaultVendorAddress(VendorDetail vendor) {
        VendorAddress defaultAddress = vendorService.getVendorDefaultAddress(vendor.getVendorAddresses(), vendor.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
        if (defaultAddress != null && defaultAddress.getVendorState() != null) {
            vendor.setVendorStateForLookup(defaultAddress.getVendorState().getPostalStateName());
            vendor.setDefaultAddressLine1(defaultAddress.getVendorLine1Address());
            vendor.setDefaultAddressLine2(defaultAddress.getVendorLine2Address());
            vendor.setDefaultAddressCity(defaultAddress.getVendorCityName());
            vendor.setDefaultAddressPostalCode(defaultAddress.getVendorZipCode());
            vendor.setDefaultAddressStateCode(defaultAddress.getVendorStateCode());
            vendor.setDefaultAddressInternationalProvince(defaultAddress.getVendorAddressInternationalProvinceName());
            vendor.setDefaultAddressCountryCode(defaultAddress.getVendorCountryCode());
        }
    }

    /**
     * Overrides a method of the superclass and is now called instead of that one by the Search method of KualiLookupAction when the
     * Lookupable is of this class. This method first calls the method from the superclass, which should do all the required field
     * checking, and then orchestrates all the specific validations which aren't done in at the JSP level. Both the superclass
     * method and the various validation methods side-effect the adding of errors to the global error map when the input is found to
     * have an issue.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        validateVendorNumber(fieldValues);
        validateVendorName(fieldValues);
        validateTaxNumber(fieldValues);

        if (!GlobalVariables.getErrorMap().isEmpty()) {
            throw new ValidationException("Error(s) in search criteria");
        }
    }

    /**
     * Ensures that if a string is entered in the Vendor Name field, it is at least the minimum number of characters in length.
     * 
     * @param fieldValues a Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateVendorName(Map fieldValues) {
        String vendorName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_NAME);
        if (StringUtils.isNotBlank(vendorName)) {
            if (ObjectUtils.isNull(VNDR_LOOKUP_MIN_NAME_LENGTH)) {
                VNDR_LOOKUP_MIN_NAME_LENGTH = parameterService.getParameterValue(VendorDetail.class, VendorParameterConstants.PURAP_VNDR_LOOKUP_MIN_NAME_LENGTH);
            }
            if (vendorName.length() < Integer.parseInt(VNDR_LOOKUP_MIN_NAME_LENGTH)) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NAME, VendorKeyConstants.ERROR_VENDOR_LOOKUP_NAME_TOO_SHORT, VNDR_LOOKUP_MIN_NAME_LENGTH);
            }
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
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_NUMERIC_DASH_SEPARATED);
        }
    }

    /**
     * Validates that the tax number is 9 digits long.
     * 
     * @param fieldValues a Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateTaxNumber(Map fieldValues) {
        String taxNumber = (String) fieldValues.get(VendorPropertyConstants.VENDOR_TAX_NUMBER);
        if (StringUtils.isNotBlank(taxNumber) && (!StringUtils.isNumeric(taxNumber) || taxNumber.length() != 9)) {
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_VENDOR_LOOKUP_TAX_NUM_INVALID);
        }
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}

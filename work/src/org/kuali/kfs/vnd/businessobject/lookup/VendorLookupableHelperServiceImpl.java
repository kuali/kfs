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
package org.kuali.module.vendor.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessRule;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.format.Formatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorKeyConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.VendorRuleConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.dao.VendorDao;
import org.kuali.module.vendor.service.VendorService;

public class VendorLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private VendorService vendorService;
    private KualiConfigurationService kualiConfigurationService;
    
    private static String VNDR_LOOKUP_MIN_NAME_LENGTH;
    private static String VNDR_MIN_NUM_LOOKUP_CRITERIA;
    
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
     * @see org.kuali.core.lookup.KualiLookupableImpl#getMaintenanceUrl(org.kuali.core.bo.BusinessObject, java.lang.String) 
     * 
     * This method is used by getActionUrls to print the url on the Vendor Lookup page for the links to edit a Vendor or to create a
     * new division. We won't provide a link to copy a vendor because we decided it wouldn't make sense to copy a vendor. We
     * should display the link to create a new division only if the vendor is a parent vendor, and also remove the vendor
     * detail assigned id from the query string in the link to create a new division. We'll add the vendor detail assigned id
     * in the query string if the vendor is not a parent, or if the vendor is a parent and the link is not the create new
     * division link (i.e. if the link is "edit"). We'll always add the vendor header id in the query string in all links.
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
                if (!fieldNm.equals(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID) || 
                        !((VendorDetail) bo).isVendorParentIndicator() || 
                        (((VendorDetail) bo).isVendorParentIndicator()) && 
                        !methodToCall.equals(KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION)) {
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
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map) 
     * 
     * This method overrides the getSearchResults in the super class so that we can do some customization 
     * in our vendor lookup. For example, for vendor name as the search criteria, we want to search both 
     * the vendor detail table and the vendor alias table for the vendor name. Display the vendor's default
     * address state in the search results.
     */
    @Override
    public List<BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        boolean unbounded = false;
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String vendorName = fieldValues.get(VendorPropertyConstants.VENDOR_NAME);
        if (StringUtils.isNotEmpty(vendorName)) {
            // if searching by vendorName, also search in list of alias names
            fieldValues.put(VendorPropertyConstants.VENDOR_ALIAS_NAME_FULL_PATH, "|" + vendorName);
        }

        List<BusinessObject> searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);

        List<BusinessObject> processedSearchResults = new ArrayList();
        
        // loop through results
        // if its a top level vendor, search for its divisions and add them to the appropriate list then add the vendor to the return results
        // if its a division, see if we already have the parent and if not, retrieve it and its divisions then add the parent to the return results
        
        // loop through results
        for (BusinessObject bo : searchResults) {
            VendorDetail vendor = (VendorDetail) bo;
            //If this vendor is not already in the processedSearchResults, let's do further processing (e.g. setting the state for lookup from default address, etc)
            //and then add it in the processedSearchResults.
            if (!processedSearchResults.contains(vendor)) {
                // populate state from default address

                if (!vendor.isVendorParentIndicator()) {
                    for (BusinessObject tmpObject : searchResults) {
                        VendorDetail tmpVendor = (VendorDetail) tmpObject;
                        if (tmpVendor.getVendorHeaderGeneratedIdentifier().equals(vendor.getVendorHeaderGeneratedIdentifier()) && tmpVendor.getVendorDetailAssignedIdentifier() == 0) {
                            vendor.setVendorName(tmpVendor.getVendorName() + " > " + vendor.getVendorName());
                            break;
                        }
                    }
                }
                updatedefaultVendorAddress(vendor);
                processedSearchResults.add(vendor);
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
    
    // populate state from default address
    private void updatedefaultVendorAddress(VendorDetail vendor) { 
        VendorAddress defaultAddress = vendorService.getVendorDefaultAddress(vendor.getVendorAddresses(), 
                vendor.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
        if (defaultAddress != null && defaultAddress.getVendorState() != null) {
            vendor.setVendorStateForLookup(defaultAddress.getVendorState().getPostalStateName());
            vendor.setDefaultAddressLine1(defaultAddress.getVendorLine1Address());
            vendor.setDefaultAddressLine2(defaultAddress.getVendorLine2Address());
            vendor.setDefaultAddressCity(defaultAddress.getVendorCityName());
            vendor.setDefaultAddressPostalCode(defaultAddress.getVendorZipCode());
            vendor.setDefaultAddressStateCode(defaultAddress.getVendorStateCode());
            vendor.setDefaultAddressCountryCode(defaultAddress.getVendorCountryCode());
        }
    }
    
    /**
     * This method overrides a method of the superclass and is now called instead of that one by the Search
     * method of KualiLookupAction when the Lookupable is of this class.  This method first calls the method
     * from the superclass, which should do all the required field checking, and then orchestrates all the
     * specific validations which aren't done in at the JSP level.  Both the superclass method and the various
     * validation methods side-effect the adding of errors to the global error map when the input is found
     * to have an issue.
     * 
     * @param fieldValues   A Map containing only those key-value pairs that have been filled in on the lookup
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
        
        validateVendorNumber(fieldValues);
        validateVendorName(fieldValues);
        validateTaxNumber(fieldValues);
        validateNumberOfSearchCriteria(fieldValues);
        validateTypeNameState(fieldValues);
        validateStatusName(fieldValues);
        validateStateType(fieldValues);
        
        if (!GlobalVariables.getErrorMap().isEmpty()) {
            throw new ValidationException("Error(s) in search criteria");
        }
    }
    
    /**
     * This method ensures that, if a string is entered in the Vendor Name field, it is at least
     * the minimum number of characters in length.
     * 
     * @param fieldValues   A Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateVendorName(Map fieldValues) {
        String vendorName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_NAME);
        if (StringUtils.isNotBlank(vendorName)) {
            if (ObjectUtils.isNull(VNDR_LOOKUP_MIN_NAME_LENGTH)) {
                BusinessRule minNameLengthRule = kualiConfigurationService.getApplicationRule(VendorRuleConstants.PURAP_ADMIN_GROUP, 
                        VendorRuleConstants.PURAP_VNDR_LOOKUP_MIN_NAME_LENGTH);
                VNDR_LOOKUP_MIN_NAME_LENGTH = minNameLengthRule.getRuleText();
            }
            if (vendorName.length() < Integer.parseInt(VNDR_LOOKUP_MIN_NAME_LENGTH)) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NAME, 
                        VendorKeyConstants.ERROR_VENDOR_LOOKUP_NAME_TOO_SHORT, VNDR_LOOKUP_MIN_NAME_LENGTH);
            }
        }
    }
    
    /**
     * This method validates that the Vendor Number has no more than one dash in it, and does not consist solely of one dash. Then
     * it calls extractVendorNumberToVendorIds to obtain vendorHeaderGeneratedId and vendorDetailAssignedId and if either one of the
     * ids cannot be converted to integers, it will add error that the vendor number must be numerics or numerics separated by a
     * dash.
     * 
     * @param fieldValues A Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateVendorNumber(Map fieldValues) {
        String vendorNumber = (String) fieldValues.get(VendorPropertyConstants.VENDOR_NUMBER);
        if (StringUtils.isNotBlank(vendorNumber)) {
            int dashPos1 = vendorNumber.indexOf(VendorConstants.DASH);
            if (dashPos1 > -1) { // There's a dash in the number.
                if (vendorNumber.indexOf(VendorConstants.DASH, dashPos1 + 1) > -1) { // There can't be more than one.
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, 
                            VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_TOO_MANY_DASHES);
                }
                if (vendorNumber.matches("\\-*")) {
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, 
                            VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_DASHES_ONLY);
                }
            }
            extractVendorNumberToVendorIds(fieldValues, vendorNumber);
        }
    }
    
    /**
     * This method parses the vendorNumber string into vendorHeaderGeneratedIdentifier and vendorDetailAssignedIdentifier, validates
     * that both fields would be able to be converted into integers, if so it will add both fields into the search criterias map in
     * the fieldValues and remove the vendorNumber from the fieldValues. If the two fields cannot be converted into integers, this
     * method will add error message to the errorMap in GlobalVariables that the vendor number must be numeric or numerics separated
     * by a dash.
     * 
     * @param fieldValues
     * @param vendorNumber
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
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, 
                    VendorKeyConstants.ERROR_VENDOR_LOOKUP_VNDR_NUM_NUMERIC_DASH_SEPARATED);
        }
    }
    
    /**
     * This method validates that the tax number is 9 digits long.
     * 
     * @param fieldValues A Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateTaxNumber(Map fieldValues) {
        String taxNumber = (String) fieldValues.get(VendorPropertyConstants.VENDOR_TAX_NUMBER);
        if (StringUtils.isNotBlank(taxNumber) && (!StringUtils.isNumeric(taxNumber) || taxNumber.length() != 9)) {
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_TAX_NUMBER, 
                    VendorKeyConstants.ERROR_VENDOR_LOOKUP_TAX_NUM_INVALID);
        }
    }
    
    /**
     * This method checks that at least the minimum number of search criteria are selected. Vendor Type does not count as a
     * criterion.
     * 
     * @param fieldValues A Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateNumberOfSearchCriteria(Map fieldValues) {
        int criteria = fieldValues.size();
        String typeCd = (String) fieldValues.get(VendorPropertyConstants.VENDOR_TYPE_CODE);
        if (StringUtils.isNotBlank(typeCd)) {
            criteria--;
        }
        if (ObjectUtils.isNull(VNDR_MIN_NUM_LOOKUP_CRITERIA)) {
            BusinessRule minNumLookupRule = kualiConfigurationService.getApplicationRule(VendorRuleConstants.PURAP_ADMIN_GROUP, 
                    VendorRuleConstants.PURAP_VNDR_MIN_NUM_LOOKUP_CRITERIA);
            VNDR_MIN_NUM_LOOKUP_CRITERIA = minNumLookupRule.getRuleText();
        }
        if (criteria < Integer.parseInt(VNDR_MIN_NUM_LOOKUP_CRITERIA)) {
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NUMBER, 
                    VendorKeyConstants.ERROR_VENDOR_LOOKUP_FEWER_THAN_MIN_CRITERIA, VNDR_MIN_NUM_LOOKUP_CRITERIA);
        }
    }
    
    /**
     * This method validates that if there is a Vendor Type Code selected, either a Name must be filled in, or a State must be
     * selected.
     * 
     * @param fieldValues A Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateTypeNameState(Map fieldValues) {
        String typeCd = (String) fieldValues.get(VendorPropertyConstants.VENDOR_TYPE_CODE);
        String vendorName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_NAME);
        String vendorFirstName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_FIRST_NAME);
        String vendorLastName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_LAST_NAME);
        String vendorStateForLookup = (String) fieldValues.get(VendorPropertyConstants.VENDOR_ADDRESS_STATE_CODE);

        if (StringUtils.isNotBlank(typeCd)) {
            if (StringUtils.isBlank(vendorStateForLookup)) {
                if (StringUtils.isBlank(vendorName) && 
                    StringUtils.isBlank(vendorFirstName) && 
                    StringUtils.isBlank(vendorLastName)) {
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NAME, 
                            VendorKeyConstants.ERROR_VENDOR_LOOKUP_TYPE_NO_NAME_OR_STATE, VendorPropertyConstants.VENDOR_NAME);
                }
            }
        }
    }
    
    /**
     * This method makes sure that if an active indicator status is chosen, then a Name must be filled in.
     * 
     * @param fieldValues A Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateStatusName(Map fieldValues) {
        String status = (String) fieldValues.get(VendorPropertyConstants.DATA_OBJ_MAINT_CD_ACTIVE_IND);
        String vendorName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_NAME);
        String vendorFirstName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_FIRST_NAME);
        String vendorLastName = (String) fieldValues.get(VendorPropertyConstants.VENDOR_LAST_NAME);

        if (StringUtils.isNotBlank(status)) {
            if (StringUtils.isBlank(vendorName) && 
                StringUtils.isBlank(vendorFirstName) && 
                StringUtils.isBlank(vendorLastName)) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_NAME, 
                        VendorKeyConstants.ERROR_VENDOR_LOOKUP_STATUS_NO_NAME);
            }
        }
    }
    
    /**
     * This method ensures that if a State is selected, a Vendor Type Code must be selected.
     * 
     * @param fieldValues A Map containing only those key-value pairs that have been filled in on the lookup
     */
    private void validateStateType(Map fieldValues) {
        String vendorStateForLookup = (String) fieldValues.get(VendorPropertyConstants.VENDOR_ADDRESS_STATE_CODE);
        String typeCd = (String) fieldValues.get(VendorPropertyConstants.VENDOR_TYPE_CODE);

        if (StringUtils.isNotBlank(vendorStateForLookup)) {
            if (StringUtils.isBlank(typeCd)) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_TYPE_CODE, 
                        VendorKeyConstants.ERROR_VENDOR_LOOKUP_STATE_NO_TYPE);
            }
        }
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
}

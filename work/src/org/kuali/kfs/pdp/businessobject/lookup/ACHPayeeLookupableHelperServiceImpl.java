/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.pdp.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.lookup.AbstractPayeeLookupableHelperServiceImpl;
import org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.ACHPayee;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Payee lookupable for PDP Payee ACH. Builds off of DV Payee lookup by taking off payment reason code, and adding adding entity id
 * to search and return url
 */
public class ACHPayeeLookupableHelperServiceImpl extends AbstractPayeeLookupableHelperServiceImpl {

    /**
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     * 
     * KRAD Conversion: Lookupable performs customization of the search results and performs a sort
     * by retrieving the default sort columns using data dictionary service..
     * 
     * Uses data dictionary.
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<DisbursementPayee> searchResults = new ArrayList<DisbursementPayee>();

        String payeeTypeCode = fieldValues.get(KFSPropertyConstants.PAYEE_TYPE_CODE);
        if (StringUtils.isBlank(payeeTypeCode)) {
            GlobalVariables.getMessageMap().putInfo(KFSPropertyConstants.PAYEE_TYPE_CODE, PdpKeyConstants.MESSAGE_PDP_ACH_PAYEE_LOOKUP_NO_PAYEE_TYPE);
        }
        
        if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER)) || StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.VENDOR_NAME)) || (StringUtils.isNotBlank(payeeTypeCode) && PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(payeeTypeCode))) {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
        }
        else if (StringUtils.isNotBlank(fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID)) || StringUtils.isNotBlank(fieldValues.get(KIMPropertyConstants.Person.ENTITY_ID)) || (StringUtils.isNotBlank(payeeTypeCode) && (PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(payeeTypeCode) || PdpConstants.PayeeIdTypeCodes.ENTITY.equals(payeeTypeCode)))) {
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        }
        else {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        }

        CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return results;
    }


    /**
     * Override to set entity id as the payee id and set the pdp payee type
     * 
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getPayeeFromPerson(org.kuali.rice.kim.api.identity.Person,
     *      java.util.Map)
     */
    @Override
    protected DisbursementPayee getPayeeFromPerson(Person personDetail, Map<String, String> fieldValues) {
        DisbursementPayee payee = super.getPayeeFromPerson(personDetail, fieldValues);

        String payeeTypeCode = (String) fieldValues.get(KFSPropertyConstants.PAYEE_TYPE_CODE);

        ACHPayee achPayee = new ACHPayee();

        if (PdpConstants.PayeeIdTypeCodes.ENTITY.equals(payeeTypeCode)) {
            achPayee.setPayeeIdNumber(personDetail.getEntityId());
            achPayee.setPayeeTypeCode(PdpConstants.PayeeIdTypeCodes.ENTITY);
        }
        else {
            achPayee.setPayeeIdNumber(personDetail.getEmployeeId());
            achPayee.setPayeeTypeCode(PdpConstants.PayeeIdTypeCodes.EMPLOYEE);
        }
        
        achPayee.setPayeeName(payee.getPayeeName());
        achPayee.setPrincipalId(payee.getPrincipalId());
        achPayee.setTaxNumber(payee.getTaxNumber());
        achPayee.setAddress(payee.getAddress());
        achPayee.setActive(payee.isActive());

        return achPayee;
    }


    /**
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getPayeeFromVendor(org.kuali.kfs.vnd.businessobject.VendorDetail,
     *      java.util.Map)
     */
    @Override
    protected DisbursementPayee getPayeeFromVendor(VendorDetail vendorDetail, Map<String, String> fieldValues) {
        DisbursementPayee payee = super.getPayeeFromVendor(vendorDetail, fieldValues);

        ACHPayee achPayee = new ACHPayee();

        achPayee.setPayeeIdNumber(payee.getPayeeIdNumber());
        achPayee.setPayeeTypeCode(PdpConstants.PayeeIdTypeCodes.VENDOR_ID);
        achPayee.setPayeeName(payee.getPayeeName());
        achPayee.setPrincipalId(payee.getPrincipalId());
        achPayee.setTaxNumber(payee.getTaxNumber());
        achPayee.setAddress(payee.getAddress());
        achPayee.setActive(payee.isActive());

        return achPayee;
    }


    /**
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        String vendorName = (String) fieldValues.get(KFSPropertyConstants.VENDOR_NAME);
        String vendorNumber = (String) fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER);
        String employeeId = (String) fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID);
        String entityId = (String) fieldValues.get(KIMPropertyConstants.Person.ENTITY_ID);
        String payeeTypeCode = (String) fieldValues.get(KFSPropertyConstants.PAYEE_TYPE_CODE);

        // only can use the vendor name and vendor number fields or the employee id field, but not both.
        boolean isVendorInfoEntered = StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(vendorNumber);
        if (StringUtils.isNotBlank(entityId) && isVendorInfoEntered) {
            String messageKey = KFSKeyConstants.ERROR_DV_VENDOR_EMPLOYEE_CONFUSION;

            String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            String vendorNumberLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NUMBER);
            String entityIdLabel = this.getAttributeLabel(KIMPropertyConstants.Person.ENTITY_ID);

            GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.ENTITY_ID, messageKey, entityIdLabel, vendorNameLabel, vendorNumberLabel);
        }

        boolean isEmployeeInfoEntered = StringUtils.isNotBlank(employeeId) || StringUtils.isNotBlank(entityId);
        boolean payeeTypeEntered = StringUtils.isNotBlank(payeeTypeCode);

        if (payeeTypeEntered && PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(payeeTypeCode) && isEmployeeInfoEntered) {
            String messageKey = PdpKeyConstants.ERROR_PAYEE_LOOKUP_VENDOR_EMPLOYEE_CONFUSION;

            String employeeIdLabel = this.getAttributeLabel(KIMPropertyConstants.Person.EMPLOYEE_ID);
            String entityIdLabel = this.getAttributeLabel(KIMPropertyConstants.Person.ENTITY_ID);
            String payeeTypeLabel = this.getAttributeLabel(KFSPropertyConstants.PAYEE_TYPE_CODE);

            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_TYPE_CODE, messageKey, payeeTypeLabel, payeeTypeCode, employeeIdLabel, entityIdLabel);
        }
        else if (payeeTypeEntered && (PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(payeeTypeCode) || PdpConstants.PayeeIdTypeCodes.ENTITY.equals(payeeTypeCode)) && isVendorInfoEntered) {
            String messageKey = PdpKeyConstants.ERROR_PAYEE_LOOKUP_VENDOR_EMPLOYEE_CONFUSION;

            String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            String vendorNumberLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NUMBER);
            String payeeTypeLabel = this.getAttributeLabel(KFSPropertyConstants.PAYEE_TYPE_CODE);

            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_TYPE_CODE, messageKey, payeeTypeLabel, payeeTypeCode, vendorNameLabel, vendorNumberLabel);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    /**
     * Override to not filter rows based on payment reason
     * 
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#filterReturnUrl(java.util.List,
     *      java.util.List, java.lang.String)
     *      
     * KRAD Conversion: Performs customization of the result list of rows.
     *      
     * No use of data dictionary
     */
    @Override
    protected void filterReturnUrl(List<ResultRow> resultRowList, List<DisbursementPayee> payeeList, String paymentReasonCode) {
    }

}

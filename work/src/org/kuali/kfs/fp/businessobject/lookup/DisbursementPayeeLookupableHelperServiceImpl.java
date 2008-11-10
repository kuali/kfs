/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.fp.businessobject.lookup;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherRuleConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;

public class DisbursementPayeeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static Logger LOG = Logger.getLogger(DisbursementPayeeLookupableHelperServiceImpl.class);
    
    Lookupable vendorLookupable;
    Lookupable kualiLookupable;
    
    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return this.getDummyPayees();
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm,
     *      java.util.Collection, boolean)
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) { 
        Collection<BusinessObject> displayList = new ArrayList<BusinessObject>();
        List<ResultRow> resultRowList = (List<ResultRow>)resultTable;
        
        Map<String, String> fieldValues = lookupForm.getFieldsForLookup();
        String payeeTypeCode = fieldValues.remove(KFSPropertyConstants.PAYEE_TYPE_CODE);        
        
        // perform employee search
        if(StringUtils.equals(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE, payeeTypeCode)) {
            lookupForm.setFieldsForLookup(this.getPersonFieldValues(fieldValues));
            displayList = this.performLookupPersons(lookupForm, resultRowList, bounded);
            
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(KFSPropertyConstants.PAYEE_TYPE_CODE, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE);
            this.appendAdditionalParameters(resultRowList, parameters);
        }

        // perform vendor search
        if (StringUtils.equals(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR, payeeTypeCode)) {
            lookupForm.setFieldsForLookup(this.getVendorFieldValues(fieldValues));
            displayList = this.performLookupVendors(lookupForm, resultRowList, bounded);
            
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(KFSPropertyConstants.PAYEE_TYPE_CODE, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR);
            this.appendAdditionalParameters(resultRowList, parameters);
        }

        return displayList;
    }
    
    // convert the field values to contain person fields
    private Map<String, String> getPersonFieldValues(Map<String, String> fieldValues){
        Map<String, String> personFieldValues = new HashMap<String, String>();
        personFieldValues.putAll(fieldValues);
        
        Map<String, String> fieldConversionMap = DisbursementPayee.getFieldConversionBetweenPayeeAndPerson();
        this.replaceFieldKeys(personFieldValues, fieldConversionMap);
        
        return personFieldValues;
    }
    
    // convert the field values to contain vendor fields
    private Map<String, String> getVendorFieldValues(Map<String, String> fieldValues){
        Map<String, String> vendorFieldValues = new HashMap<String, String>();
        vendorFieldValues.putAll(fieldValues);
        
        Map<String, String> fieldConversionMap = DisbursementPayee.getFieldConversionBetweenPayeeAndVendor();
        this.replaceFieldKeys(vendorFieldValues, fieldConversionMap);
        
        return vendorFieldValues;
    }
    
    // replace the keys in fieldValues with the corresponding values defined in fieldConversionMap
    private void replaceFieldKeys(Map<String, String> fieldValues, Map<String, String> fieldConversionMap) {
        for(String key : fieldConversionMap.keySet()) {
            if(fieldValues.containsKey(key)) {
                String value = fieldValues.get(key);
                String newKey = fieldConversionMap.get(key);
                
                fieldValues.remove(key);
                fieldValues.put(newKey, value);
            }
        }
    }

    // build a dummy payee object
    private List<? extends BusinessObject> getDummyPayees() {
        List<BusinessObject> searchResults = new ArrayList<BusinessObject>();

        DisbursementPayee disbursementPayee = new DisbursementPayee();
        disbursementPayee.setPayeeTypeCode(StringUtils.EMPTY);
        disbursementPayee.setPayeeIdNumber(StringUtils.EMPTY);
        disbursementPayee.setPayeeName(StringUtils.EMPTY);

        searchResults.add(disbursementPayee);
        return new CollectionIncomplete(searchResults, new Long(0));
    }

    // perform vendor search 
    private Collection<BusinessObject> performLookupVendors(LookupForm lookupForm, List<ResultRow> resultRowList, boolean bounded) {        
        Map<String, String> fieldConversions = this.getVendorFieldValues(lookupForm.getFieldConversions());
        lookupForm.setFieldConversions(fieldConversions);
               
        vendorLookupable.setFieldConversions(fieldConversions);
        vendorLookupable.setBusinessObjectClass(VendorDetail.class);
        
        vendorLookupable.validateSearchParameters(lookupForm.getFieldsForLookup());
        return vendorLookupable.performLookup(lookupForm, resultRowList, bounded);
    }
    
    // perform person search 
    private Collection<BusinessObject> performLookupPersons(LookupForm lookupForm, List<ResultRow> resultRowList, boolean bounded) {
        Map<String, String> fieldConversions = this.getPersonFieldValues(lookupForm.getFieldConversions());
        lookupForm.setFieldConversions(fieldConversions);
                
        kualiLookupable.setFieldConversions(fieldConversions);
        kualiLookupable.setBusinessObjectClass(Person.class);
        
        return kualiLookupable.performLookup(lookupForm, resultRowList, bounded);
    }
    
    // append the additional parameters to the return URLs
    private void appendAdditionalParameters(List<ResultRow> resultRowList, Map<String, String> parameters) {
        String additionalParameterString = this.getAdditionalParameterString(parameters);
        for(ResultRow row : resultRowList) {
            String href = StringUtils.substringBetween(row.getReturnUrl(), "href=\"", "\"");
            String returnUrl = StringUtils.replace(row.getReturnUrl(), href, href + additionalParameterString);
            row.setReturnUrl(returnUrl);
        }       
    }
    
    // produce the http query string from the given parameters
    private String getAdditionalParameterString(Map<String, String> parameters) {
        String parameterAsStringPattern = "&{0}={1}";
        StringBuilder additionalParameterString = new StringBuilder(StringUtils.EMPTY);
        
        for(String key : parameters.keySet()) {
            additionalParameterString.append(MessageFormat.format(parameterAsStringPattern, key, parameters.get(key)));
        }
        
        return additionalParameterString.toString();        
    }

    /**
     * Sets the vendorLookupable attribute value.
     * 
     * @param vendorLookupable The vendorLookupable to set.
     */
    public void setVendorLookupable(Lookupable vendorLookupable) {
        this.vendorLookupable = vendorLookupable;
    }

    /**
     * Sets the kualiLookupable attribute value.
     * @param kualiLookupable The kualiLookupable to set.
     */
    public void setKualiLookupable(Lookupable kualiLookupable) {
        this.kualiLookupable = kualiLookupable;
    }
}

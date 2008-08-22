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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

public class CustomerOpenItemReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImpl.class);
    private Map fieldConversions;  
    private DataDictionaryService dataDictionaryService;
    
    /**
     * Get the search results that meet the input search criteria.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        LOG.debug("\n\n\n\n ***********************    getSearchResults() started\n");

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // create some fake entries to test with
        CustomerOpenItemReportDetail detail1 = new CustomerOpenItemReportDetail();
        detail1.setDocumentType("INV");
        detail1.setDocumentNumber("01 BM3002707");
        detail1.setDocumentDescription("BATMOBILE");
        detail1.setDueApprovedDate(Date.valueOf("08/06/2006"));
        detail1.setDocumentPaymentAmount(KualiDecimal.ZERO);
        detail1.setUnpaidUnappliedAmount(KualiDecimal.ZERO);
        
        CustomerOpenItemReportDetail detail2 = new CustomerOpenItemReportDetail();
        detail2.setDocumentType("INV");
        detail2.setDocumentNumber("01 BM3002708");
        detail2.setDocumentDescription("BATCAVE MORTGAGE");
        detail2.setDueApprovedDate(Date.valueOf("08/06/2006"));
        detail2.setDocumentPaymentAmount(KualiDecimal.ZERO);
        detail2.setUnpaidUnappliedAmount(KualiDecimal.ZERO);
        
        List results = new ArrayList();
        results.add(detail1);
        results.add(detail2);

        LOG.info("\t\t sending results back... \n\n\n");
        return new CollectionIncomplete(results, new Long(results.size()));
    }    

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    public List getReturnKeys() {
        List returnKeys;
        returnKeys = new ArrayList(fieldConversions.keySet());
        LOG.info("\n\n\t\t THIS OVERRIDE IS WORKING (GETRETURNKEYS)... \n\n\n");

        return returnKeys;
    }
    
    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    protected Properties getParameters(BusinessObject bo, Map fieldConversions, String lookupImpl) {
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KNSConstants.DOC_FORM_KEY, getDocFormKey());
        parameters.put(KNSConstants.REFRESH_CALLER, lookupImpl);
        if (getReferencesToRefresh() != null) {
            parameters.put(KNSConstants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }
        
        String encryptedList = "";

        Iterator returnKeys = getReturnKeys().iterator();
        while (returnKeys.hasNext()) {
            String fieldNm = (String) returnKeys.next();

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = KNSConstants.EMPTY_STRING;
            }

            // Encrypt value if it is a secure field
            String displayWorkgroup = dataDictionaryService.getAttributeDisplayWorkgroup(bo.getClass(), fieldNm);

            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            if (StringUtils.isNotBlank(displayWorkgroup) && !GlobalVariables.getUserSession().getUniversalUser().isMember( displayWorkgroup )) {
//                try {
//                    fieldVal = encryptionService.encrypt(fieldVal);
//                }
//                catch (GeneralSecurityException e) {
//                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
//                    throw new RuntimeException(e);
//                }

                // add to parameter list so that KualiInquiryAction can identify which parameters are encrypted
                if (encryptedList.equals("")) {
                    encryptedList = fieldNm;
                }
                else {
                    encryptedList = encryptedList + KNSConstants.FIELD_CONVERSIONS_SEPERATOR + fieldNm;
                }
            }
            
            //need to format date in url
            if (fieldVal instanceof Date) {
                DateFormatter dateFormatter = new DateFormatter();
                fieldVal = dateFormatter.format(fieldVal);
            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        // if we did encrypt a value (or values), add the list of those that are encrypted to the parameters
        if (!encryptedList.equals("")) {
            parameters.put(KNSConstants.ENCRYPTED_LIST_PREFIX, encryptedList);
        }

        return parameters;
    }    
    
    /**
     * 
     * This method performs the lookup and returns a collection of lookup items
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection displayList;
        LOG.info("\n\n\t\t THIS OVERRIDE IS WORKING (performLookup)... \n\n\n");
        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }
// MJM get resultTable populated here
        
        HashMap<String,Class> propertyTypes = new HashMap<String, Class>(); 
        
        boolean hasReturnableRow = false;
        
        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

           // String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName());
           // String actionUrls = getActionUrls(element);
            String returnUrl = "www.bigfrickenRETURNurl";
            String actionUrls = "www.someACTIONurl";
            
            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                
                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                
                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(col.getPropertyName());
//                if ( propClass == null ) {
//                    try {
//                        propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
//                        propertyTypes.put( col.getPropertyName(), propClass );
//                    } catch (Exception e) {
//                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
//                    }
//                }

                // formatters
                if (prop != null) {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean) {
                        formatter = new BooleanFormatter();
                    }
                    
                    // for Dates, always use DateFormatter
                    if (prop instanceof Date) {
                        formatter = new DateFormatter();
                    }

                    // for collection, use the list formatter if a formatter hasn't been defined yet
                    if (prop instanceof Collection && formatter == null) {
                    formatter = new CollectionFormatter();
                    }
                    
                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    }
                    else {
                        propValue = prop.toString();
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
                
                // check security on field and do masking if necessary
                boolean viewAuthorized = getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), element.getClass().getName(), col.getPropertyName());
                if (!viewAuthorized) {
                    Mask displayMask = getDataDictionaryService().getAttributeDisplayMask(element.getClass().getName(), col.getPropertyName());
                    propValue = displayMask.maskValue(propValue);
                }
                col.setPropertyValue(propValue);


                if (StringUtils.isNotBlank(propValue)) {
                    col.setPropertyURL(getInquiryUrl(element, col.getPropertyName()));
                }
            }

            ResultRow row = new ResultRow(columns, returnUrl, "", actionUrls);
            if ( element instanceof PersistableBusinessObject ) {
                row.setObjectId(((PersistableBusinessObject)element).getObjectId());
            }
            
            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

}

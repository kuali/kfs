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

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
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

public class CustomerAgingReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImpl.class);
    private EncryptionService encryptionService;
    private DataDictionaryService dataDictionaryService;
    private Map fieldConversions;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
    
    
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
        // MJM check what fieldValues returns; what is the propertyName for Account Number?

        Date today = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        Date reportDate = today;
        Date reportDateCutoff1; // MJM need to add 4 more cutoff dates based on fieldValues.get...
        
        LOG.debug("\n\n\n\n \t\t\t\t***********************    customerInvoiceDocumentService should not be null \n\n");

       // List invoices = (List) customerInvoiceDocumentService.getAllCustomerInvoiceDocuments();
        Collection<CustomerInvoiceDocument> invoices = customerInvoiceDocumentService.getAllCustomerInvoiceDocuments();
        CustomerAgingReportDetail testcustomer1 = new CustomerAgingReportDetail();
       
        Map<String,Object> knownCustomers = new HashMap<String,Object>(invoices.size());
      // EXAMPLE: fieldNamesValuesForParameter.put("parameterNamespaceCode",CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_CODE);       
       
        KualiDecimal totalbalance = new KualiDecimal(0.00);
        CustomerAgingReportDetail custDetail = null;
        
        // iterate over all invoices consolidating balances for each customer
        for (CustomerInvoiceDocument cid : invoices) {
           
            Date approvalDate = cid.getCustomerPurchaseOrderDate();  
        
           
            if (knownCustomers.containsKey(cid.getCustomer().getCustomerNumber()) && approvalDate.before(reportDate)) {
                // not a new customer id and invoice approvalDate is valid
                custDetail = (CustomerAgingReportDetail)knownCustomers.get(cid.getCustomer().getCustomerNumber());
            }
            else { //if (approvalDate.before(reportDate)) {
                // new customer id, so create a new CustomerAgingReportDetail
                custDetail = new CustomerAgingReportDetail();
                custDetail.setCustomerName(cid.getCustomer().getCustomerName());
                custDetail.setCustomerNumber(cid.getCustomer().getCustomerNumber());
                // if (approvalDate.
                custDetail.setUnpaidBalance0to30(cid.getTotalDollarAmount());
                knownCustomers.put(cid.getCustomer().getCustomerNumber(), custDetail);
            }
              
             
       

            if (LOG.isInfoEnabled()) {
                LOG.info("\t\tcustDetail=\t" + custDetail.getCustomerNumber() + "\t"+custDetail.getUnpaidBalance0to30());
            }
        }
           


            if (LOG.isInfoEnabled()) {
           //     LOG.info("CustomerInvoiceDocument cidgetTotalDollarAmount=" + cid.getTotalDollarAmount() + "\t\tCustomerName=" + cid.getCustomer().getCustomerNumber());
            }

  
        // create some fake entries to test with
        CustomerAgingReportDetail matt = new CustomerAgingReportDetail();
        matt.setCustomerName("Matt");
        matt.setCustomerNumber("DEV12345");
        matt.setUnpaidBalance0to30(KualiDecimal.ZERO);
        matt.setUnpaidBalance31to60(KualiDecimal.ZERO);
        matt.setUnpaidBalance61to90(KualiDecimal.ZERO);
        matt.setUnpaidBalance91toSYSPR(KualiDecimal.ZERO);
        matt.setUnpaidBalanceSYSPRplus1orMore(KualiDecimal.ZERO);
        
        CustomerAgingReportDetail justin = new CustomerAgingReportDetail();
        justin.setCustomerName("Justin");
        justin.setCustomerNumber("LED12346");
        justin.setUnpaidBalance0to30(KualiDecimal.ZERO);
        justin.setUnpaidBalance31to60(KualiDecimal.ZERO);
        justin.setUnpaidBalance61to90(KualiDecimal.ZERO);
        justin.setUnpaidBalance91toSYSPR(KualiDecimal.ZERO);
        justin.setUnpaidBalanceSYSPRplus1orMore(KualiDecimal.ZERO);
        
        CustomerAgingReportDetail patty = new CustomerAgingReportDetail();
        patty.setCustomerName("Patty");
        patty.setCustomerNumber("MGR12347");
        patty.setUnpaidBalance0to30(KualiDecimal.ZERO);
        patty.setUnpaidBalance31to60(KualiDecimal.ZERO);
        patty.setUnpaidBalance61to90(KualiDecimal.ZERO);
        patty.setUnpaidBalance91toSYSPR(KualiDecimal.ZERO);
        patty.setUnpaidBalanceSYSPRplus1orMore(KualiDecimal.ZERO);
        
        // List results = accountBalanceService.findAccountBalanceByConsolidation(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);
        // List<CustomerAgingReportDetail> results = new ArrayList<CustomerAgingReportDetail>(3);
        List results = new ArrayList();
        results.add(matt);
        results.add(justin);
        results.add(patty);
        for (Object detail : knownCustomers.values()) {
            results.add(detail);
        }   
  
        
        LOG.info("\t\t sending results back... \n\n\n");
        return new CollectionIncomplete(results, new Long(results.size()));
    }    

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    public List getReturnKeys() {
        List returnKeys;
//        if (fieldConversions != null && !fieldConversions.isEmpty()) {
            returnKeys = new ArrayList(fieldConversions.keySet());
            LOG.info("\n\n\t\t THIS OVERRIDE IS WORKING (GETRETURNKEYS)... \n\n\n");
//        }
//        else {
//            returnKeys = getPersistenceStructureService().listPrimaryKeyFieldNames(getBusinessObjectClass());
//        }
//
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

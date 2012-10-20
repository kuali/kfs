/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.document.service.CustomerOpenItemReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerOpenItemReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerOpenItemReportLookupableHelperServiceImpl.class);
    private Map fieldConversions;
    private DataDictionaryService dataDictionaryService;

    /**
     * Get the search results that meet the input search criteria.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List results;
        LOG.debug("\n\n\n\n ***********************    getSearchResults() started\n");

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        /*
        String customerNumber = ((String[]) getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER))[0];
        List results = SpringContext.getBean(CustomerOpenItemReportService.class).getPopulatedReportDetails(customerNumber);
        */
        String reportName =((String[]) getParameters().get(KFSConstants.CustomerOpenItemReport.REPORT_NAME))[0];
        if  (StringUtils.equals(reportName, KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME)) {
            String customerNumber = ((String[]) getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER))[0];
            results = SpringContext.getBean(CustomerOpenItemReportService.class).getPopulatedReportDetails(customerNumber);
        } else if (StringUtils.equals(reportName, KFSConstants.CustomerOpenItemReport.UNPAID_UNAPPLIED_AMOUNT_REPORT)){
            String customerNumber = ((String[]) getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER))[0];
            String documentNumber = ((String[]) getParameters().get(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER))[0];
            results = SpringContext.getBean(CustomerOpenItemReportService.class).getPopulatedUnpaidUnappliedAmountReportDetails(customerNumber, documentNumber);
        } else {
            results = SpringContext.getBean(CustomerOpenItemReportService.class).getPopulatedReportDetails(getParameters());
        }
        LOG.info("\t\t sending results back... \n\n\n");
        return new CollectionIncomplete(results, new Long(results.size()));
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    /*
    @Override
    public List getReturnKeys() {
        List returnKeys;
        returnKeys = new ArrayList(fieldConversions.keySet());
        LOG.info("\n\n\t\t THIS OVERRIDE IS WORKING (GETRETURNKEYS)... \n\n\n");

        return returnKeys;
    }
    */

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    protected Properties getParameters(BusinessObject bo, Map fieldConversions, String lookupImpl, List pkNames) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KRADConstants.DOC_FORM_KEY, getDocFormKey());
        parameters.put(KRADConstants.REFRESH_CALLER, lookupImpl);
        if (getReferencesToRefresh() != null) {
            parameters.put(KRADConstants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }

        String encryptedList = "";

        Iterator returnKeys = getReturnKeys().iterator();
        while (returnKeys.hasNext()) {
            String fieldNm = (String) returnKeys.next();

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = KRADConstants.EMPTY_STRING;
            }

            // Encrypt value if it is a secure field
            // TODO: revisit authorization checks in this class

            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            // need to format date in url
            if (fieldVal instanceof Date) {
                DateFormatter dateFormatter = new DateFormatter();
                fieldVal = dateFormatter.format(fieldVal);
            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        return parameters;
    }

    /**
     * This method performs the lookup and returns a collection of lookup items
     * 
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     * 
     * KRAD Conversion: Performs the conditional formatting of the columns in the 
     * display results set.  Also sets customized property urls for the columns. 
     * 
     * Data dictionary is used to retrieve properties of the fields.
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

        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;
        
        String customerNumber = ((String[]) getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER))[0];
        String customerName = ((String[]) getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NAME))[0];
        Collection<String> refDocumentNumbers = SpringContext.getBean(CustomerOpenItemReportService.class).getDocumentNumbersOfReferenceReports(customerNumber);

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            // String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(),
            // lookupForm.getLookupableImplServiceName());
            // String actionUrls = getActionUrls(element);
            String returnUrl = "www.bigfrickenRETURNurl";
            String actionUrls = "www.someACTIONurl";
            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();
                
                // pick off result column from result list, do formatting
                String propValue = KRADConstants.EMPTY_STRING;
                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                // formatters
                if (prop != null) {
                    // for Dates, always use DateFormatter
                    if (prop instanceof Date)
                        formatter = new DateFormatter();

                    if (formatter != null)
                        propValue = (String) formatter.format(prop);
                    else
                        propValue = prop.toString();
                }

                // comparator
                Class propClass = propertyTypes.get(col.getPropertyName());
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
                    if (StringUtils.equals(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER, col.getPropertyName())) {
                        String propertyURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + propValue + "&command=displayDocSearchView";
                        col.setPropertyURL(propertyURL);
                    } else if (StringUtils.equals(KFSConstants.CustomerOpenItemReport.UNPAID_UNAPPLIED_AMOUNT, col.getPropertyName())){
                        String documentNumber = ObjectUtils.getPropertyValue(element, KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER).toString();
                        if (refDocumentNumbers.contains(documentNumber)){
                            String href="arCustomerOpenItemReportLookup.do" +
                            "?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail" +
                            "&returnLocation=&lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable" +
                            "&methodToCall=search&customerNumber="+customerNumber+ 
                            "&reportName=" + KFSConstants.CustomerOpenItemReport.UNPAID_UNAPPLIED_AMOUNT_REPORT +
                            "&customerName=" + customerName +
                            //"&customerName=" +customer.getCustomerName()+
                            "&documentNumber=" + documentNumber +
                            "&reportName=Unpaid / Unapplied Amount Report&docFormKey=88888888";
                            col.setPropertyURL(href);
                        } else col.setPropertyURL(""); 
                    }

                    else col.setPropertyURL("");
                }
                
            }
            
            
            
            ResultRow row = new ResultRow(columns, returnUrl, actionUrls);
            if (element instanceof PersistableBusinessObject)
                row.setObjectId(((PersistableBusinessObject) element).getObjectId());

            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable)
                hasReturnableRow = true;

            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

}


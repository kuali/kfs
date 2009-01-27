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
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

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
    public List getSearchResults(Map fieldValues) {
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
            // TODO: revisit authorization checks in this class
            // String displayWorkgroup = dataDictionaryService.getAttributeDisplayWorkgroup(bo.getClass(), fieldNm);

            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            // if (StringUtils.isNotBlank(displayWorkgroup) && !SpringContext.getBean(IdentityManagementService.class).isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, displayWorkgroup)) {}

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
                String propValue = KNSConstants.EMPTY_STRING;
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
                        String propertyURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + propValue + "&command=displayDocSearchView";
                        col.setPropertyURL(propertyURL);
                    } else col.setPropertyURL("");
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


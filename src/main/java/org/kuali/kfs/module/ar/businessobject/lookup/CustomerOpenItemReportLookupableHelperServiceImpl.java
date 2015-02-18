/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.module.ar.document.service.CustomerOpenItemReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class CustomerOpenItemReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerOpenItemReportLookupableHelperServiceImpl.class);
    protected CustomerOpenItemReportService customerOpenItemReportService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

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

        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String reportName =getParameters().get(KFSConstants.CustomerOpenItemReport.REPORT_NAME)[0];
        if  (StringUtils.equals(reportName, KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME)) {
            String customerNumber = getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER)[0];
            results = getCustomerOpenItemReportService().getPopulatedReportDetails(customerNumber);
        } else if (StringUtils.equals(reportName, KFSConstants.CustomerOpenItemReport.UNPAID_UNAPPLIED_AMOUNT_REPORT)){
            String customerNumber = getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER)[0];
            String documentNumber = getParameters().get(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER)[0];
            results = getCustomerOpenItemReportService().getPopulatedUnpaidUnappliedAmountReportDetails(customerNumber, documentNumber);
        } else {
            results = getCustomerOpenItemReportService().getPopulatedReportDetails(getParameters());
        }
        LOG.info("\t\t sending results back... \n\n\n");
        return new CollectionIncomplete(results, new Long(results.size()));
    }

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
        Collection displayList = getSearchResults(lookupForm.getFieldsForLookup());

        // MJM get resultTable populated here

        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        String customerNumber = getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER)[0];
        String customerName = getParameters().get(KFSConstants.CustomerOpenItemReport.CUSTOMER_NAME)[0];
        Collection<String> refDocumentNumbers = getCustomerOpenItemReportService().getDocumentNumbersOfReferenceReports(customerNumber);

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            String returnUrl = "";
            String actionUrls = "";
            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KRADConstants.EMPTY_STRING;
                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                // formatters
                if (prop != null) {
                    propValue = getContractsGrantsReportHelperService().formatByType(prop, formatter);
                }

                // comparator
                Class propClass = propertyTypes.get(col.getPropertyName());
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
                    if (StringUtils.equals(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER, col.getPropertyName())) {
                        String baseUrl = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/" + KFSConstants.DOC_HANDLER_ACTION;
                        Properties parameters = new Properties();
                        parameters.put(KFSConstants.PARAMETER_DOC_ID, propValue);
                        parameters.put(KFSConstants.PARAMETER_COMMAND, KFSConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);

                        String propertyURL = UrlFactory.parameterizeUrl(baseUrl, parameters);
                        col.setPropertyURL(propertyURL);
                    } else if (StringUtils.equals(KFSConstants.CustomerOpenItemReport.UNPAID_UNAPPLIED_AMOUNT, col.getPropertyName())){
                        String documentNumber = ObjectUtils.getPropertyValue(element, KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER).toString();
                        if (refDocumentNumbers.contains(documentNumber)){
                            Properties params = new Properties();
                            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CustomerOpenItemReportDetail.class.getName());
                            params.put(KFSConstants.RETURN_LOCATION_PARAMETER, StringUtils.EMPTY);
                            params.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, ArConstants.CUSTOMER_OPEN_ITEM_REPORT_LOOKUPABLE_IMPL);
                            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
                            params.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customerNumber);
                            params.put(KFSConstants.CustomerOpenItemReport.REPORT_NAME, KFSConstants.CustomerOpenItemReport.UNPAID_UNAPPLIED_AMOUNT_REPORT);
                            params.put(ArPropertyConstants.CustomerFields.CUSTOMER_NAME, customerName);
                            params.put(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER, documentNumber);
                            params.put(KFSConstants.DOC_FORM_KEY, "88888888");
                            String href = UrlFactory.parameterizeUrl(ArConstants.UrlActions.CUSTOMER_OPEN_ITEM_REPORT_LOOKUP, params);
                            col.setPropertyURL(href);
                        }
                        else {
                            col.setPropertyURL("");
                        }
                    }
                    else {
                        col.setPropertyURL("");
                    }
                }

            }

            ResultRow row = new ResultRow(columns, returnUrl, actionUrls);
            if (element instanceof PersistableBusinessObject) {
                row.setObjectId(((PersistableBusinessObject) element).getObjectId());
            }

            boolean isRowReturnable = isResultReturnable(element);
            row.setRowReturnable(isRowReturnable);
            if (isRowReturnable) {
                hasReturnableRow = true;
            }

            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

    public CustomerOpenItemReportService getCustomerOpenItemReportService() {
        return customerOpenItemReportService;
    }

    public void setCustomerOpenItemReportService(CustomerOpenItemReportService customerOpenItemReportService) {
        this.customerOpenItemReportService = customerOpenItemReportService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}

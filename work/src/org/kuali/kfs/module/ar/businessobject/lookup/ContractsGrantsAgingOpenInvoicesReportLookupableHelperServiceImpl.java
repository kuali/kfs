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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsAgingOpenInvoicesReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsAgingOpenInvoicesReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
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

public class ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected ContractsGrantsAgingOpenInvoicesReportService contractsGrantsAgingOpenInvoicesReportService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Get the search results that meet the input search criteria.
     *
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        List results;
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        results = getContractsGrantsAgingOpenInvoicesReportService().getPopulatedReportDetails(getParameters());
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
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection displayList;
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
                        String propertyURL = contractsGrantsReportHelperService.getDocSearchUrl(propValue);
                        col.setPropertyURL(propertyURL);
                    }else if (StringUtils.equals("Actions", col.getColumnTitle())) {
                        col.setPropertyURL(getCollectionActivityDocumentUrl(element, col.getColumnTitle()));
                    }
                    else {
                        col.setPropertyURL("");
                    }
                }
            }

            ResultRow row = new ResultRow(columns, KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING);
            if (element instanceof PersistableBusinessObject) {
                row.setObjectId(((PersistableBusinessObject) element).getObjectId());
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


    /**
     * This method returns the Collection Activity create url
     *
     * @param bo business object
     * @param columnTitle
     * @return Returns the url for the Collection Activity creation
     */
    protected String getCollectionActivityDocumentUrl(BusinessObject bo, String columnTitle) {
        String lookupUrl = "";
        ContractsGrantsAgingOpenInvoicesReport detail = (ContractsGrantsAgingOpenInvoicesReport) bo;
        Properties parameters = new Properties();
        ContractsGrantsInvoiceDocument cgInvoice = getBusinessObjectService().findBySinglePrimaryKey(ContractsGrantsInvoiceDocument.class, detail.getDocumentNumber());

        String proposalNumber = cgInvoice.getInvoiceGeneralDetail().getProposalNumber().toString();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "docHandler");
        parameters.put("selectedProposalNumber", proposalNumber);
        parameters.put("selectedInvoiceDocumentNumber", detail.getDocumentNumber());
        parameters.put("command", "initiate");
        parameters.put("docTypeName", "COLA");
        lookupUrl = UrlFactory.parameterizeUrl("arCollectionActivityDocument.do", parameters);

        return lookupUrl;
    }

    public ContractsGrantsAgingOpenInvoicesReportService getContractsGrantsAgingOpenInvoicesReportService() {
        return contractsGrantsAgingOpenInvoicesReportService;
    }

    public void setContractsGrantsAgingOpenInvoicesReportService(ContractsGrantsAgingOpenInvoicesReportService contractsGrantsAgingOpenInvoicesReportService) {
        this.contractsGrantsAgingOpenInvoicesReportService = contractsGrantsAgingOpenInvoicesReportService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.report.service.CollectionActivityReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * LookupableHelperService class for Collection Activity Report.
 */
public class CollectionActivityReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected CollectionActivityReportService collectionActivityReportService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Get the search results that meet the input search criteria.
     *
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResultsUnbounded(Map fieldValues) {
        List<CollectionActivityReport> results = new ArrayList<CollectionActivityReport>();
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        results = collectionActivityReportService.filterEventsForCollectionActivity(fieldValues);
        return new CollectionIncomplete<CollectionActivityReport>(results, (long) results.size());
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
        Collection displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        // MJM get resultTable populated here
        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Object aDisplayList : displayList) {
            BusinessObject element = (BusinessObject) aDisplayList;

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            if (ObjectUtils.isNotNull(getColumns())) {
                List<Column> columns = getColumns();
                for (Object column : columns) {

                    Column col = (Column) column;
                    Formatter formatter = col.getFormatter();

                    // pick off result column from result list, do formatting
                    Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                    String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                    Class propClass = getPropertyClass(element, col.getPropertyName());

                    // formatters
                    if (ObjectUtils.isNotNull(prop)) {
                        propValue = getContractsGrantsReportHelperService().formatByType(prop, formatter);
                    }

                    // comparator
                    col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                    col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                    propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                    col.setPropertyValue(propValue);

                    // Add url when property is invoiceNumber
                    if (col.getPropertyName().equals(ArPropertyConstants.CollectionActivityReportFields.INVOICE_NUMBER)) {
                        String url = contractsGrantsReportHelperService.getDocSearchUrl(propValue);

                        Map<String, String> fieldList = new HashMap<String, String>();
                        fieldList.put(ArPropertyConstants.TicklersReportFields.INVOICE_NUMBER, propValue);
                        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                        col.setColumnAnchor(a);
                    }
                    else if (col.getPropertyName().equals(ArPropertyConstants.CollectionActivityReportFields.ACCOUNT_NUMBER)) {
                        CollectionActivityReport bo = (CollectionActivityReport) element;
                        String url = this.getAccountInquiryUrl(bo);
                        Map<String, String> fieldList = new HashMap<String, String>();
                        fieldList.put(KFSPropertyConstants.ACCOUNT_NUMBER, propValue);
                        fieldList.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bo.getChartOfAccountsCode());
                        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));
                        col.setColumnAnchor(a);
                    }
                    else if (org.apache.commons.lang.StringUtils.equals("Actions", col.getColumnTitle())) {

                        String url = this.getCollectionActivityDocumentUrl(element, col.getColumnTitle());
                        Map<String, String> fieldList = new HashMap<String, String>();
                        fieldList.put(KFSPropertyConstants.PROPOSAL_NUMBER, propValue);
                        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                        col.setColumnAnchor(a);
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
        }

        return displayList;
    }

    /**
     * Gets the account lookup url on given account number.
     *
     * @param accountNumber Account Number for inquiry on Account
     * @return Returns the url string.
     */
    protected String getAccountInquiryUrl(CollectionActivityReport bo) {
        Properties params = new Properties();
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Account.class.getName());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, KFSConstants.INQUIRY_ACTION);
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(ArPropertyConstants.CollectionActivityReportFields.ACCOUNT_NUMBER, bo.getAccountNumber());
        params.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bo.getChartOfAccountsCode());
        return UrlFactory.parameterizeUrl(KFSConstants.INQUIRY_ACTION, params);
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
        CollectionActivityReport detail = (CollectionActivityReport) bo;
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "docHandler");
        parameters.put(ArPropertyConstants.CollectionActivityDocumentFields.SELECTED_PROPOSAL_NUMBER, detail.getProposalNumber().toString());
        parameters.put(ArPropertyConstants.CollectionActivityDocumentFields.SELECTED_INVOICE_DOCUMENT_NUMBER, detail.getInvoiceNumber());
        parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, "COLA");
        lookupUrl = UrlFactory.parameterizeUrl("arCollectionActivityDocument.do", parameters);

        return lookupUrl;
    }

    public CollectionActivityReportService getCollectionActivityReportService() {
        return collectionActivityReportService;
    }

    public void setCollectionActivityReportService(CollectionActivityReportService collectionActivityReportService) {
        this.collectionActivityReportService = collectionActivityReportService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}

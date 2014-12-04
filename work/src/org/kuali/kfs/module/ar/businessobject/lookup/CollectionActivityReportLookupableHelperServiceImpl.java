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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.report.service.CollectionActivityReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
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

/**
 * LookupableHelperService class for Collection Activity Report.
 */
public class CollectionActivityReportLookupableHelperServiceImpl extends AccountsReceivableLookupableHelperServiceImplBase {
    protected CollectionActivityReportService collectionActivityReportService;
    protected ConfigurationService configurationService;
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

        boolean hasReturnableRow = false;

        Person user = GlobalVariables.getUserSession().getPerson();
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());

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
                    if (col.getPropertyName().equals(ArPropertyConstants.INVOICE_NUMBER)) {
                        String url = contractsGrantsReportHelperService.getDocSearchUrl(propValue);

                        Map<String, String> fieldList = new HashMap<String, String>();
                        fieldList.put(ArPropertyConstants.INVOICE_NUMBER, propValue);
                        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                        col.setColumnAnchor(a);
                    } else if (StringUtils.isNotBlank(propValue)) {
                        col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
                    }
                }

                ResultRow row = new ResultRow(columns, KFSConstants.EMPTY_STRING, getActionUrls(element, pkNames, businessObjectRestrictions));
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


    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> actionUrls = super.getCustomActionUrls(businessObject, pkNames);

        final CollectionActivityReport collectionActivityReport = (CollectionActivityReport)businessObject;
        String url = contractsGrantsReportHelperService.getInitiateCollectionActivityDocumentUrl(collectionActivityReport.getProposalNumber().toString(), collectionActivityReport.getInvoiceNumber());
        Map<String, String> fieldList = new HashMap<String, String>();
        final String proposalNumber = !ObjectUtils.isNull(collectionActivityReport.getProposalNumber())
                ? collectionActivityReport.getProposalNumber().toString()
                : KFSConstants.EMPTY_STRING;
        fieldList.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));
        a.setDisplayText(getConfigurationService().getPropertyValueAsString(ArKeyConstants.CollectionActivityDocumentConstants.COLLECTION_ACTIVITY_TITLE_PROPERTY));
        actionUrls.add(a);

        return actionUrls;
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

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
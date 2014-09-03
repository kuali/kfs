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

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceLookup;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a lookupable helper service class for the Collection Activity Report.
 */
public class CollectionActivityInvoiceLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityInvoiceLookupableHelperServiceImpl.class);
    protected CollectionActivityDocumentService collectionActivityDocumentService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Get the search results that meet the input search criteria.
     *
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        List<CollectionActivityInvoiceLookup> results = new ArrayList<CollectionActivityInvoiceLookup>();
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        Long proposalNumber = new Long((String) fieldValues.get(ArPropertyConstants.PROPOSAL_NUMBER));
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByProposalNumber(proposalNumber);

        for (ContractsGrantsInvoiceDocument invoiceDocument : cgInvoices) {
            results.add(convert(invoiceDocument));
        }

        return new CollectionIncomplete<CollectionActivityInvoiceLookup>(results, (long) results.size());
    }

    protected CollectionActivityInvoiceLookup convert(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        CollectionActivityInvoiceLookup cl = new CollectionActivityInvoiceLookup();
        cl.setProposalNumber(contractsGrantsInvoiceDocument.getProposalNumber());
        cl.setInvoiceNumber(contractsGrantsInvoiceDocument.getDocumentNumber());

        if (CollectionUtils.isNotEmpty(contractsGrantsInvoiceDocument.getAccountDetails())) {
            cl.setAccountNumber(contractsGrantsInvoiceDocument.getAccountDetails().get(0).getAccountNumber());
        }

        cl.setInvoiceDate(contractsGrantsInvoiceDocument.getBillingDate());
        cl.setInvoiceAmount(contractsGrantsInvoiceDocument.getSourceTotal());
        cl.setBillingPeriod(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingPeriod());
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail())) {
            cl.setBillingFrequency(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency());
        }
        cl.setPaymentAmount(getCollectionActivityDocumentService().retrievePaymentAmountByDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber()));
        cl.setBalanceDue(cl.getInvoiceAmount().subtract(cl.getPaymentAmount()));
        cl.setAge(contractsGrantsInvoiceDocument.getAge());
        return cl;
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
        if (LOG.isDebugEnabled()) {
            for (Object key : lookupForm.getFieldsForLookup().keySet()) {
                LOG.debug("Key : " + key.toString() + " Value : " + lookupForm.getFieldsForLookup().get(key));
            }
        }

        Collection displayList = getSearchResults(lookupForm.getFieldsForLookup());

        // MJM get resultTable populated here
        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        List returnKeys = getReturnKeys();

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

                }
                lookupForm.setLookupObjectId(((CollectionActivityInvoiceLookup) element).getInvoiceNumber());
                HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
                ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), getActionUrls(element, pkNames, businessObjectRestrictions));
                    row.setObjectId(((CollectionActivityInvoiceLookup) element).getInvoiceNumber());
                    row.setRowId(returnUrl.getName());
                    row.setReturnUrlHtmlData(returnUrl);
                boolean isRowReturnable = isResultReturnable(element);
                row.setRowReturnable(isRowReturnable);
                if (isRowReturnable) {
                    hasReturnableRow = true;
                }

                resultTable.add(row);
            }
            lookupForm.setHasReturnableRow(hasReturnableRow);
        }

        return displayList;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }

    public CollectionActivityDocumentService getCollectionActivityDocumentService() {
        return collectionActivityDocumentService;
    }

    public void setCollectionActivityDocumentService(CollectionActivityDocumentService collectionActivityDocumentService) {
        this.collectionActivityDocumentService = collectionActivityDocumentService;
    }
}
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceLookup;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsReportService;
import org.kuali.kfs.module.ar.web.ui.CollectionActivityInvoiceResultRow;
import org.kuali.kfs.module.ar.web.ui.ReferralToCollectionsResultRow;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Defines a lookupable helper service class for Referral To Collections Report.
 */
public class CollectionActivityInvoiceLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferralToCollectionsReportLookupableHelperServiceImpl.class);

    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;
    protected ConfigurationService configurationService;
    private Map fieldConversions;

    private CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
    private CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
    private BusinessObjectService businessObjectService;

    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
    private ReferralToCollectionsReportService referralToCollectionsReportService = SpringContext.getBean(ReferralToCollectionsReportService.class);


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
        Long proposalNumber = new Long((String) fieldValues.get(ArPropertyConstants.CollectionActivityDocumentFields.PROPOSAL_NUMBER));
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByProposalNumber(proposalNumber, "");

        for (ContractsGrantsInvoiceDocument invoiceDocument : cgInvoices) {
            results.add(convert(invoiceDocument));
        }

        return new CollectionIncomplete<CollectionActivityInvoiceLookup>(results, (long) results.size());
    }


    /**
     * Get the search results that meet the input search criteria.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResultsUnbounded(Map fieldValues) {
       List<ReferralToCollectionsReport> results = new ArrayList<ReferralToCollectionsReport>();
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        results = referralToCollectionsReportService.filterRecordsForReferralToCollections(fieldValues, true);
        return new CollectionIncomplete<ReferralToCollectionsReport>(results, (long) results.size());
    }


    private CollectionActivityInvoiceLookup convert(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        CollectionActivityInvoiceLookup cl = new CollectionActivityInvoiceLookup();
        cl.setProposalNumber(contractsGrantsInvoiceDocument.getProposalNumber());
        cl.setInvoiceNumber(contractsGrantsInvoiceDocument.getDocumentNumber());

        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAccountDetails()) && CollectionUtils.isNotEmpty(contractsGrantsInvoiceDocument.getAccountDetails())) {
            cl.setAccountNumber(contractsGrantsInvoiceDocument.getAccountDetails().get(0).getAccountNumber());
        }

        cl.setInvoiceDate(contractsGrantsInvoiceDocument.getBillingDate());
        cl.setInvoiceAmount(contractsGrantsInvoiceDocument.getSourceTotal());
        cl.setBillingPeriod(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingPeriod());
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail())) {
            cl.setBillingFrequency(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency());
        }
        cl.setPaymentAmount(contractsGrantsInvoiceDocumentService.retrievePaymentAmountByDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber()));
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
        Collection displayList;
        for (Object key : lookupForm.getFieldsForLookup().keySet()) {
            System.out.println("Key : " + key.toString() + " Value : " + lookupForm.getFieldsForLookup().get(key));
        }

        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        // MJM get resultTable populated here
        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        List returnKeys = getReturnKeys();
        
        Person user = GlobalVariables.getUserSession().getPerson();
        int i = 0;
        try {
            // iterate through result list and wrap rows with return url and action urls
            for (Object aDisplayList : displayList) {
                BusinessObject element = (BusinessObject) aDisplayList;

                BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);
                String actionUrls = "www.someACTIONurl";

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
                            // for Booleans, always use BooleanFormatter
                            if (prop instanceof Boolean) {
                                formatter = new BooleanFormatter();
                            }

                            // for Dates, always use DateFormatter
                            if (prop instanceof Date) {
                                formatter = new DateFormatter();
                            }

                            // for collection, use the list formatter if a formatter hasn't been defined yet
                            if (prop instanceof Collection && ObjectUtils.isNull(formatter)) {
                                formatter = new CollectionFormatter();
                            }

                            if (ObjectUtils.isNotNull(formatter)) {
                                propValue = (String) formatter.format(prop);
                            }
                            else {
                                propValue = prop.toString();
                            }
                        }

                        // comparator
                        col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                        col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                        propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                        col.setPropertyValue(propValue);

                    }
                    lookupForm.setLookupObjectId(((CollectionActivityInvoiceLookup) element).getInvoiceNumber());
                    HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
                    CollectionActivityInvoiceResultRow row = new CollectionActivityInvoiceResultRow((List<Column>) columns, returnUrl.constructCompleteHtmlTag(), getActionUrls(element, pkNames, businessObjectRestrictions));
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
        }
        catch (Exception e) {
            // do nothing, try block needed to make CustomerAgingReportLookupableHelperServiceImpl
            LOG.error("problem during collectionActivityInvoiceLookupableHelperService.performLookup()", e);
        }
        return displayList;
    }

    /**
     * Gets the customer inquiry url on given customerNumber
     * 
     * @param customerNumber Customer Number for inquiry on Account
     * @return Returns the url string.
     */
    private String getCustomerInquiryUrl(ReferralToCollectionsReport bo) {
        Properties params = new Properties();
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Customer.class.getName());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, KFSConstants.INQUIRY_ACTION);
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(KFSPropertyConstants.CUSTOMER_NUMBER, bo.getCustomerNumber());
        return UrlFactory.parameterizeUrl(KFSConstants.INQUIRY_ACTION, params);
    }

    /**
     * This method returns the Agency inquiry url
     * 
     * @param bo business object
     * @param columnTitle
     * @return Returns the url for the Agency Inquiry
     */
    private String getAgencyInquiryUrl(ReferralToCollectionsReport bo) {
        Properties params = new Properties();
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, ContractsAndGrantsBillingAgency.class.getName());
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, "continueWithInquiry");
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(KFSPropertyConstants.AGENCY_NUMBER, bo.getAgencyNumber());
        return UrlFactory.parameterizeUrl(KFSConstants.INQUIRY_ACTION, params);
    }

    /**
     * Sets the businessObjectService attribute.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        if (ObjectUtils.isNull(dateTimeService)) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * @return an implementation of the ConfigurationService
     */
    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    protected String createTitleText(Class<? extends BusinessObject> boClass) {
        String titleText = "";

        final String titlePrefixProp = getConfigurationService().getPropertyValueAsString("title.inquiry.url.value.prependtext");
        if (StringUtils.isNotBlank(titlePrefixProp)) {
            titleText += titlePrefixProp + " ";
        }

        final String objectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(boClass.getName()).getObjectLabel();
        if (StringUtils.isNotBlank(objectLabel)) {
            titleText += objectLabel + " ";
        }

        return titleText;
    }
    
    
}

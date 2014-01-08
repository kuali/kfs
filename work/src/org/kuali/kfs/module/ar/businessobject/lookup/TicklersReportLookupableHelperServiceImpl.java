/*
 * Copyright 2012 The Kuali Foundation.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Helper class for Tickler Reports.
 */
public class TicklersReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {

    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private CustomerService customerService;
    private PersonService personService;

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
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<TicklersReport> displayList = new ArrayList<TicklersReport>();

        CollectionActivityDocumentService colActDocService = SpringContext.getBean(CollectionActivityDocumentService.class);
        Criteria criteria = new Criteria();

        String principalId = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.COLLECTOR);
        String collectorPrincName = (String) lookupFormFields.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);

        String lookupFieldValue = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.ACTIVITY_CODE);
        if (ObjectUtils.isNotNull(lookupFieldValue) && !lookupFieldValue.equals("")) {
            criteria.addEqualTo(ArPropertyConstants.TicklersReportFields.ACTIVITY_CODE, lookupFieldValue);
        }

        lookupFieldValue = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.PROPOSAL_NUMBER);
        if (ObjectUtils.isNotNull(lookupFieldValue) && !lookupFieldValue.equals("")) {
            criteria.addEqualTo(ArPropertyConstants.EventFields.INVOICE_DOCUMENT_PROPOSAL_NUMBER, lookupFieldValue);
        }

        lookupFieldValue = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.COMPLETED);
        if (ObjectUtils.isNotNull(lookupFieldValue) && !lookupFieldValue.equals("")) {
            criteria.addEqualTo(ArPropertyConstants.EventFields.COMPLETED, lookupFieldValue);
        }

        criteria.addEqualTo(ArPropertyConstants.EventFields.INVOICE_DOCUMENT_OPEN_INV_IND, "true");
        criteria.addEqualTo(ArPropertyConstants.EventFields.FOLLOW_UP_IND, Boolean.TRUE);
        criteria.addNotEqualTo(ArPropertyConstants.EventFields.EVENT_ROUTE_STATUS, KewApiConstants.ROUTE_HEADER_SAVED_CD);

        Collection<Event> events = colActDocService.retrieveEventsByCriteria(criteria);

        lookupFieldValue = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.AGENCY_NUMBER);

        for (Event event : events) {

            // Check for followup date range
            boolean isValid = true;

            if (ObjectUtils.isNotNull(lookupFieldValue) && !lookupFieldValue.equals("")) {
                if (ObjectUtils.isNull(event.getInvoiceDocument().getAward()) || !lookupFieldValue.equals(event.getInvoiceDocument().getAward().getAgencyNumber())) {
                    isValid = false;
                }
            }

            if (isValid) {
                String propertyName = ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE;
                String dateFromFieldValues = ObjectUtils.isNull(lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + propertyName.toString())) ? "" : lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + propertyName.toString()).toString();
                String dateToFieldValues = ObjectUtils.isNull(lookupFormFields.get(propertyName.toString())) ? "" : lookupFormFields.get(propertyName.toString()).toString();

                if (ObjectUtils.isNotNull(event.getFollowupDate())) {
                    try {
                        isValid = ContractsGrantsReportUtils.isDateFieldInRange(dateFromFieldValues, dateToFieldValues, event.getFollowupDate(), propertyName.toString());
                    }
                    catch (Exception e) {
                        // do nothing
                        isValid = false;
                    }
                }
                else {
                    isValid = false;
                }
            }

            if (isValid) {
                // Check for customer collectors
                if (StringUtils.isNotEmpty(collectorPrincName)) {
                    Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
                    if (ObjectUtils.isNotNull(collUser)) {
                        principalId = collUser.getPrincipalId();
                        if (ObjectUtils.isNotNull(principalId) && !principalId.equals("")) {
                            isValid = contractsGrantsInvoiceDocumentService.canViewInvoice(event.getInvoiceDocument(), principalId);
                        }
                        else {
                            isValid = false;
                        }
                    }
                    else {
                        isValid = false;
                    }
                }
            }

            if (isValid) {
                Person user = GlobalVariables.getUserSession().getPerson();
                isValid = contractsGrantsInvoiceDocumentService.canViewInvoice(event.getInvoiceDocument(), user.getPrincipalId());
            }

            if (isValid) {

                TicklersReport ticklerReport = new TicklersReport();
                ContractsGrantsInvoiceDocument invoice = event.getInvoiceDocument();
                ticklerReport.setProposalNumber(invoice.getProposalNumber());
                ticklerReport.setActivityCode(event.getActivityCode());
                if (ObjectUtils.isNotNull(invoice.getAward()) && ObjectUtils.isNotNull(invoice.getAward().getAgency())) {
                    ticklerReport.setAgencyNumber(invoice.getAward().getAgency().getAgencyNumber());
                    ticklerReport.setAgencyName(invoice.getAward().getAgency().getFullName());
                }
                if (CollectionUtils.isNotEmpty(invoice.getAccountDetails())) {
                    ticklerReport.setAccountNumber(invoice.getAccountDetails().get(0).getAccountNumber());
                }
                ticklerReport.setFollowupDate(event.getFollowupDate());
                ticklerReport.setInvoiceNumber(event.getInvoiceNumber());
                ticklerReport.setActivityText(event.getActivityText());
                ticklerReport.setInvoiceAmount(invoice.getSourceTotal());
                ticklerReport.setBalanceDue(invoice.getSourceTotal().subtract(invoice.getPaymentAmount()));
                if (ObjectUtils.isNotNull(event.getCollectionActivityType())) {
                    ticklerReport.setActivityDescription(event.getCollectionActivityType().getActivityDescription());
                }
                ticklerReport.setCompletedInd(event.isCompletedInd());
                ticklerReport.setActivityDate(event.getActivityDate());
                ticklerReport.setUser(event.getUser().getName());
                displayList.add(ticklerReport);
            }
        }

        this.buildResultTable(lookupForm, displayList, resultTable);

        return displayList;
    }

    /**
     * @see org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsReportLookupableHelperServiceImplBase#buildResultTable(org.kuali.rice.kns.web.struts.form.LookupForm,
     *      java.util.Collection, java.util.Collection)
     */
    @Override
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;

        // Iterate through result list and wrap rows with return url and action url
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // Add url when property is invoiceNumber or proposalNumber
                if (col.getPropertyName().equals(ArPropertyConstants.TicklersReportFields.INVOICE_NUMBER)) {
                    String url = ConfigContext.getCurrentContextConfig().getKEWBaseURL() + "/" + KewApiConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KewApiConstants.COMMAND_PARAMETER + "=" + KewApiConstants.DOCSEARCH_COMMAND + "&" + KewApiConstants.DOCUMENT_ID_PARAMETER + "=" + propValue;

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(ArPropertyConstants.TicklersReportFields.INVOICE_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                    col.setColumnAnchor(a);
                }
                else if (col.getPropertyName().equals(ArPropertyConstants.TicklersReportFields.PROPOSAL_NUMBER)) {
                    String url = this.getAwardLookupUrl(propValue);
                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.PROPOSAL_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                    col.setColumnAnchor(a);
                }
                else if (org.apache.commons.lang.StringUtils.equals("Actions", col.getColumnTitle())) {

                    String url = this.getCollectionActivityDocumentUrl(element, col.getColumnTitle());
                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.PROPOSAL_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                    col.setColumnAnchor(a);
                }
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean isRowReturnable = isResultReturnable(element);
            row.setRowReturnable(isRowReturnable);
            if (isRowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);
    }

    /**
     * Gets the award lookup url on given proposal number.
     *
     * @param proposalNumber Proposal number for lookup on award.
     * @return Returns the url string.
     */
    private String getAwardLookupUrl(String proposalNumber) {
        Properties params = new Properties();
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, ContractsAndGrantsBillingAward.class.getName());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, "portal.do");
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(ArPropertyConstants.TicklersReportFields.PROPOSAL_NUMBER, proposalNumber);
        return UrlFactory.parameterizeUrl(KFSConstants.INQUIRY_ACTION, params);
    }

    /**
     * This method returns the Collection Activity create url
     *
     * @param bo business object
     * @param columnTitle
     * @return Returns the url for the Collection Activity creation
     */
    private String getCollectionActivityDocumentUrl(BusinessObject bo, String columnTitle) {
        String lookupUrl = "";
        TicklersReport detail = (TicklersReport) bo;
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "docHandler");
        parameters.put(ArPropertyConstants.CollectionActivityDocumentFields.SELECTED_PROPOSAL_NUMBER, detail.getProposalNumber().toString());
        parameters.put(ArPropertyConstants.CollectionActivityDocumentFields.SELECTED_INVOICE_DOCUMENT_NUMBER, detail.getInvoiceNumber());
        parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, "COLA");
        lookupUrl = UrlFactory.parameterizeUrl("arCollectionActivityDocument.do", parameters);

        return lookupUrl;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }
}

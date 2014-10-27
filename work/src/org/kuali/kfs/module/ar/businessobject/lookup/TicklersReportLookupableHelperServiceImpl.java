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

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
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

/**
 * Helper class for Tickler Reports.
 */
public class TicklersReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected DateTimeService dateTimeService;
    protected PersonService personService;
    protected CollectionActivityDocumentService collectionActivityDocumentService;

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

        Map<String,String> fieldValues = new HashMap<String,String>();

        String principalId = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.COLLECTOR);
        String collectorPrincName = (String) lookupFormFields.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);

        final String activityCode = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.ACTIVITY_CODE);
        if (!StringUtils.isBlank(activityCode)) {
            fieldValues.put(ArPropertyConstants.TicklersReportFields.ACTIVITY_CODE, activityCode);
        }

        final String proposalNumber = (String) lookupFormFields.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        if (!StringUtils.isBlank(proposalNumber)) {
            fieldValues.put(ArPropertyConstants.CollectionEventFields.INVOICE_DOCUMENT_PROPOSAL_NUMBER, proposalNumber);
        }

        final String completed = (String) lookupFormFields.get(ArPropertyConstants.COMPLETED);
        if (!StringUtils.isBlank(completed)) {
            fieldValues.put(ArPropertyConstants.COMPLETED, completed);
        }

        fieldValues.put(ArPropertyConstants.CollectionEventFields.INVOICE_DOCUMENT_OPEN_INV_IND, "true");
        fieldValues.put(ArPropertyConstants.CollectionEventFields.FOLLOW_UP, "true");

        Collection<CollectionEvent> collectionEvents = getCollectionActivityDocumentService().retrieveCollectionEvents(fieldValues, null);

        final String agencyNumber = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.AGENCY_NUMBER);

        for (CollectionEvent event : collectionEvents) {

            // Check for followup date range
            boolean isValid = true;

            if (!StringUtils.isBlank(agencyNumber)) {
                if (ObjectUtils.isNull(event.getInvoiceDocument().getInvoiceGeneralDetail().getAward()) || !StringUtils.equals(agencyNumber, event.getInvoiceDocument().getInvoiceGeneralDetail().getAward().getAgencyNumber())) {
                    isValid = false;
                }
            }

            if (isValid) {
                String dateFromFieldValues = ObjectUtils.isNull(lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE)) ? "" : lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE).toString();
                String dateToFieldValues = ObjectUtils.isNull(lookupFormFields.get(ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE)) ? "" : lookupFormFields.get(ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE).toString();

                if (ObjectUtils.isNotNull(event.getFollowupDate())) {
                    isValid = isEventFollowupDateFieldInRange(event, dateFromFieldValues, dateToFieldValues);
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
                ticklerReport.setProposalNumber(invoice.getInvoiceGeneralDetail().getProposalNumber());
                ticklerReport.setActivityCode(event.getActivityCode());
                if (ObjectUtils.isNotNull(invoice.getInvoiceGeneralDetail().getAward()) && ObjectUtils.isNotNull(invoice.getInvoiceGeneralDetail().getAward().getAgency())) {
                    ticklerReport.setAgencyNumber(invoice.getInvoiceGeneralDetail().getAward().getAgency().getAgencyNumber());
                    ticklerReport.setAgencyName(invoice.getInvoiceGeneralDetail().getAward().getAgency().getFullName());
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
                ticklerReport.setCompleted(event.isCompleted());
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
                if (col.getPropertyName().equals(ArPropertyConstants.INVOICE_NUMBER)) {
                    String url = contractsGrantsReportHelperService.getDocSearchUrl(propValue);

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(ArPropertyConstants.INVOICE_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                    col.setColumnAnchor(a);
                } else if (org.apache.commons.lang.StringUtils.equals(ArConstants.ACTIONS_LABEL, col.getColumnTitle())) {
                    TicklersReport ticklersReport = (TicklersReport) element;
                    String url = contractsGrantsReportHelperService.getInitiateCollectionActivityDocumentUrl(ticklersReport.getProposalNumber().toString(), ticklersReport.getInvoiceNumber());
                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.PROPOSAL_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                    col.setColumnAnchor(a);
                } else if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
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
     * Filters out events with follow up dates not within the given date range
     * @param event the event with a follow-up date
     * @param dateFromFieldValues the beginning of the date range
     * @param dateToFieldValues the end of the date range
     * @return true if date field is within range, false otherwise.
     */
    protected boolean isEventFollowupDateFieldInRange(CollectionEvent event, String dateFromFieldValues, String dateToFieldValues) {
        if (ObjectUtils.isNull(event.getFollowupDate())) {
            return true; // we don't have a follow up date, so let's just bail without filtering out this event
        }

        // Clearing time field for date only comparison
        final Date clearedFollowupDate = KfsDateUtils.clearTimeFields(event.getFollowupDate());

        try {
            // Both are blank or null
            if (StringUtils.isBlank(dateToFieldValues)) {
                if (StringUtils.isBlank(dateFromFieldValues)) {
                    return true;
                } else {
                    final Date dateFrom = getDateTimeService().convertToSqlDate(dateFromFieldValues);
                    return clearedFollowupDate.after(dateFrom) || clearedFollowupDate.equals(dateFrom);
                }
            } else {
                if (StringUtils.isBlank(dateFromFieldValues)) {
                    final Date dateTo = getDateTimeService().convertToSqlDate(dateToFieldValues);
                    return clearedFollowupDate.before(dateTo) || clearedFollowupDate.equals(dateTo);
                } else {
                    final Date dateTo = getDateTimeService().convertToSqlDate(dateToFieldValues);
                    final Date dateFrom = getDateTimeService().convertToSqlDate(dateFromFieldValues);
                    return (clearedFollowupDate.after(dateFrom) || clearedFollowupDate.equals(dateFrom)) && (clearedFollowupDate.before(dateTo) || clearedFollowupDate.equals(dateTo));
                }
            }

        }
        catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
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

    public CollectionActivityDocumentService getCollectionActivityDocumentService() {
        return collectionActivityDocumentService;
    }

    public void setCollectionActivityDocumentService(CollectionActivityDocumentService collectionActivityDocumentService) {
        this.collectionActivityDocumentService = collectionActivityDocumentService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
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
import org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
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
public class TicklersReportLookupableHelperServiceImpl extends CollectionsReportLookupableHelperServiceImplBase {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected DateTimeService dateTimeService;
    protected PersonService personService;
    protected ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService;

    /**
     * Validates the follow up date fields
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        if (!ObjectUtils.isNull(fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE))) {
            final String dateFromFieldValues = fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE).toString();
            validateDateField(dateFromFieldValues, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE+ArConstants.FROM_SUFFIX, getDateTimeService());
        }
        if (!ObjectUtils.isNull(fieldValues.get(ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE))) {
            final String dateToFieldValues = fieldValues.get(ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE).toString();
            validateDateField(dateToFieldValues, ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE+ArConstants.TO_SUFFIX, getDateTimeService());
        }
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

        fieldValues.put(ArPropertyConstants.CollectionEventFields.FOLLOW_UP, KFSConstants.Booleans.TRUE);

        final String dateFromFieldValues = ObjectUtils.isNull(lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE)) ? KFSConstants.EMPTY_STRING : lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE).toString();
        final String dateToFieldValues = ObjectUtils.isNull(lookupFormFields.get(ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE)) ? KFSConstants.EMPTY_STRING : lookupFormFields.get(ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE).toString();
        final String followUpDateCriteria = getContractsGrantsReportHelperService().fixDateCriteria(dateFromFieldValues, dateToFieldValues, false);
        if (!StringUtils.isBlank(followUpDateCriteria)) {
            fieldValues.put(ArPropertyConstants.TicklersReportFields.FOLLOWUP_DATE, followUpDateCriteria);
        }

        Collection<CollectionEvent> collectionEvents = getLookupService().findCollectionBySearchUnbounded(CollectionEvent.class, fieldValues);

        final String agencyNumber = (String) lookupFormFields.get(ArPropertyConstants.TicklersReportFields.AGENCY_NUMBER);

        for (CollectionEvent event : collectionEvents) {

            // Check for followup date range
            boolean isValid = true;

            if (StringUtils.isNotBlank(completed)) {
                if(StringUtils.equalsIgnoreCase(completed, "Y")) {
                    isValid = event.isCompleted();
                } else if (StringUtils.equalsIgnoreCase(completed,"N")) {
                    isValid = !event.isCompleted();
                }

            }

           ContractsGrantsInvoiceDocument invoice = event.getInvoiceDocument();
//            if (!invoice.isOpenInvoiceIndicator()) {
//                isValid = false;
//            }

            if (!StringUtils.isBlank(agencyNumber) && isValid) {
                if (ObjectUtils.isNull(event.getInvoiceDocument().getInvoiceGeneralDetail().getAward()) || !StringUtils.equals(agencyNumber, event.getInvoiceDocument().getInvoiceGeneralDetail().getAward().getAgencyNumber())) {
                    isValid = false;
                }
            }

            if (isValid) {
                // Check for customer collectors
                if (StringUtils.isNotEmpty(collectorPrincName)) {
                    Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
                    if (ObjectUtils.isNotNull(collUser)) {
                        principalId = collUser.getPrincipalId();
                        if (!StringUtils.isBlank(principalId)) {
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
                ticklerReport.setEventId(event.getId());

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
                ticklerReport.setActivityDate(event.getActivityDate());
                ticklerReport.setCompletedDate(event.getCompletedDate());
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
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());

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
                } else if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
                }
            }

            ResultRow row = new ResultRow(columns, "", getActionUrls(element, pkNames, businessObjectRestrictions));

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

    public ContractsGrantsCollectionActivityDocumentService getContractsGrantsCollectionActivityDocumentService() {
        return contractsGrantsCollectionActivityDocumentService;
    }

    public void setContractsGrantsCollectionActivityDocumentService(ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService) {
        this.contractsGrantsCollectionActivityDocumentService = contractsGrantsCollectionActivityDocumentService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}

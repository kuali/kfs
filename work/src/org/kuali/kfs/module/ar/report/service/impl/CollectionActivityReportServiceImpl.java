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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityType;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.CollectionActivityReportService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to get the services for PDF generation and other services for Collection Activity Report
 */
public class CollectionActivityReportServiceImpl implements CollectionActivityReportService {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected PersonService personService;
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityReportServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#filterContractsGrantsAgingReport(java.util.Map)
     */
    @Override
    @Transactional
    public List<CollectionActivityReport> filterEventsForCollectionActivity(Map lookupFormFields) {

        List<CollectionActivityReport> displayList = new ArrayList<>();

        Map<String,String> fieldValues = new HashMap<>();

        String collectorPrincName = (String)lookupFormFields.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);
        String collector = (String)lookupFormFields.get(ArPropertyConstants.CollectionActivityReportFields.COLLECTOR);
        String proposalNumber = (String)lookupFormFields.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String agencyNumber = (String)lookupFormFields.get(ArPropertyConstants.CollectionActivityReportFields.AGENCY_NUMBER);
        String invoiceNumber = (String)lookupFormFields.get(ArPropertyConstants.INVOICE_NUMBER);
        String accountNumber = (String)lookupFormFields.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String customerNumber = (String)lookupFormFields.get(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER);

        // Getting final docs
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, StringUtils.join(getFinancialSystemDocumentService().getSuccessfulDocumentStatuses(), "|"));
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);

        if (!StringUtils.isBlank(proposalNumber)) {
            fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }

        if (!StringUtils.isBlank(invoiceNumber)) {
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceNumber);
        }

        if (!StringUtils.isBlank(accountNumber)) {
            fieldValues.put(ArPropertyConstants.CustomerInvoiceDocumentFields.ACCOUNT_NUMBER, accountNumber);
        }

        if (!StringUtils.isBlank(customerNumber)) {
            fieldValues.put(ArPropertyConstants.ACCOUNTS_RECEIVABLE_DOCUMENT_HEADER+"."+ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customerNumber);
        }

        // Filter Invoice docs according to criteria.
        Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs = contractsGrantsInvoiceDocumentService.retrieveAllCGInvoicesByCriteria(fieldValues);

        if (!CollectionUtils.isEmpty(contractsGrantsInvoiceDocs)) {
            String collectorPrincipalId = null;
            if (StringUtils.isNotEmpty(collectorPrincName.trim())) {
                Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
                if (ObjectUtils.isNotNull(collUser)) {
                    collectorPrincipalId = collUser.getPrincipalId();
                }
            }

            Set<String> agencyNumbers = null;
            if (!StringUtils.isBlank(agencyNumber)) {
                agencyNumbers = this.getMatchingAgencyNumbers(agencyNumber);
            }

            for (Iterator<ContractsGrantsInvoiceDocument> iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                ContractsGrantsInvoiceDocument document = iter.next();

                if (!canDocumentBeViewed(document, collectorPrincipalId)) {
                    iter.remove();
                }
                else if (!CollectionUtils.isEmpty(agencyNumbers) && !matchesAgencyNumber(document, agencyNumbers)) {
                    iter.remove();
                }
            }
        }

        if (!CollectionUtils.isEmpty(contractsGrantsInvoiceDocs)) {
            for (ContractsGrantsInvoiceDocument cgInvoiceDocument : contractsGrantsInvoiceDocs) {
                List<CollectionEvent> events = cgInvoiceDocument.getCollectionEvents();
                List<CustomerInvoiceDetail> details = cgInvoiceDocument.getSourceAccountingLines();
                final String accountNum = (!CollectionUtils.isEmpty(details) && !ObjectUtils.isNull(details.get(0))) ? details.get(0).getAccountNumber() : "";
                if (CollectionUtils.isNotEmpty(events)) {
                    for (CollectionEvent event : events) {
                        String workflowDocumentStatusCode = financialSystemDocumentService.findByDocumentNumber(event.getDocumentNumber()).getWorkflowDocumentStatusCode();
                        if (!StringUtils.equals(workflowDocumentStatusCode, DocumentStatus.SAVED.getCode())) {
                            CollectionActivityReport collectionActivityReport = new CollectionActivityReport();
                            collectionActivityReport.setAccountNumber(accountNum);
                            convertEventToCollectionActivityReport(collectionActivityReport, event);
                            displayList.add(collectionActivityReport);
                        }
                    }
                }
            }
        }

        return displayList;
    }

    /**
     * Determines if the given CINV can be viewed for the given collector and by the current user
     * @param document the CINV document to test
     * @param collectorPrincipalId the collector principal id to test if it exists
     * @return true if the document can be viewed, false otherwise
     */
    protected boolean canDocumentBeViewed(ContractsGrantsInvoiceDocument document, String collectorPrincipalId) {
        final Person user = GlobalVariables.getUserSession().getPerson();
        return (StringUtils.isBlank(collectorPrincipalId) || contractsGrantsInvoiceDocumentService.canViewInvoice(document, collectorPrincipalId))
                && contractsGrantsInvoiceDocumentService.canViewInvoice(document, user.getPrincipalId());
    }

    /**
     * Assuming that the given agencyNumberLookup may have wildcard characters, attempts to look up all matching agency numbers
     * @param agencyNumberLookup the agency number from the lookup to find agency numbers on actual awards for
     * @return any matching agency numbers from matching awards
     */
    protected Set<String> getMatchingAgencyNumbers(String agencyNumberLookup) {
        Set<String> matchingAgencyNumbers = new HashSet<>();

        Map<String, String> agencyLookupFields = new HashMap<>();
        agencyLookupFields.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumberLookup);
        List<? extends ContractsAndGrantsAward> awards = getContractsAndGrantsModuleBillingService().lookupAwards(agencyLookupFields, true);
        if (!CollectionUtils.isEmpty(awards)) {
            for (ContractsAndGrantsAward award : awards) {
                if (award instanceof ContractsAndGrantsBillingAward) {
                    matchingAgencyNumbers.add(((ContractsAndGrantsBillingAward)award).getAgencyNumber());
                }
            }
        }
        if (matchingAgencyNumbers.isEmpty()) {
            return null; // we'll assume that the return is null...
        }

        return matchingAgencyNumbers;
    }

    /**
     * Determines if the given document matches the passed in agency number
     * @param document the document to check
     * @param agencyNumber the agency number to verify against
     * @return true if the document matches the given agency number, false otherwise
     */
    protected boolean matchesAgencyNumber(ContractsGrantsInvoiceDocument document, Set<String> agencyNumbers) {
        if (!ObjectUtils.isNull(document) && !ObjectUtils.isNull(document.getInvoiceGeneralDetail().getAward()) && !StringUtils.isBlank(document.getInvoiceGeneralDetail().getAward().getAgencyNumber())) {
            final String documentAgencyNumber = document.getInvoiceGeneralDetail().getAward().getAgencyNumber();
            return agencyNumbers.contains(documentAgencyNumber);
        }
        return false;
    }

    /**
     * This method is used to convert the CollectionEvent Object into collection activity report.
     *
     * @param collectionActivityReport
     * @param collectionEvent
     * @return Returns the Object of CollectionActivityReport class.
     */
    protected CollectionActivityReport convertEventToCollectionActivityReport(CollectionActivityReport collectionActivityReport, CollectionEvent collectionEvent) {

        if (ObjectUtils.isNull(collectionEvent)) {
            LOG.error("an invalid(null) argument was given");
            throw new IllegalArgumentException("an invalid(null) argument was given");
        }

        // account no
        collectionActivityReport.setInvoiceNumber(collectionEvent.getInvoiceNumber());
        collectionActivityReport.setActivityDate(collectionEvent.getActivityDate());

        // Activity Type
        CollectionActivityType collectionActivityType = collectionEvent.getCollectionActivityType();

        if (ObjectUtils.isNotNull(collectionActivityType)) {
            collectionActivityReport.setActivityType(collectionActivityType.getActivityDescription());
        }

        collectionActivityReport.setActivityComment(collectionEvent.getActivityText());
        collectionActivityReport.setFollowupDate(collectionEvent.getFollowupDate());
        collectionActivityReport.setProposalNumber(collectionEvent.getInvoiceDocument().getInvoiceGeneralDetail().getProposalNumber());
        collectionActivityReport.setCompletedDate(collectionEvent.getCompletedDate());

        String userPrincId = collectionEvent.getUserPrincipalId();
        Person person = personService.getPerson(userPrincId);

        if (ObjectUtils.isNotNull(person)) {
            collectionActivityReport.setUserPrincipalId(person.getName());
        }

        if (ObjectUtils.isNotNull(collectionEvent.getInvoiceDocument())) {
            collectionActivityReport.setChartOfAccountsCode(collectionEvent.getInvoiceDocument().getBillByChartOfAccountCode());
        }
        if (ObjectUtils.isNotNull(collectionEvent.getInvoiceDocument().getInvoiceGeneralDetail()) && ObjectUtils.isNotNull(collectionEvent.getInvoiceDocument().getInvoiceGeneralDetail().getAward()) && ObjectUtils.isNotNull(collectionEvent.getInvoiceDocument().getInvoiceGeneralDetail().getAward().getAgency())) {
            collectionActivityReport.setAgencyNumber(collectionEvent.getInvoiceDocument().getInvoiceGeneralDetail().getAward().getAgency().getAgencyNumber());
            collectionActivityReport.setAgencyName(collectionEvent.getInvoiceDocument().getInvoiceGeneralDetail().getAward().getAgency().getFullName());
        }
        return collectionActivityReport;
    }

    @NonTransactional
    public PersonService getPersonService() {
        return personService;
    }

    @NonTransactional
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    @NonTransactional
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    @NonTransactional
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentService attribute value.
     *
     * @param contractsGrantsInvoiceDocumentService The contractsGrantsInvoiceDocumentService to set.
     */
    @NonTransactional
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }
}

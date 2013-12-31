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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityType;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.CollectionActivityReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to get the services for PDF generation and other services for Collection Activity Report
 */
public class CollectionActivityReportServiceImpl extends ContractsGrantsReportServiceImplBase implements CollectionActivityReportService {

    private ReportInfo collActReportInfo;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected BusinessObjectService businessObjectService;
    private PersonService personService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityReportServiceImpl.class);

    /**
     * Gets the collActReportInfo attribute.
     *
     * @return Returns the collActReportInfo.
     */
    @NonTransactional
    public ReportInfo getCollActReportInfo() {
        return collActReportInfo;
    }

    /**
     * Sets the collActReportInfo attribute value.
     *
     * @param collActReportInfo The collActReportInfo to set.
     */
    @NonTransactional
    public void setCollActReportInfo(ReportInfo collActReportInfo) {
        this.collActReportInfo = collActReportInfo;
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

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    @NonTransactional
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {
        return generateReport(reportDataHolder, collActReportInfo, baos);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#filterContractsGrantsAgingReport(java.util.Map)
     */
    @Override
    @Transactional
    public List<CollectionActivityReport> filterEventsForColletionActivity(Map lookupFormFields) {

        List<CollectionActivityReport> displayList = new ArrayList<CollectionActivityReport>();

        CollectionActivityDocumentService colActDocService = SpringContext.getBean(CollectionActivityDocumentService.class);
        Criteria criteria = new Criteria();

        String collectorPrincName = lookupFormFields.get(ArPropertyConstants.COLLECTOR_PRINC_NAME).toString();
        String collector = lookupFormFields.get(ArPropertyConstants.CollectionActivityReportFields.COLLECTOR).toString();
        String proposalNumber = lookupFormFields.get(ArPropertyConstants.CollectionActivityReportFields.PROPOSAL_NUMBER).toString();
        String agencyNumber = lookupFormFields.get(ArPropertyConstants.CollectionActivityReportFields.AGENCY_NUMBER).toString();
        String invoiceNumber = lookupFormFields.get(ArPropertyConstants.CollectionActivityReportFields.INVOICE_NUMBER).toString();
        String accountNumber = lookupFormFields.get(ArPropertyConstants.CollectionActivityReportFields.ACCOUNT_NUMBER).toString();

        // Getting final docs
        criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        if (ObjectUtils.isNotNull(proposalNumber) && StringUtils.isNotEmpty(proposalNumber)) {
            criteria.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }

        if (ObjectUtils.isNotNull(invoiceNumber) && StringUtils.isNotEmpty(invoiceNumber)) {
            criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoiceNumber);
        }

        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotEmpty(accountNumber)) {
            criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.ACCOUNT_NUMBER, accountNumber);
        }

        // Filter Invoice docs according to criteria.
        Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs = contractsGrantsInvoiceDocumentService.retrieveAllCGInvoicesByCriteria(criteria);
        contractsGrantsInvoiceDocs = contractsGrantsInvoiceDocumentService.attachWorkflowHeadersToCGInvoices(contractsGrantsInvoiceDocs);

        // Filter "CINV" docs and remove "INV" docs.
        // also filter out invoice docs by collector and for the user performing the search
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            String collectorPrincipalId = null;
            if (StringUtils.isNotEmpty(collectorPrincName.trim())) {
                Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
                if (ObjectUtils.isNotNull(collUser)) {
                    collectorPrincipalId = collUser.getPrincipalId();
                }
            }
            Person user = GlobalVariables.getUserSession().getPerson();

            for (Iterator<ContractsGrantsInvoiceDocument> iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                try {
                    ContractsGrantsInvoiceDocument document = iter.next();
                    String documentTypeName = document.getAccountsReceivableDocumentHeader().getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
                    if (!documentTypeName.equals(ArPropertyConstants.CONTRACTS_GRANTS_INV_DOC_TYPE)) {
                        iter.remove();
                        continue;
                    }

                    if (StringUtils.isNotEmpty(collectorPrincipalId)) {
                        if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, collectorPrincipalId)) {
                            iter.remove();
                            continue;
                        }
                    }

                    if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, user.getPrincipalId())) {
                        iter.remove();
                    }

                }
                catch (Exception wfe) {
                    LOG.error("problem during CollectionActivityReportServiceImpl.filterEventsForColletionActivity()", wfe);
                }
            }
        }

        // find by agency number
        if (ObjectUtils.isNotNull(agencyNumber) && StringUtils.isNotEmpty(agencyNumber)) {
            contractsGrantsInvoiceDocs = this.filterContractsGrantsDocsAccordingToAgency(contractsGrantsInvoiceDocs, agencyNumber);
        }

        // Set account number.
        String accountNum = null;
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && CollectionUtils.isNotEmpty(contractsGrantsInvoiceDocs)) {
            for (ContractsGrantsInvoiceDocument cgInvoiceDocument : contractsGrantsInvoiceDocs) {
                List<Event> events = cgInvoiceDocument.getEvents();
                List<CustomerInvoiceDetail> details = cgInvoiceDocument.getSourceAccountingLines();
                accountNum = (ObjectUtils.isNotNull(details) && CollectionUtils.isNotEmpty(details) && ObjectUtils.isNotNull(details.get(0))) ? details.get(0).getAccountNumber() : "";
                if (ObjectUtils.isNotNull(events) && CollectionUtils.isNotEmpty(events)) {
                    for (Event event : events) {
                        if (ObjectUtils.isNull(event.getEventRouteStatus()) || !event.getEventRouteStatus().equals(KewApiConstants.ROUTE_HEADER_SAVED_CD)) {
                            CollectionActivityReport collectionActivityReport = new CollectionActivityReport();
                            collectionActivityReport.setAccountNumber(accountNum);
                            this.convertEventToCollectionActivityReport(collectionActivityReport, event);
                            displayList.add(collectionActivityReport);
                        }
                    }
                }
            }
        }

        return displayList;
    }


    /**
     * This method filters the Contracts Grants Invoice doc by Award related details
     *
     * @param contractsGrantsInvoiceDocs
     * @param awardDocumentNumber
     * @param awardEndDate
     * @param fundManager
     * @return Returns the list of ContractsGrantsInvoiceDocument.
     */
    @SuppressWarnings("unchecked")
    protected Collection<ContractsGrantsInvoiceDocument> filterContractsGrantsDocsAccordingToAgency(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs, String agencyNumber) {
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            for (Iterator iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                ContractsGrantsInvoiceDocument cgDoc = (ContractsGrantsInvoiceDocument) iter.next();
                String agencyNum = cgDoc.getAward().getAgencyNumber();
                if (!(ObjectUtils.isNotNull(agencyNum) && agencyNum.equals(agencyNumber))) {
                    iter.remove();
                }
            }
        }
        return contractsGrantsInvoiceDocs;
    }


    /**
     * This method is used to convert the Event Object into collection activity report.
     *
     * @param collectionActivityReport
     * @param event
     * @return Returns the Object of CollectionActivityReport class.
     */
    protected CollectionActivityReport convertEventToCollectionActivityReport(CollectionActivityReport collectionActivityReport, Event event) {

        // account no
        collectionActivityReport.setInvoiceNumber(event.getInvoiceNumber());
        collectionActivityReport.setActivityDate(event.getActivityDate());
        String activityCode = event.getActivityCode();
        Map<String, String> map = new HashMap<String, String>();

        if (ObjectUtils.isNotNull(activityCode)) {
            map.put(ArPropertyConstants.CollectionActivityTypeFields.ACTIVITY_CODE, activityCode);
            CollectionActivityType collectionActivityType = businessObjectService.findByPrimaryKey(CollectionActivityType.class, map);
            if (ObjectUtils.isNotNull(collectionActivityType)) {
                collectionActivityReport.setActivityType(collectionActivityType.getActivityDescription());
            }
        }

        collectionActivityReport.setActivityComment(event.getActivityText());
        collectionActivityReport.setFollowupDate(event.getFollowupDate());
        collectionActivityReport.setProposalNumber(event.getInvoiceDocument().getProposalNumber());
        collectionActivityReport.setCompletedInd(event.isCompletedInd());
        collectionActivityReport.setCompletedDate(event.getCompletedDate());

        String userPrincId = event.getUserPrincipalId();
        Person person = personService.getPerson(userPrincId);

        if (ObjectUtils.isNotNull(person)) {
            collectionActivityReport.setUserPrincipalId(person.getName());
        }

        if (ObjectUtils.isNotNull(event.getInvoiceDocument())) {
            collectionActivityReport.setChartOfAccountsCode(event.getInvoiceDocument().getBillByChartOfAccountCode());
        }
        if (ObjectUtils.isNotNull(event.getInvoiceDocument().getAward()) && ObjectUtils.isNotNull(event.getInvoiceDocument().getAward().getAgency())) {
            collectionActivityReport.setAgencyNumber(event.getInvoiceDocument().getAward().getAgency().getAgencyNumber());
            collectionActivityReport.setAgencyName(event.getInvoiceDocument().getAward().getAgency().getFullName());
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

}

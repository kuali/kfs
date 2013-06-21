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
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.businessobject.CollectorInformation;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.dataaccess.CollectorHierarchyDao;
import org.kuali.kfs.module.ar.dataaccess.CustomerCollectorDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.CollectionActivityReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is used to get the services for PDF generation and other services for Collection Activity Report
 */
public class CollectionActivityReportServiceImpl extends ContractsGrantsReportServiceImplBase implements CollectionActivityReportService {

    private ReportInfo collActReportInfo;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private CollectorHierarchyDao collectorHierarchyDao;
    private CustomerCollectorDao customerCollectorDao;
    protected BusinessObjectService businessObjectService;

    /**
     * Gets the collActReportInfo attribute.
     *
     * @return Returns the collActReportInfo.
     */
    public ReportInfo getCollActReportInfo() {
        return collActReportInfo;
    }

    /**
     * Sets the collActReportInfo attribute value.
     *
     * @param collActReportInfo The collActReportInfo to set.
     */
    public void setCollActReportInfo(ReportInfo collActReportInfo) {
        this.collActReportInfo = collActReportInfo;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the collectorHierarchyDao attribute value.
     *
     * @param collectorHierarchyDao The collectorHierarchyDao to set.
     */
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    /**
     * Gets the collectorHierarchyDao attribute.
     *
     * @return Returns the collectorHierarchyDao.
     */
    public CollectorHierarchyDao getCollectorHierarchyDao() {
        return collectorHierarchyDao;
    }

    /**
     * Sets the collectorHierarchyDao attribute value.
     *
     * @param collectorHierarchyDao The collectorHierarchyDao to set.
     */
    public void setCollectorHierarchyDao(CollectorHierarchyDao collectorHierarchyDao) {
        this.collectorHierarchyDao = collectorHierarchyDao;
    }

    /**
     * Gets the customerCollectorDao attribute.
     *
     * @return Returns the customerCollectorDao.
     */
    public CustomerCollectorDao getCustomerCollectorDao() {
        return customerCollectorDao;
    }

    /**
     * Sets the customerCollectorDao attribute value.
     *
     * @param customerCollectorDao The customerCollectorDao to set.
     */
    public void setCustomerCollectorDao(CustomerCollectorDao customerCollectorDao) {
        this.customerCollectorDao = customerCollectorDao;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {
        return generateReport(reportDataHolder, collActReportInfo, baos);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#filterContractsGrantsAgingReport(java.util.Map)
     */
    @Override
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

        // Filter "CGIN" docs and remove "INV" docs.
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            for (Iterator iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                try {
                    ContractsGrantsInvoiceDocument document = (ContractsGrantsInvoiceDocument) iter.next();
                    String documentTypeName = document.getAccountsReceivableDocumentHeader().getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
                    if (!documentTypeName.equals(ArPropertyConstants.CONTRACTS_GRANTS_INV_DOC_TYPE)) {
                        iter.remove();
                    }
                }
                catch (Exception wfe) {
                    wfe.printStackTrace();
                }
            }
        }

        // filter by collector
        List<String> collectorList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(collectorPrincName.trim())) {
            PersonService personService = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class);
            Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
            if (ObjectUtils.isNotNull(collUser)) {
                collector = collUser.getPrincipalId();
                if (ObjectUtils.isNotNull(collector) && StringUtils.isNotEmpty(collector.trim())) {
                    Criteria collectorCriteria = new Criteria();
                    collectorCriteria.addEqualTo(ArPropertyConstants.COLLECTOR_HEAD, collector);
                    collectorCriteria.addEqualTo(KFSPropertyConstants.ACTIVE, true);

                    // chk if selected collector is collector head
                    Collection<CollectorHierarchy> collectorHierarchies = collectorHierarchyDao.getCollectorHierarchyByCriteria(collectorCriteria);

                    if (ObjectUtils.isNotNull(collectorHierarchies) && CollectionUtils.isNotEmpty(collectorHierarchies)) {
                        CollectorHierarchy collectorHead = new ArrayList<CollectorHierarchy>(collectorHierarchies).get(0);
                        if (ObjectUtils.isNotNull(collectorHead)) {
                            collectorList.add(collectorHead.getPrincipalId());
                            if (ObjectUtils.isNotNull(collectorHead.getCollectorInformations()) && CollectionUtils.isNotEmpty(collectorHead.getCollectorInformations())) {
                                // get principal ids of collector
                                for (CollectorInformation collectorInfo : collectorHead.getCollectorInformations()) {
                                    if (collectorInfo.isActive()) {
                                        collectorList.add(collectorInfo.getPrincipalId());
                                    }
                                }
                            }
                        }
                        else {
                            if (collectorHierarchyDao.isCollector(collector)) {
                                collectorList.add(collector);
                            }
                        }
                    }
                    else {
                        // check it exists in collector information and is active and his head is active
                        if (collectorHierarchyDao.isCollector(collector)) {
                            collectorList.add(collector);
                        }
                    }

                    // filter invoice by Collectorlist
                    if (ObjectUtils.isNotNull(collectorList) && !collectorList.isEmpty()) {
                        // retrieve customer numbers for the collectors
                        collectorCriteria = new Criteria();
                        collectorCriteria.addIn(KFSPropertyConstants.PRINCIPAL_ID, collectorList);
                        List<String> customerNumbers = customerCollectorDao.retrieveCustomerNmbersByCriteria(collectorCriteria);
                        contractsGrantsInvoiceDocs = this.filterInvoicesAccordingToCustomerNumbers(contractsGrantsInvoiceDocs, customerNumbers);
                    }
                    else {
                        displayList.clear();
                        return displayList;
                    }
                }
            }
            else {
                displayList.clear();
                return displayList;
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
    private Collection<ContractsGrantsInvoiceDocument> filterContractsGrantsDocsAccordingToAgency(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs, String agencyNumber) {
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
     * This method filters invoices according to collectors
     *
     * @param contractsGrantsInvoiceDocuments
     * @param customerNumbers
     * @return Returns the list of ContractsGrantsInvoiceDocument.
     */
    private Collection<ContractsGrantsInvoiceDocument> filterInvoicesAccordingToCustomerNumbers(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocuments, List<String> customerNumbers) {
        List<ContractsGrantsInvoiceDocument> filteredInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocuments) && CollectionUtils.isNotEmpty(contractsGrantsInvoiceDocuments)) {
            for (ContractsGrantsInvoiceDocument cgDoc : contractsGrantsInvoiceDocuments) {
                if (isCustomerAvailableInList(cgDoc.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerNumbers)) {
                    filteredInvoices.add(cgDoc);
                }
            }
        }
        return filteredInvoices;
    }

    /**
     * This method checks that the given customer is found in list of customers found in customer collectors
     *
     * @param customerNumber
     * @param customerNumbers
     * @return Returns true if customer found in the list otherwise false
     */
    private boolean isCustomerAvailableInList(String customerNumber, List<String> customerNumbers) {
        boolean isAvail = false;
        if (ObjectUtils.isNotNull(customerNumbers) && !customerNumbers.isEmpty()) {
            for (String custNmber : customerNumbers) {
                if (custNmber.equalsIgnoreCase(customerNumber)) {
                    isAvail = true;
                    break;
                }
            }
        }
        else {
            isAvail = false;
        }
        return isAvail;
    }

    /**
     * This method is used to convert the Event Object into collection activity report.
     *
     * @param collectionActivityReport
     * @param event
     * @return Returns the Object of CollectionActivityReport class.
     */
    private CollectionActivityReport convertEventToCollectionActivityReport(CollectionActivityReport collectionActivityReport, Event event) {

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
        PersonService personService = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class);

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

}

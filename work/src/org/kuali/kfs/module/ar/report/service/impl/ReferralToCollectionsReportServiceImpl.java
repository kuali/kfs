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
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionStatus;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.businessobject.CollectorInformation;
import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.module.ar.businessobject.FinalDisposition;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsReport;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.module.ar.dataaccess.CollectorHierarchyDao;
import org.kuali.kfs.module.ar.dataaccess.CustomerCollectorDao;
import org.kuali.kfs.module.ar.dataaccess.ReferralToCollectionsDao;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is used to get the services for PDF generation and other services for Referral To Collections Report.
 */
public class ReferralToCollectionsReportServiceImpl extends ContractsGrantsReportServiceImplBase implements ReferralToCollectionsReportService {

    private ReportInfo refToCollReportInfo;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private CollectorHierarchyDao collectorHierarchyDao;
    private CustomerCollectorDao customerCollectorDao;
    protected BusinessObjectService businessObjectService;
    private ReferralToCollectionsDao referralToCollectionsDao;
    private DocumentService documentService;
    private String proposalNumber;
    private String accountNumber;
    private String invoiceNumber;
    private boolean considerProposalNumberInd;
    private boolean considerAccountNumberInd;
    private boolean considerinvoiceNumberInd;


    /**
     * Gets the refToCollReportInfo attribute.
     *
     * @return Returns the refToCollReportInfo.
     */
    public ReportInfo getRefToCollReportInfo() {
        return refToCollReportInfo;
    }

    /**
     * Gets the refToCollReportInfo attribute.
     *
     * @return Returns the refToCollReportInfo.
     */
    public void setRefToCollReportInfo(ReportInfo refToCollReportInfo) {
        this.refToCollReportInfo = refToCollReportInfo;
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
     * Gets the referralToCollectionsDao attribute.
     *
     * @return Returns the referralToCollectionsDao.
     */
    public ReferralToCollectionsDao getReferralToCollectionsDao() {
        return referralToCollectionsDao;
    }

    /**
     * Sets the referralToCollectionsDao attribute value.
     *
     * @param referralToCollectionsDao The referralToCollectionsDao to set.
     */
    public void setReferralToCollectionsDao(ReferralToCollectionsDao referralToCollectionsDao) {
        this.referralToCollectionsDao = referralToCollectionsDao;
    }

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {
        return generateReport(reportDataHolder, refToCollReportInfo, baos);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#filterContractsGrantsAgingReport(java.util.Map)
     */
    @Override
    public List<ReferralToCollectionsReport> filterRecordsForReferralToCollections(Map lookupFormFields, boolean isPdf) {

        List<ReferralToCollectionsReport> displayList = new ArrayList<ReferralToCollectionsReport>();

        Criteria criteria = new Criteria();

        String collectorPrincName = lookupFormFields.get(ArPropertyConstants.COLLECTOR_PRINC_NAME).toString();
        String collector = lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.COLLECTOR).toString();
        proposalNumber = lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.PROPOSAL_NUMBER).toString();
        String agencyNumber = lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.AGENCY_NUMBER).toString();
        invoiceNumber = lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.INVOICE_NUMBER).toString();
        accountNumber = lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.ACCOUNT_NUMBER).toString();

        if (ObjectUtils.isNotNull(agencyNumber) && StringUtils.isNotEmpty(agencyNumber)) {
            criteria.addEqualTo(ArPropertyConstants.ReferralToCollectionsFields.AGENCY_NUMBER, agencyNumber);
        }

        considerProposalNumberInd = false;
        considerinvoiceNumberInd = false;
        considerAccountNumberInd = false;

        // considering final docs
        criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        if (ObjectUtils.isNotNull(proposalNumber) && StringUtils.isNotEmpty(proposalNumber.trim())) {
            considerProposalNumberInd = true;
            criteria.addEqualTo(ArPropertyConstants.ReferralToCollectionsFields.PROPOSAL_NUMBER, proposalNumber);
        }

        if (ObjectUtils.isNotNull(invoiceNumber) && StringUtils.isNotEmpty(invoiceNumber.trim())) {
            considerinvoiceNumberInd = true;
            criteria.addEqualTo(ArPropertyConstants.ReferralToCollectionsFields.INVOICE_NUMBER, invoiceNumber);
        }

        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotEmpty(accountNumber.trim())) {
            considerAccountNumberInd = true;
            criteria.addEqualTo(ArPropertyConstants.ReferralToCollectionsFields.ACCOUNT_NUMBER, accountNumber);
        }

        // filter by criteria
        Collection<ReferralToCollectionsDocument> referralToCollectionsDocs = referralToCollectionsDao.getRefToCollDocsByCriteria(criteria);
        referralToCollectionsDocs = this.populateWorkflowHeaders(referralToCollectionsDocs);

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
                        collectorCriteria = new Criteria();
                        collectorCriteria.addIn(KFSPropertyConstants.PRINCIPAL_ID, collectorList);
                        List<String> customerNumbers = customerCollectorDao.retrieveCustomerNmbersByCriteria(collectorCriteria);
                        referralToCollectionsDocs = this.filterDocsAccordingToCustomerNumbers(referralToCollectionsDocs, customerNumbers);
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
        String accountNum = null;
        if (ObjectUtils.isNotNull(referralToCollectionsDocs) && CollectionUtils.isNotEmpty(referralToCollectionsDocs)) {
            for (ReferralToCollectionsDocument refToColDoc : referralToCollectionsDocs) {
                if (!isPdf) {
                    ReferralToCollectionsReport refReport = new ReferralToCollectionsReport();
                    refReport = this.convertReferralToCollectionsDocumentToReport(refToColDoc, refReport);
                    displayList.add(refReport);
                }
                else {
                    List<ReferralToCollectionsReport> refList = this.prepareRefToCollListFromReferralToCollectionsDocument(refToColDoc);
                    displayList.addAll(refList);
                }
            }
        }
        return displayList;
    }

    /**
     * This method filters invoices according to collectors
     *
     * @param contractsGrantsInvoiceDocuments
     * @param customerNumbers
     * @return Returns the list of ContractsGrantsInvoiceDocument.
     */
    private Collection<ReferralToCollectionsDocument> filterDocsAccordingToCustomerNumbers(Collection<ReferralToCollectionsDocument> refToCollDocs, List<String> customerNumbers) {
        List<ReferralToCollectionsDocument> filteredDocs = new ArrayList<ReferralToCollectionsDocument>();
        if (ObjectUtils.isNotNull(refToCollDocs) && CollectionUtils.isNotEmpty(refToCollDocs)) {
            for (ReferralToCollectionsDocument refDoc : refToCollDocs) {
                if (isCustomerAvailableInList(refDoc.getCustomerNumber(), customerNumbers)) {
                    filteredDocs.add(refDoc);
                }
            }
        }
        return filteredDocs;
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
     * This method prepares the ReferralToCollectionsReport from ReferralToCollectionsDoc.
     *
     * @param referralToCollectionsDocument
     * @return Returns the Object of ReferralToCollectionsReport.
     */
    private ReferralToCollectionsReport convertReferralToCollectionsDocumentToReport(ReferralToCollectionsDocument referralToCollectionsDocument, ReferralToCollectionsReport referralToCollectionsReport) {
        referralToCollectionsReport.setDocumentNumber(referralToCollectionsDocument.getDocumentNumber());
        referralToCollectionsReport.setDocumentDate(referralToCollectionsDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate());
        referralToCollectionsReport.setAgencyNumber(referralToCollectionsDocument.getAgencyNumber());
        referralToCollectionsReport.setAgencyName(referralToCollectionsDocument.getAgencyFullName());
        referralToCollectionsReport.setCustomerNumber(referralToCollectionsDocument.getCustomerNumber());
        String customerType = referralToCollectionsDocument.getCustomerTypeCode();

        // set CustomerType.
        if (ObjectUtils.isNotNull(customerType)) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(ArPropertyConstants.CustomerTypeFields.CUSTOMER_TYPE_CODE, customerType);
            CustomerType customerTypeObj = businessObjectService.findByPrimaryKey(CustomerType.class, map);
            if (ObjectUtils.isNotNull(customerTypeObj)) {
                referralToCollectionsReport.setCustomerType(customerTypeObj.getCustomerTypeDescription());
            }
        }

        // Set ReferredType.
        String referredType = referralToCollectionsDocument.getReferralTypeCode();
        String referredTypeString = "";

        // Set Referral Type
        if (ObjectUtils.isNotNull(referredType)) {
            referredTypeString = this.retrieveReferralType(referredType);
        }

        referralToCollectionsReport.setReferralType(referredTypeString);
        referralToCollectionsReport.setReferredTo(referredTypeString);
        String statusCode = referralToCollectionsDocument.getCollectionStatusCode();

        // Set collection status
        if (ObjectUtils.isNotNull(statusCode)) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(ArPropertyConstants.CollectionStatusFields.COLLECTION_STATUS_CODE, statusCode);
            CollectionStatus collectionStatus = businessObjectService.findByPrimaryKey(CollectionStatus.class, map);
            if (ObjectUtils.isNotNull(collectionStatus)) {
                referralToCollectionsReport.setCollectionStatus(collectionStatus.getStatusDescription());
            }
        }
        return referralToCollectionsReport;
    }

    /**
     * This method prepares the List of ReferralToCollectionDetails from ReferralToCollectionsDoc.
     *
     * @param referralToCollectionsDocument
     * @return Returns the list of ReferralToCollectionsDetails.
     */
    private List<ReferralToCollectionsReport> prepareRefToCollListFromReferralToCollectionsDocument(ReferralToCollectionsDocument referralToCollectionsDocument) {
        List<ReferralToCollectionsReport> reportList = new ArrayList<ReferralToCollectionsReport>();
        List<ReferralToCollectionsDetail> reCollectionsDocumentDetails = referralToCollectionsDocument.getReferralToCollectionsDetails();
        if (ObjectUtils.isNotNull(reCollectionsDocumentDetails) && CollectionUtils.isNotEmpty(reCollectionsDocumentDetails)) {
            for (ReferralToCollectionsDetail refDetail : reCollectionsDocumentDetails) {

                // check if reftoColldetail fulfill the all search criterias.
                if (isValidReferralToCollectionsDetail(refDetail)) {
                    ReferralToCollectionsReport referralToCollectionsReport = new ReferralToCollectionsReport();
                    referralToCollectionsReport.setDocumentNumber(referralToCollectionsDocument.getDocumentNumber());
                    referralToCollectionsReport.setDocumentDate(referralToCollectionsDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate());
                    referralToCollectionsReport.setAgencyNumber(referralToCollectionsDocument.getAgencyNumber());
                    referralToCollectionsReport.setAgencyName(referralToCollectionsDocument.getAgencyFullName());
                    referralToCollectionsReport.setCustomerNumber(referralToCollectionsDocument.getCustomerNumber());
                    String referredType = referralToCollectionsDocument.getReferralTypeCode();
                    String referredTypeString = "";

                    // Set Referral Type
                    if (ObjectUtils.isNotNull(referredType)) {
                        referredTypeString = this.retrieveReferralType(referredType);
                    }
                    referralToCollectionsReport.setReferralType(referredTypeString);
                    referralToCollectionsReport.setReferredTo(referredTypeString);

                    referralToCollectionsReport.setCollectionStatus(referralToCollectionsDocument.getCollectionStatusCode());
                    referralToCollectionsReport.setProposalNumber(refDetail.getProposalNumber());
                    referralToCollectionsReport.setAccountNumber(refDetail.getAccountNumber());
                    referralToCollectionsReport.setInvoiceNumber(refDetail.getInvoiceNumber());
                    referralToCollectionsReport.setBillingDate(refDetail.getBillingDate());
                    referralToCollectionsReport.setInvoiceAmount(refDetail.getInvoiceTotal().bigDecimalValue());
                    referralToCollectionsReport.setOpenAmount(refDetail.getInvoiceBalance().bigDecimalValue());

                    String finalDisposition = refDetail.getFinalDispositionCode();
                    String finalDispositionString = "";

                    // set final disposition
                    if (ObjectUtils.isNotNull(finalDisposition)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put(ArPropertyConstants.FinalDispositionFields.FINAL_DISPOSITION_CODE, finalDisposition);
                        FinalDisposition finalDispositionObj = businessObjectService.findByPrimaryKey(FinalDisposition.class, map);
                        if (ObjectUtils.isNotNull(finalDispositionObj)) {
                            referralToCollectionsReport.setFinalDisposition(finalDispositionObj.getDispositionDescription());
                        }
                    }
                    reportList.add(referralToCollectionsReport);
                }
            }
        }
        return reportList;
    }

    /**
     * This method is used to generate workflow headers for ReferralToCollectionsDocument.
     *
     * @param refDocs
     * @return Returns the collections of ReferralToCollectionsDocs with their workflow headers.
     */
    private Collection<ReferralToCollectionsDocument> populateWorkflowHeaders(Collection<ReferralToCollectionsDocument> refDocs) {

        // make a list of necessary workflow docs to retrieve
        List<String> documentHeaderIds = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(refDocs)) {
            for (ReferralToCollectionsDocument refDoc : refDocs) {
                documentHeaderIds.add(refDoc.getDocumentNumber());
            }
        }

        // get all of our docs with full workflow headers
        Collection<ReferralToCollectionsDocument> docs = new ArrayList<ReferralToCollectionsDocument>();
        if (CollectionUtils.isNotEmpty(documentHeaderIds)) {
            try {
                for (Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(ReferralToCollectionsDocument.class, documentHeaderIds)) {
                    docs.add((ReferralToCollectionsDocument) doc );
                }
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("Unable to retrieve Referral To Collections Documents", e);
            }
        }
        return docs;
    }

    /**
     * This method is used to filter ReferralToCollectionsDetails according to search criteria.
     *
     * @param refDetail
     * @return Returns the flag true if doc specifies the search criterias.
     */

    private boolean isValidReferralToCollectionsDetail(ReferralToCollectionsDetail refDetail) {

        boolean valid = true;
        boolean isValidPN = true;
        boolean isValidIN = true;
        boolean isValidAN = true;

        if (considerProposalNumberInd) {
            isValidPN = refDetail.getProposalNumber().toString().equals(proposalNumber);
        }

        if (considerinvoiceNumberInd) {
            isValidIN = refDetail.getInvoiceNumber().toString().equals(invoiceNumber);
        }

        if (considerAccountNumberInd) {
            isValidAN = refDetail.getAccountNumber().toString().equals(accountNumber);
        }

        valid = isValidPN && isValidIN && isValidAN;

        return valid;
    }

    /**
     * Retrieve the referral typde desc from the refTypeCode
     *
     * @param refTypeCode Primary key for the ReferralType Business Object.
     * @return Returns the description for the referraltypecode.
     */
    private String retrieveReferralType(String refTypeCode) {
        String refTypeDesc = null;
        Map<String, String> map = new HashMap<String, String>();
        map.put(ArPropertyConstants.ReferralTypeFields.REFERRAL_TYPE_CODE, refTypeCode);
        ReferralType refTtpe = businessObjectService.findByPrimaryKey(ReferralType.class, map);
        if (ObjectUtils.isNotNull(refTtpe)) {
            refTypeDesc = refTtpe.getDescription();
        }
        return refTypeDesc;
    }
}

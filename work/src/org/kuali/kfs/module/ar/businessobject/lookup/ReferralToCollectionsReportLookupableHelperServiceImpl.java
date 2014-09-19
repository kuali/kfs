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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionStatus;
import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.module.ar.businessobject.FinalDisposition;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsReport;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
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
 * Defines a lookupable helper service class for Referral To Collections Report.
 */
public class ReferralToCollectionsReportLookupableHelperServiceImpl extends AccountsReceivableLookupableHelperServiceImplBase {
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected PersonService personService;

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
        results = filterRecordsForReferralToCollections(fieldValues, true);
        return new CollectionIncomplete<ReferralToCollectionsReport>(results, (long) results.size());
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
        // MJM get resultTable populated here
        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Object aDisplayList : displayList) {
            BusinessObject element = (BusinessObject) aDisplayList;

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);
            String returnUrl = "";
            String actionUrls = "";

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

                    // Add url when property is documentNumber
                    ReferralToCollectionsReport bo = (ReferralToCollectionsReport) element;
                    if (col.getPropertyName().equals(KFSPropertyConstants.DOCUMENT_NUMBER)) {
                        String url = contractsGrantsReportHelperService.getDocSearchUrl(propValue);

                        Map<String, String> fieldList = new HashMap<String, String>();
                        fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                        col.setColumnAnchor(a);
                    } else if (StringUtils.isNotBlank(propValue)) {
                        col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
                    }
                }

                ResultRow row = new ResultRow(columns, returnUrl, actionUrls);
                if (element instanceof PersistableBusinessObject) {
                    row.setObjectId(((PersistableBusinessObject) element).getObjectId());
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
        return displayList;
    }

    /**
     * Performs the lookup of ReferralToCollections documents and details for the sake of the report
     * @param lookupFormFields the lookup form fields
     * @param isPdf whether a pdf is being generated or not
     * @return a list of matching ReferalToCollectionsReport records
     */
    protected List<ReferralToCollectionsReport> filterRecordsForReferralToCollections(Map lookupFormFields, boolean isPdf) {
        List<ReferralToCollectionsReport> displayList = new ArrayList<>();

        Map<String,String> fieldValues = new HashMap<>();

        String collectorPrincName = (String)lookupFormFields.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);
        String collector = (String)lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.COLLECTOR);
        String proposalNumber = (String)lookupFormFields.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String agencyNumber = (String)lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.AGENCY_NUMBER);
        String invoiceNumber = (String)lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.INVOICE_NUMBER);
        String accountNumber = (String)lookupFormFields.get(ArPropertyConstants.ReferralToCollectionsReportFields.ACCOUNT_NUMBER);

        if (!StringUtils.isBlank(agencyNumber)) {
            fieldValues.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber);
        }

        // considering final docs
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, StringUtils.join(getFinancialSystemDocumentService().getSuccessfulDocumentStatuses(),"|"));

        if (!StringUtils.isBlank(proposalNumber)) {
            fieldValues.put(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TO_COLLECTIONS_DETAILS+"."+KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }

        if (!StringUtils.isBlank(invoiceNumber)) {
            fieldValues.put(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TO_COLLECTIONS_DETAILS+"."+ArPropertyConstants.ReferralToCollectionsReportFields.INVOICE_NUMBER, invoiceNumber);
        }

        if (!StringUtils.isBlank(accountNumber)) {
            fieldValues.put(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TO_COLLECTIONS_DETAILS+"."+KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }

        // filter by criteria
        Collection<ReferralToCollectionsDocument> referralToCollectionsDocs = getLookupService().findCollectionBySearchHelper(ReferralToCollectionsDocument.class, fieldValues, true);

        // filter by collector
        if (!StringUtils.isBlank(collectorPrincName)) {
            Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
            if (ObjectUtils.isNotNull(collUser)) {
                collector = collUser.getPrincipalId();
                if (!StringUtils.isBlank(collector)) {
                    filterRecordsForCollector(collector, referralToCollectionsDocs);
                }
                else {
                    return displayList;
                }
            }
            else {
                return displayList;
            }
        }

        // filter for user requesting the report
        Person user = GlobalVariables.getUserSession().getPerson();
        filterRecordsForCollector(user.getPrincipalId(), referralToCollectionsDocs);

        // find by agency number
        String accountNum = null;
        if (!CollectionUtils.isEmpty(referralToCollectionsDocs)) {
            for (ReferralToCollectionsDocument refToColDoc : referralToCollectionsDocs) {
                if (!isPdf) {
                    ReferralToCollectionsReport refReport = convertReferralToCollectionsDocumentToReport(refToColDoc);
                    displayList.add(refReport);
                }
                else {
                    List<ReferralToCollectionsReport> refList = prepareRefToCollListFromReferralToCollectionsDocument(refToColDoc, proposalNumber, invoiceNumber, accountNumber);
                    displayList.addAll(refList);
                }
            }
        }
        return displayList;
    }

    /**
     * Removes any documents from the sourceCollectionDocuments that have customers that don't match the collector
     * based on the role qualifiers for the collector assigned to the CGB Collector role.  Made public to make unit tests happy
     * @param collector String principalId for the collector used to match against customers and filter the documents
     * @param sourceCollectionDocuments Collection of documents to filter
     */
    public void filterRecordsForCollector(String collector, Collection<ReferralToCollectionsDocument> sourceCollectionDocuments) {
        RoleService roleService = KimApiServiceLocator.getRoleService();

        for (Iterator<ReferralToCollectionsDocument> iter = sourceCollectionDocuments.iterator(); iter.hasNext();) {
            ReferralToCollectionsDocument refDoc = iter.next();

            List<String> roleIds = new ArrayList<String>();
            Map<String, String> qualification = new HashMap<String, String>(3);

            String customerName = refDoc.getCustomerName();
            if (StringUtils.isNotEmpty(customerName)) {
                qualification.put(ArKimAttributes.CUSTOMER_NAME, customerName);
            }

            roleIds.add(roleService.getRoleIdByNamespaceCodeAndName(ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR));
            if (!roleService.principalHasRole(collector, roleIds, qualification)) {
                iter.remove();
            }
        }

    }

    /**
     * This method prepares the ReferralToCollectionsReport from ReferralToCollectionsDoc.
     *
     * @param referralToCollectionsDocument
     * @return Returns the Object of ReferralToCollectionsReport.
     */
    protected ReferralToCollectionsReport convertReferralToCollectionsDocumentToReport(ReferralToCollectionsDocument referralToCollectionsDocument) {
        final ReferralToCollectionsReport referralToCollectionsReport = populateReport(referralToCollectionsDocument);
        return referralToCollectionsReport;
    }

    /**
     * This method prepares the List of ReferralToCollectionDetails from ReferralToCollectionsDoc.
     *
     * @param referralToCollectionsDocument
     * @return Returns the list of ReferralToCollectionsDetails.
     */
    protected List<ReferralToCollectionsReport> prepareRefToCollListFromReferralToCollectionsDocument(ReferralToCollectionsDocument referralToCollectionsDocument, String proposalNumber, String invoiceNumber, String accountNumber) {
        List<ReferralToCollectionsReport> reportList = new ArrayList<>();
        Collection<ReferralToCollectionsDetail> reCollectionsDocumentDetails = lookupReferralToCollectionsDetails(referralToCollectionsDocument.getDocumentNumber(), proposalNumber, invoiceNumber, accountNumber);
        if (!CollectionUtils.isEmpty(reCollectionsDocumentDetails)) {
            for (ReferralToCollectionsDetail refDetail : reCollectionsDocumentDetails) {
                // check if reftoColldetail fulfill the all search criterias.
                ReferralToCollectionsReport referralToCollectionsReport = populateReport(referralToCollectionsDocument);

                referralToCollectionsReport.setProposalNumber(refDetail.getProposalNumber());
                referralToCollectionsReport.setAccountNumber(refDetail.getAccountNumber());
                referralToCollectionsReport.setInvoiceNumber(refDetail.getInvoiceNumber());
                referralToCollectionsReport.setBillingDate(refDetail.getBillingDate());
                referralToCollectionsReport.setInvoiceAmount(refDetail.getInvoiceTotal().bigDecimalValue());
                referralToCollectionsReport.setOpenAmount(refDetail.getInvoiceBalance().bigDecimalValue());

                if (!StringUtils.isBlank(refDetail.getFinalDispositionCode())) {
                    referralToCollectionsReport.setFinalDisposition(retrieveFinalDispositionDescription(refDetail.getFinalDispositionCode()));
                }
                reportList.add(referralToCollectionsReport);
            }
        }
        return reportList;
    }

    /**
     * Returns the details which match the given criteria
     * @param documentNumber the document number to find details on
     * @param proposalNumber the proposal number to look up
     * @param invoiceNumber the invoice number to look up
     * @param accountNumber the account number to look up
     * @return a Collection of the ReferralToCollectionsDetails
     */
    protected Collection<ReferralToCollectionsDetail> lookupReferralToCollectionsDetails(String documentNumber, String proposalNumber, String invoiceNumber, String accountNumber) {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        if (!StringUtils.isBlank(proposalNumber)) {
            fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (!StringUtils.isBlank(invoiceNumber)) {
            fieldValues.put(ArPropertyConstants.ReferralToCollectionsReportFields.INVOICE_NUMBER, invoiceNumber);
        }
        if (!StringUtils.isBlank(accountNumber)) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }
        final Collection<ReferralToCollectionsDetail> matchingDetails = getLookupService().findCollectionBySearchHelper(ReferralToCollectionsDetail.class, fieldValues, true);
        return matchingDetails;
    }

    /**
     * Creates a ReferralToCollectionsReport object and populates it with values from ReferralToCollectionsDocument
     * @param referralToCollectionsDocument the document to populate the report with
     * @return the generated ReferralToCollectionsReport
     */
    protected ReferralToCollectionsReport populateReport(ReferralToCollectionsDocument referralToCollectionsDocument) {
        ReferralToCollectionsReport referralToCollectionsReport = new ReferralToCollectionsReport();
        referralToCollectionsReport.setDocumentNumber(referralToCollectionsDocument.getDocumentNumber());
        referralToCollectionsReport.setDocumentDate(referralToCollectionsDocument.getFinancialSystemDocumentHeader().getWorkflowCreateDate());
        referralToCollectionsReport.setAgencyNumber(referralToCollectionsDocument.getAgencyNumber());
        referralToCollectionsReport.setAgencyName(referralToCollectionsDocument.getAgencyFullName());
        referralToCollectionsReport.setCustomerNumber(referralToCollectionsDocument.getCustomerNumber());

        // Set Referral Type
        if (!StringUtils.isBlank(referralToCollectionsDocument.getReferralTypeCode())) {
            final String referredTypeDescription = retrieveReferralTypeDescription(referralToCollectionsDocument.getReferralTypeCode());
            referralToCollectionsReport.setReferralType(referredTypeDescription);
            referralToCollectionsReport.setReferredTo(referredTypeDescription);
        }

        if (!StringUtils.isBlank(referralToCollectionsDocument.getCustomerTypeCode())) {
            referralToCollectionsReport.setCustomerType(retrieveCustomerTypeDescription(referralToCollectionsDocument.getCustomerTypeCode()));
        }

        if (!StringUtils.isBlank(referralToCollectionsDocument.getCollectionStatusCode())) {
            referralToCollectionsReport.setCollectionStatus(retrieveCollectionStatusDescription(referralToCollectionsDocument.getCollectionStatusCode()));
        }

        return referralToCollectionsReport;
    }

    /**
     * Retrieve the referral typde desc from the refTypeCode
     *
     * @param refTypeCode Primary key for the ReferralType Business Object.
     * @return Returns the description for the referraltypecode.
     */
    protected String retrieveReferralTypeDescription(String refTypeCode) {
        ReferralType refTtpe = businessObjectService.findBySinglePrimaryKey(ReferralType.class, refTypeCode);
        if (!ObjectUtils.isNull(refTtpe)) {
            return refTtpe.getDescription();
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Returns the description of a given customer type code
     * @param customerTypeCode the code to find a description for
     * @return the description, or an empty string if the customer type could not be found
     */
    protected String retrieveCustomerTypeDescription(String customerTypeCode) {
        CustomerType customerType = businessObjectService.findBySinglePrimaryKey(CustomerType.class, customerTypeCode);
        if (!ObjectUtils.isNull(customerType)) {
            return customerType.getCustomerTypeDescription();
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Returns the description of a given collection status code
     * @param statusCode the code of a collection status
     * @return the description of the collection status, or an empty string if the collection status could not be found
     */
    protected String retrieveCollectionStatusDescription(String statusCode) {
        CollectionStatus collectionStatus = businessObjectService.findBySinglePrimaryKey(CollectionStatus.class, statusCode);
        if (!ObjectUtils.isNull(collectionStatus)) {
            return collectionStatus.getStatusDescription();
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Returns the description of a given final disposition code
     * @param finalDispositionCode the code of the final disposition to find a description for
     * @return the description of the final disposition, or an empty string if the final disposition could not be found
     */
    protected String retrieveFinalDispositionDescription(String finalDispositionCode) {
        FinalDisposition finalDisposition = businessObjectService.findBySinglePrimaryKey(FinalDisposition.class, finalDispositionCode);
        if (!ObjectUtils.isNull(finalDisposition)) {
            return finalDisposition.getDispositionDescription();
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * get role qualifiers for collector
     * billing chart/org, processing chart/org, first/last letters for customer name
     *
     * @param principalId
     * @param namespaceCode
     * @return
     */
    protected Map<String, String> getRoleQualifiersForUser(String principalId, String namespaceCode) {
        Map<String, String> roleQualifiers = new HashMap<>();
        if (StringUtils.isBlank(principalId)) {
            return null;
        }
        Map<String, String> qualification = new HashMap<>(4);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, KFSConstants.Booleans.TRUE);
        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        RoleService roleService = KimApiServiceLocator.getRoleService();
        List<Map<String, String>> qualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(principalId, ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualification);
        if ((qualifiers != null) && !qualifiers.isEmpty()) {
            for (Map<String, String> qualifier: qualifiers) {
                String startingLetter = qualifier.get(ArKimAttributes.CUSTOMER_NAME_STARTING_LETTER);
                String endingLetter = qualifier.get(ArKimAttributes.CUSTOMER_NAME_ENDING_LETTER);
                if (!StringUtils.isBlank(startingLetter) && !StringUtils.isBlank(endingLetter)) {
                    roleQualifiers.put(ArKimAttributes.CUSTOMER_NAME_STARTING_LETTER, startingLetter);
                    roleQualifiers.put(ArKimAttributes.CUSTOMER_NAME_ENDING_LETTER, endingLetter);
                    return roleQualifiers;
                }
            }
        }
        return null;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}
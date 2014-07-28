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

import org.joda.time.DateTime;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for LOC Draw Details Report.
 */
public class ContractsGrantsLOCReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected WorkflowDocumentService workflowDocumentService;

    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        String reportType = (String) lookupFormFields.get("reportType");

        if (reportType.equals("AmountsNotDrawn")) {
            return performDrawDetailsReportLookup(lookupForm, resultTable, bounded);
        } else {
            return performAmountsNotDrawnReportLookup(lookupForm, resultTable, bounded);
        }
    }

    private Collection performDrawDetailsReportLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsLOCReport> displayList = new ArrayList<ContractsGrantsLOCReport>();
        Collection<ContractsGrantsLetterOfCreditReviewDocument> cgLOCReviewDocs = findFinalDocuments(ContractsGrantsLetterOfCreditReviewDocument.class);

        for (ContractsGrantsLetterOfCreditReviewDocument cgLOCReviewDoc : cgLOCReviewDocs) {
            List<ContractsGrantsLetterOfCreditReviewDetail> headerReviewDetails = cgLOCReviewDoc.getHeaderReviewDetails();
            List<ContractsGrantsLetterOfCreditReviewDetail> accountReviewDetails = cgLOCReviewDoc.getAccountReviewDetails();
            if (accountReviewDetails.size() > 0) {

                KualiDecimal totalAmountAvailableToDraw = KualiDecimal.ZERO;
                KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                KualiDecimal totalFundsNotDrawn = KualiDecimal.ZERO;

                ContractsGrantsLOCReport cgLOCDrawDetailsReport = new ContractsGrantsLOCReport();

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : accountReviewDetails) {
                    KualiDecimal claimOnCashBalance = ObjectUtils.isNull(accountReviewDetailEntry.getClaimOnCashBalance()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getClaimOnCashBalance();
                    // previousDraw should be sum of amountToDraw?
                    KualiDecimal previousDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountToDraw();
                    KualiDecimal fundsNotDrawn = ObjectUtils.isNull(accountReviewDetailEntry.getFundsNotDrawn()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getFundsNotDrawn();

                    totalClaimOnCashBalance = totalClaimOnCashBalance.add(claimOnCashBalance);
                    totalAmountToDraw = totalAmountToDraw.add(previousDraw);
                    totalFundsNotDrawn = totalFundsNotDrawn.add(fundsNotDrawn);
                }

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : headerReviewDetails) {
                    KualiDecimal amountAvailableToDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountAvailableToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountAvailableToDraw();
                    totalAmountAvailableToDraw = totalAmountAvailableToDraw.add(amountAvailableToDraw);
                }

                cgLOCDrawDetailsReport.setDocumentNumber(cgLOCReviewDoc.getDocumentNumber());
                cgLOCDrawDetailsReport.setLetterOfCreditFundCode(cgLOCReviewDoc.getLetterOfCreditFundCode());
                cgLOCDrawDetailsReport.setLetterOfCreditFundGroupCode(cgLOCReviewDoc.getLetterOfCreditFundGroupCode());

                final DateTime dateCreated = getDocumentDateCreated(cgLOCReviewDoc.getDocumentNumber());
                if (ObjectUtils.isNotNull(dateCreated)) {
                    cgLOCDrawDetailsReport.setLetterOfCreditReviewCreateDate(new java.sql.Date(dateCreated.getMillis()));
                }
                cgLOCDrawDetailsReport.setAmountAvailableToDraw(totalAmountAvailableToDraw);
                cgLOCDrawDetailsReport.setClaimOnCashBalance(totalClaimOnCashBalance);
                cgLOCDrawDetailsReport.setAmountToDraw(totalAmountToDraw);
                cgLOCDrawDetailsReport.setFundsNotDrawn(totalFundsNotDrawn);

                if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgLOCDrawDetailsReport, "ContractsGrantsLOCReport")) {
                    displayList.add(cgLOCDrawDetailsReport);
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    private Collection performAmountsNotDrawnReportLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsLOCReport> displayList = new ArrayList<ContractsGrantsLOCReport>();
        Collection<ContractsGrantsLetterOfCreditReviewDocument> cgLOCReviewDocs = findFinalDocuments(ContractsGrantsLetterOfCreditReviewDocument.class);

        for (ContractsGrantsLetterOfCreditReviewDocument cgLOCReviewDoc : cgLOCReviewDocs) {
            List<ContractsGrantsLetterOfCreditReviewDetail> headerReviewDetails = cgLOCReviewDoc.getHeaderReviewDetails();
            List<ContractsGrantsLetterOfCreditReviewDetail> accountReviewDetails = cgLOCReviewDoc.getAccountReviewDetails();
            if (accountReviewDetails.size() > 0) {

                KualiDecimal totalAmountAvailableToDraw = KualiDecimal.ZERO;
                KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                KualiDecimal totalFundsNotDrawn = KualiDecimal.ZERO;

                ContractsGrantsLOCReport cgLOCAmountNotDrawnReport = new ContractsGrantsLOCReport();

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : accountReviewDetails) {
                    KualiDecimal claimOnCashBalance = ObjectUtils.isNull(accountReviewDetailEntry.getClaimOnCashBalance()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getClaimOnCashBalance();
                    // PreviousDraw should be sum of amountToDraw?
                    KualiDecimal previousDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountToDraw();
                    KualiDecimal fundsNotDrawn = ObjectUtils.isNull(accountReviewDetailEntry.getFundsNotDrawn()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getFundsNotDrawn();

                    totalClaimOnCashBalance = totalClaimOnCashBalance.add(claimOnCashBalance);
                    totalAmountToDraw = totalAmountToDraw.add(previousDraw);
                    totalFundsNotDrawn = totalFundsNotDrawn.add(fundsNotDrawn);
                }

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : headerReviewDetails) {
                    KualiDecimal amountAvailableToDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountAvailableToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountAvailableToDraw();
                    totalAmountAvailableToDraw = totalAmountAvailableToDraw.add(amountAvailableToDraw);
                }

                cgLOCAmountNotDrawnReport.setDocumentNumber(cgLOCReviewDoc.getDocumentNumber());
                cgLOCAmountNotDrawnReport.setLetterOfCreditFundCode(cgLOCReviewDoc.getLetterOfCreditFundCode());
                cgLOCAmountNotDrawnReport.setLetterOfCreditFundGroupCode(cgLOCReviewDoc.getLetterOfCreditFundGroupCode());

                final DateTime dateCreated = getDocumentDateCreated(cgLOCReviewDoc.getDocumentNumber());
                if (ObjectUtils.isNotNull(dateCreated)) {
                    cgLOCAmountNotDrawnReport.setLetterOfCreditReviewCreateDate(new java.sql.Date(dateCreated.getMillis()));
                }
                cgLOCAmountNotDrawnReport.setAmountAvailableToDraw(totalAmountAvailableToDraw);
                cgLOCAmountNotDrawnReport.setClaimOnCashBalance(totalClaimOnCashBalance);
                cgLOCAmountNotDrawnReport.setAmountToDraw(totalAmountToDraw);
                cgLOCAmountNotDrawnReport.setFundsNotDrawn(totalFundsNotDrawn);

                // Only adding entries with funds not drawn amount greater than zero
                if (totalFundsNotDrawn.isGreaterThan(KualiDecimal.ZERO)) {
                    if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgLOCAmountNotDrawnReport, "ContractsGrantsLOCAmountsNotDrawnReport")) {
                        displayList.add(cgLOCAmountNotDrawnReport);
                    }
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    /**
     * Lookup all final documents of the given document class
     * @param documentClass the class of the document to look up
     * @return a Collection of documents, without WorkflowDocument's embedded in the documentHeader
     */
    protected <D extends Document> Collection<D> findFinalDocuments(Class<D> documentClass) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, DocumentStatus.FINAL.getCode());
        return getBusinessObjectService().findMatching(documentClass, fieldValues);
    }

    /**
     * Returns the creation date of the given workflow document
     * @param documentNumber the document number to look up the creation date for
     * @return the creation date
     */
    protected DateTime getDocumentDateCreated(String documentNumber) {
        final org.kuali.rice.kew.api.document.Document wd = getWorkflowDocumentService().getDocument(documentNumber);
        return wd.getDateCreated();
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
}

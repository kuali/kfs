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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for LOC Draw Details Report.
 */
public class ContractsGrantsLOCReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected FinancialSystemDocumentService financialSystemDocumentService;

    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        final String reportType = (String)lookupFormFields.remove("reportType");

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

                ContractsGrantsLOCReport cgLOCReport = new ContractsGrantsLOCReport();

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

                cgLOCReport.setDocumentNumber(cgLOCReviewDoc.getDocumentNumber());
                cgLOCReport.setLetterOfCreditFundCode(cgLOCReviewDoc.getLetterOfCreditFundCode());
                cgLOCReport.setLetterOfCreditFundGroupCode(cgLOCReviewDoc.getLetterOfCreditFundGroupCode());

                final Timestamp dateCreated = cgLOCReviewDoc.getFinancialSystemDocumentHeader().getWorkflowCreateDate();
                if (ObjectUtils.isNotNull(dateCreated)) {
                    cgLOCReport.setLetterOfCreditReviewCreateDate(new java.sql.Date(dateCreated.getTime()));
                }
                cgLOCReport.setAmountAvailableToDraw(totalAmountAvailableToDraw);
                cgLOCReport.setClaimOnCashBalance(totalClaimOnCashBalance);
                cgLOCReport.setAmountToDraw(totalAmountToDraw);
                cgLOCReport.setFundsNotDrawn(totalFundsNotDrawn);

                if (reportType.equals(ArConstants.LOCReportTypeFieldValues.AMOUNTS_NOT_DRAWN) && totalFundsNotDrawn.isGreaterThan(KualiDecimal.ZERO)) {
                    if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgLOCReport, ArConstants.CONTRACTS_GRANTS_LOC_REPORT)) {
                        displayList.add(cgLOCReport);
                    }
                } else if(ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgLOCReport, ArConstants.CONTRACTS_GRANTS_LOC_REPORT)) {
                    displayList.add(cgLOCReport);
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

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }
}

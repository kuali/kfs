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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCDrawDetailsReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for LOC Draw Details Report.
 */
public class ContractsGrantsLOCDrawDetailsReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    private static final Log LOG = LogFactory.getLog(ContractsGrantsLOCDrawDetailsReportLookupableHelperServiceImpl.class);

    private DocumentService documentService;

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

        Collection<ContractsGrantsLOCDrawDetailsReport> displayList = new ArrayList<ContractsGrantsLOCDrawDetailsReport>();
        Collection<ContractsGrantsLOCReviewDocument> cgLOCReviewDocs = businessObjectService.findAll(ContractsGrantsLOCReviewDocument.class);

        for (ContractsGrantsLOCReviewDocument cgLOCReviewDoc : cgLOCReviewDocs) {
            ContractsGrantsLOCReviewDocument cgLOCReviewDocWithHeader;
            // docs that have a problem to get documentHeader won't be on the report
            try {
                cgLOCReviewDocWithHeader = (ContractsGrantsLOCReviewDocument) documentService.getByDocumentHeaderId(cgLOCReviewDoc.getDocumentNumber());
            }
            catch (WorkflowException e) {
                LOG.debug("WorkflowException happened while retrives documentHeader");
                continue;
            }

            // if there is any problem to get workflowDocument, go to next doc
            WorkflowDocument workflowDocument = null;
            try {
                workflowDocument = cgLOCReviewDocWithHeader.getDocumentHeader().getWorkflowDocument();
            }
            catch (RuntimeException e) {
                LOG.debug(e + " happened" + " : transient workflowDocument is null");
                continue;
            }

            boolean isStatusFinal = false;
            // check status of document
            if (ObjectUtils.isNotNull(workflowDocument)) {
                isStatusFinal = workflowDocument.isFinal();
            }

            // if status of ContractsGrantsLOCReviewDocument is final or processed, go to next doc
            if (!isStatusFinal) {
                continue;
            }

            List<ContractsGrantsLOCReviewDetail> headerReviewDetails = cgLOCReviewDoc.getHeaderReviewDetails();
            List<ContractsGrantsLOCReviewDetail> accountReviewDetails = cgLOCReviewDoc.getAccountReviewDetails();
            if (accountReviewDetails.size() > 0) {

                KualiDecimal totalAmountAvailableToDraw = KualiDecimal.ZERO;
                KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                KualiDecimal totalFundsNotDrawn = KualiDecimal.ZERO;

                ContractsGrantsLOCDrawDetailsReport cgLOCDrawDetailsReport = new ContractsGrantsLOCDrawDetailsReport();

                for (ContractsGrantsLOCReviewDetail accountReviewDetailEntry : accountReviewDetails) {
                    KualiDecimal claimOnCashBalance = ObjectUtils.isNull(accountReviewDetailEntry.getClaimOnCashBalance()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getClaimOnCashBalance();
                    // previousDraw should be sum of amountToDraw?
                    KualiDecimal previousDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountToDraw();
                    KualiDecimal fundsNotDrawn = ObjectUtils.isNull(accountReviewDetailEntry.getFundsNotDrawn()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getFundsNotDrawn();

                    totalClaimOnCashBalance = totalClaimOnCashBalance.add(claimOnCashBalance);
                    totalAmountToDraw = totalAmountToDraw.add(previousDraw);
                    totalFundsNotDrawn = totalFundsNotDrawn.add(fundsNotDrawn);
                }

                for (ContractsGrantsLOCReviewDetail accountReviewDetailEntry : headerReviewDetails) {
                    KualiDecimal amountAvailableToDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountAvailableToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountAvailableToDraw();
                    totalAmountAvailableToDraw = totalAmountAvailableToDraw.add(amountAvailableToDraw);
                }

                cgLOCDrawDetailsReport.setDocumentNumber(cgLOCReviewDoc.getDocumentNumber());
                cgLOCDrawDetailsReport.setLetterOfCreditFundCode(cgLOCReviewDoc.getLetterOfCreditFundCode());
                cgLOCDrawDetailsReport.setLetterOfCreditFundGroupCode(cgLOCReviewDoc.getLetterOfCreditFundGroupCode());

                if (ObjectUtils.isNotNull(workflowDocument) && ObjectUtils.isNotNull(workflowDocument.getDateCreated())) {
                    cgLOCDrawDetailsReport.setLetterOfCreditReviewCreateDate((Date)(workflowDocument.getDateCreated().toDate()));
                }
                cgLOCDrawDetailsReport.setAmountAvailableToDraw(totalAmountAvailableToDraw);
                cgLOCDrawDetailsReport.setClaimOnCashBalance(totalClaimOnCashBalance);
                cgLOCDrawDetailsReport.setAmountToDraw(totalAmountToDraw);
                cgLOCDrawDetailsReport.setFundsNotDrawn(totalFundsNotDrawn);

                if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgLOCDrawDetailsReport, "ContractsGrantsLOCDrawDetailsReport")) {
                    displayList.add(cgLOCDrawDetailsReport);
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    /**
     * @return the documentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * @param documentService the documentService to set
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}

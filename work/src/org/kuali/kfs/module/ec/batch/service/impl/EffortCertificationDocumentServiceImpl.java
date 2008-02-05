/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.effort.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.service.LaborModuleService;
import org.kuali.kfs.util.MessageBuilder;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.service.EffortCertificationDocumentService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * To implement the services related to the effort certification document
 */
@Transactional
public class EffortCertificationDocumentServiceImpl implements EffortCertificationDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentServiceImpl.class);

    private LaborModuleService laborModuleService;
    private DocumentService documentService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#processApprovedEffortCertificationDocument(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    public void processApprovedEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument) {
        if (!effortCertificationDocument.getDocumentHeader().getWorkflowDocument().stateIsApproved()) {
            LOG.error("The given document has not beeen approved.");
            return;
        }
        //TODO: add logic here
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#createEffortCertificationDocument(org.kuali.module.effort.bo.EffortCertificationDocumentBuild)
     */
    @Logged
    public boolean createEffortCertificationDocument(EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        try {
            EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) documentService.getNewDocument(EffortCertificationDocument.class);

            // populate the fields of the docuemnt
            effortCertificationDocument.setUniversityFiscalYear(effortCertificationDocumentBuild.getUniversityFiscalYear());
            effortCertificationDocument.setEmplid(effortCertificationDocumentBuild.getEmplid());
            effortCertificationDocument.setEffortCertificationReportNumber(effortCertificationDocumentBuild.getEffortCertificationReportNumber());
            effortCertificationDocument.setEffortCertificationDocumentCode(effortCertificationDocumentBuild.getEffortCertificationDocumentCode());

            // populcate the detail line of the document
            List<EffortCertificationDetail> detailLines = effortCertificationDocument.getEffortCertificationDetailLines();
            List<EffortCertificationDetailBuild> detailLinesBuild = effortCertificationDocumentBuild.getEffortCertificationDetailLinesBuild();
            for (EffortCertificationDetailBuild detailLineBuild : detailLinesBuild) {
                detailLines.add(new EffortCertificationDetail(detailLineBuild));
            }

            // populate the document header of the document
            DocumentHeader documentHeader = effortCertificationDocument.getDocumentHeader();
            documentHeader.setFinancialDocumentDescription(effortCertificationDocumentBuild.getEmplid());
            documentHeader.setFinancialDocumentTotalAmount(EffortCertificationDocument.getDocumentTotalAmount(effortCertificationDocument));
            
            documentService.routeDocument(effortCertificationDocument, KFSConstants.EMPTY_STRING, null);
        }
        catch (WorkflowException we) {
            LOG.error(we);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#generateSalaryExpenseTransferDocument(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    @Logged
    public boolean generateSalaryExpenseTransferDocument(EffortCertificationDocument effortCertificationDocument) {
        String documentDescription = effortCertificationDocument.getEmplid();
        String explanation = MessageBuilder.getPropertyString(EffortKeyConstants.MESSAGE_CREATE_SET_DOCUMENT_DESCRIPTION);

        List<LaborLedgerExpenseTransferAccountingLine> sourceAccoutingLines = buildSourceAccountingLines(effortCertificationDocument);
        List<LaborLedgerExpenseTransferAccountingLine> targetAccoutingLines = buildTargetAccountingLines(effortCertificationDocument);
        
        if (sourceAccoutingLines.isEmpty() || targetAccoutingLines.isEmpty()) {
            return true;
        }
        
        try {
            laborModuleService.createSalaryExpenseTransferDocument(documentDescription, explanation, sourceAccoutingLines, targetAccoutingLines);
        }
        catch (WorkflowException e) {
            LOG.error(e);
            return false;
        }
        return true;
    }

    /**
     * build the source accounting lines for a salary expense transfer document from the given effort certification document
     * 
     * @param effortCertificationDocument the given effort certification document
     * @return the source accounting lines for a salary expense transfer document built from the given effort certification document
     */
    private List<LaborLedgerExpenseTransferAccountingLine> buildSourceAccountingLines(EffortCertificationDocument effortCertificationDocument) {
        List<EffortCertificationDetail> effortCertificationDetailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        List<LaborLedgerExpenseTransferAccountingLine> accountingLines = new ArrayList<LaborLedgerExpenseTransferAccountingLine>();
        
        int sequenceNumber = 0;
        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            boolean isQualified = this.getDifference(detailLine).isPositive();
            if (isQualified) {
                LaborLedgerExpenseTransferAccountingLine accountingLine = laborModuleService.createExpenseTransferSourceAccountingLine();
                accountingLine.setSequenceNumber(sequenceNumber);

                this.populateAccountingLine(effortCertificationDocument, detailLine, accountingLine);
                accountingLines.add(accountingLine);
            }
        }
        return accountingLines;
    }

    /**
     * build the target accounting lines for a salary expense transfer document from the given effort certification document
     * 
     * @param effortCertificationDocument the given effort certification document
     * @return the target accounting lines for a salary expense transfer document built from the given effort certification document
     */
    private List<LaborLedgerExpenseTransferAccountingLine> buildTargetAccountingLines(EffortCertificationDocument effortCertificationDocument) {
        List<EffortCertificationDetail> effortCertificationDetailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        List<LaborLedgerExpenseTransferAccountingLine> accountingLines = new ArrayList<LaborLedgerExpenseTransferAccountingLine>();

        int sequenceNumber = 0;
        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            boolean isQualified = this.getDifference(detailLine).isNegative();
            if (isQualified) {
                LaborLedgerExpenseTransferAccountingLine accountingLine = laborModuleService.createExpenseTransferTargetAccountingLine();
                accountingLine.setSequenceNumber(sequenceNumber);

                this.populateAccountingLine(effortCertificationDocument, detailLine, accountingLine);
                accountingLines.add(accountingLine);
            }
        }
        return accountingLines;
    }

    /**
     * populate an accounting line from the given detail line of the effort certification document
     * 
     * @param effortCertificationDocument the effort certification document containing the detail line
     * @param detailLine the given detail line
     * @param accountingLine the accounting line needed to be populated
     */
    private void populateAccountingLine(EffortCertificationDocument effortCertificationDocument, EffortCertificationDetail detailLine, LaborLedgerExpenseTransferAccountingLine accountingLine) {
        accountingLine.setChartOfAccountsCode(detailLine.getChartOfAccountsCode());
        accountingLine.setAccountNumber(detailLine.getAccountNumber());
        accountingLine.setSubAccountNumber(detailLine.getSubAccountNumber());

        accountingLine.setPostingYear(detailLine.getFinancialDocumentPostingYear());
        accountingLine.setFinancialObjectCode(detailLine.getFinancialObjectCode());
        accountingLine.setBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

        accountingLine.setAmount(this.getDifference(detailLine).abs());

        accountingLine.setFinancialSubObjectCode(KFSConstants.EMPTY_STRING);
        accountingLine.setProjectCode(KFSConstants.EMPTY_STRING);
        accountingLine.setOrganizationReferenceId(KFSConstants.EMPTY_STRING);

        accountingLine.setEmplid(effortCertificationDocument.getEmplid());
        accountingLine.setPositionNumber(detailLine.getPositionNumber());
        accountingLine.setPayrollTotalHours(BigDecimal.ZERO);

        EffortCertificationReportDefinition reportDefinition = effortCertificationDocument.getEffortCertificationReportDefinition();        
        accountingLine.setPayrollEndDateFiscalYear(reportDefinition.getUniversityFiscalYear());
        accountingLine.setPayrollEndDateFiscalPeriodCode(reportDefinition.getExpenseTransferFiscalPeriodCode());
    }

    /**
     * get the difference between the original amount and updated amount of the given detail line
     * 
     * @param detailLine the given detail line
     * @return the difference between the original amount and updated amount of the given detail line
     */
    private KualiDecimal getDifference(EffortCertificationDetail detailLine) {
        return detailLine.getEffortCertificationOriginalPayrollAmount().subtract(detailLine.getEffortCertificationPayrollAmount());
    }

    /**
     * Sets the laborModuleService attribute value.
     * 
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}

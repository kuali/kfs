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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteRecipient;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.MessageBuilder;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortConstants.DocumentRoutingLevelName;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rules.EffortCertificationDocumentRuleUtil;
import org.kuali.module.effort.service.EffortCertificationDocumentService;
import org.kuali.module.integration.bo.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.module.integration.service.ContractsAndGrantsModuleService;
import org.kuali.module.integration.service.LaborModuleService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * To implement the services related to the effort certification document
 */
@Transactional
public class EffortCertificationDocumentServiceImpl implements EffortCertificationDocumentService {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentServiceImpl.class);

    private LaborModuleService laborModuleService;
    public ContractsAndGrantsModuleService contractsAndGrantsModuleService;

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#processApprovedEffortCertificationDocument(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    public void processApprovedEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument) {
        if (!effortCertificationDocument.getDocumentHeader().getWorkflowDocument().stateIsApproved()) {
            LOG.debug("The given document has not beeen approved.");
            return;
        }

        // add logic here if any
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#createAndRouteEffortCertificationDocument(org.kuali.module.effort.bo.EffortCertificationDocumentBuild)
     */
    @Logged
    public boolean createAndRouteEffortCertificationDocument(EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        try {
            EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) documentService.getNewDocument(EffortCertificationDocument.class);
            this.populateEffortCertificationDocument(effortCertificationDocument, effortCertificationDocumentBuild);
            documentService.routeDocument(effortCertificationDocument, KFSConstants.EMPTY_STRING, null);
        }
        catch (WorkflowException we) {
            LOG.error(we);
            throw new RuntimeException(we);
        }

        return true;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#populateEffortCertificationDocument(org.kuali.module.effort.document.EffortCertificationDocument,
     *      org.kuali.module.effort.bo.EffortCertificationDocumentBuild)
     */
    @Logged
    public boolean populateEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument, EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        // populate the fields of the docuemnt
        effortCertificationDocument.setUniversityFiscalYear(effortCertificationDocumentBuild.getUniversityFiscalYear());
        effortCertificationDocument.setEmplid(effortCertificationDocumentBuild.getEmplid());
        effortCertificationDocument.setEffortCertificationReportNumber(effortCertificationDocumentBuild.getEffortCertificationReportNumber());
        effortCertificationDocument.setEffortCertificationDocumentCode(effortCertificationDocumentBuild.getEffortCertificationDocumentCode());

        // populcate the detail line of the document
        List<EffortCertificationDetail> detailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        detailLines.clear();

        List<EffortCertificationDetailBuild> detailLinesBuild = effortCertificationDocumentBuild.getEffortCertificationDetailLinesBuild();
        for (EffortCertificationDetailBuild detailLineBuild : detailLinesBuild) {
            detailLines.add(new EffortCertificationDetail(detailLineBuild));
        }

        // populate the document header of the document
        DocumentHeader documentHeader = effortCertificationDocument.getDocumentHeader();
        documentHeader.setFinancialDocumentDescription(effortCertificationDocumentBuild.getEmplid());
        documentHeader.setFinancialDocumentTotalAmount(EffortCertificationDocument.getDocumentTotalAmount(effortCertificationDocument));

        return true;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#resetEffortCertificationDetailLines(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    @Logged
    public void removeEffortCertificationDetailLines(EffortCertificationDocument effortCertificationDocument) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, effortCertificationDocument.getDocumentNumber());

        businessObjectService.deleteMatching(EffortCertificationDetail.class, fieldValues);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#generateSalaryExpenseTransferDocument(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    @Logged
    public boolean generateSalaryExpenseTransferDocument(EffortCertificationDocument effortCertificationDocument) {
        String documentDescription = effortCertificationDocument.getEmplid();
        String explanation = MessageBuilder.getPropertyString(EffortKeyConstants.MESSAGE_CREATE_SET_DOCUMENT_DESCRIPTION);

        List<LaborLedgerExpenseTransferAccountingLine> sourceAccoutingLines = this.buildSourceAccountingLines(effortCertificationDocument);
        List<LaborLedgerExpenseTransferAccountingLine> targetAccoutingLines = this.buildTargetAccountingLines(effortCertificationDocument);

        if (sourceAccoutingLines.isEmpty() || targetAccoutingLines.isEmpty()) {
            return true;
        }

        try {
            laborModuleService.createSalaryExpenseTransferDocument(documentDescription, explanation, sourceAccoutingLines, targetAccoutingLines);
        }
        catch (WorkflowException we) {
            LOG.error(we);
            throw new RuntimeException(we);
        }
        return true;
    }
    
    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentService#addRouteLooping(org.kuali.module.effort.document.EffortCertificationDocument)
     */
    @Logged
    public void addRouteLooping(EffortCertificationDocument effortCertificationDocument) {
        String routeLevelName = null;
        try {
            routeLevelName = effortCertificationDocument.getDocumentHeader().getWorkflowDocument().getDocRouteLevelName();
        }
        catch (WorkflowException we) {
            LOG.error(we);
            throw new RuntimeException(we);
        }

        List<EffortCertificationDetail> detailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        List<AdHocRouteRecipient> adHocRoutePersons = effortCertificationDocument.getAdHocRoutePersons();

        for (EffortCertificationDetail detailLine : detailLines) {
            boolean hasBeenChanged = EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromPersisted(detailLine);

            if (!hasBeenChanged) {
                continue;
            }

            Account account = detailLine.getAccount();
            String accountFiscalOfficerPersonUserId = account.getAccountFiscalOfficerUserPersonUserIdentifier();
            String actionRequestOfOfficer = this.getActionRequest(routeLevelName, DocumentRoutingLevelName.FISCAL_OFFICER);
            adHocRoutePersons.add(this.buildAdHocRouteRecipient(accountFiscalOfficerPersonUserId, actionRequestOfOfficer));

            String accountProjectDirectorPersonUserId = contractsAndGrantsModuleService.getProjectDirectorForAccount(account).getPersonUserIdentifier();
            String actionRequestOfDirector = this.getActionRequest(routeLevelName, DocumentRoutingLevelName.PROJECT_DIRECTOR);
            adHocRoutePersons.add(this.buildAdHocRouteRecipient(accountProjectDirectorPersonUserId, actionRequestOfDirector));
        }
    }

    /**
     * determine the action request according to the current route level and expected route level
     * 
     * @param routeLevelName the current route level
     * @param expectedRouteLevelName the expected route level
     * @return the action request determined from the current route level and expected route level
     */
    private String getActionRequest(String routeLevelName, String expectedRouteLevelName) {
        boolean isExpectedRouteLevel = StringUtils.equals(routeLevelName, expectedRouteLevelName);
        return isExpectedRouteLevel ? EdenConstants.ACTION_REQUEST_APPROVE_REQ : EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ;
    }

    /**
     * build an adhoc route recipient from the given person user id and action request
     * 
     * @param personUserId the given person user id
     * @param actionRequest the given action request
     * @return an adhoc route recipient built from the given information
     */
    private AdHocRouteRecipient buildAdHocRouteRecipient(String personUserId, String actionRequest) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(actionRequest);
        adHocRouteRecipient.setId(personUserId);

        return adHocRouteRecipient;
    }

    /**
     * build the source accounting lines for a salary expense transfer document from the given effort certification document. In the
     * holder, the first item is source accounting line list and the second the target accounting line list.
     * 
     * @param effortCertificationDocument the given effort certification document
     * @return the source accounting lines for a salary expense transfer document built from the given effort certification document
     */
    private List<LaborLedgerExpenseTransferAccountingLine> buildSourceAccountingLines(EffortCertificationDocument effortCertificationDocument) {
        List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines = new ArrayList<LaborLedgerExpenseTransferAccountingLine>();
        Class<? extends LaborLedgerExpenseTransferAccountingLine> sourceLineclass = laborModuleService.getExpenseTransferSourceAccountingLineClass();

        List<EffortCertificationDetail> effortCertificationDetailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            if (this.getDifference(detailLine).isPositive()) {
                this.addAccountingLineIntoList(sourceAccountingLines, sourceLineclass, effortCertificationDocument, detailLine);
            }
        }
        return sourceAccountingLines;
    }

    /**
     * build the target accounting lines for a salary expense transfer document from the given effort certification document. In the
     * holder, the first item is source accounting line list and the second the target accounting line list.
     * 
     * @param effortCertificationDocument the given effort certification document
     * @return the target accounting lines for a salary expense transfer document built from the given effort certification document
     */
    private List<LaborLedgerExpenseTransferAccountingLine> buildTargetAccountingLines(EffortCertificationDocument effortCertificationDocument) {
        List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines = new ArrayList<LaborLedgerExpenseTransferAccountingLine>();
        Class<? extends LaborLedgerExpenseTransferAccountingLine> targetLineclass = laborModuleService.getExpenseTransferTargetAccountingLineClass();

        List<EffortCertificationDetail> effortCertificationDetailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            if (this.getDifference(detailLine).isNegative()) {
                this.addAccountingLineIntoList(targetAccountingLines, targetLineclass, effortCertificationDocument, detailLine);
            }
        }
        return targetAccountingLines;
    }

    /**
     * add a new accounting line into the given accounting line list. The accounting line is generated from the given detail line
     * 
     * @param accountingLines a list of accounting lines
     * @param clazz the specified class of the accounting line
     * @param effortCertificationDocument the given effort certification document that contains the given detail line
     * @param detailLine the given detail line that is used to generate an accounting line
     */
    private void addAccountingLineIntoList(List<LaborLedgerExpenseTransferAccountingLine> accountingLineList, Class<? extends LaborLedgerExpenseTransferAccountingLine> clazz, EffortCertificationDocument effortCertificationDocument, EffortCertificationDetail detailLine) {
        LaborLedgerExpenseTransferAccountingLine accountingLine = ObjectUtil.createObject(clazz);
        accountingLine.setSequenceNumber(accountingLineList.size() + 1);

        this.populateAccountingLine(effortCertificationDocument, detailLine, accountingLine);
        accountingLineList.add(accountingLine);
    }

    /**
     * populate an accounting line from the given detail line
     * 
     * @param effortCertificationDocument the given effort certification document that contains the given detail line
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
        accountingLine.setPayrollEndDateFiscalYear(reportDefinition.getExpenseTransferFiscalYear());
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

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the contractsAndGrantsModuleService attribute value.
     * 
     * @param contractsAndGrantsModuleService The contractsAndGrantsModuleService to set.
     */
    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }
}

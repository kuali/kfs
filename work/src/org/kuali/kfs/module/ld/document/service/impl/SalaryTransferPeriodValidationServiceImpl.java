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
package org.kuali.kfs.module.ld.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.integration.ec.EffortCertificationModuleService;
import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.service.SalaryTransferPeriodValidationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.module.ld.document.service.SalaryTransferPeriodValidationService
 */
@Transactional
public class SalaryTransferPeriodValidationServiceImpl implements SalaryTransferPeriodValidationService {
    private EffortCertificationModuleService effortCertificationService;
    private DocumentService documentService;
    private NoteService noteService;
    private ConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.kfs.module.ld.document.service.SalaryTransferPeriodValidationService#validateTransfers(org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument)
     */
    @Override
    public boolean validateTransfers(SalaryExpenseTransferDocument document) {
        List<ExpenseTransferAccountingLine> transferLinesInOpenPeriod = new ArrayList<ExpenseTransferAccountingLine>();

        // check for closed or open reporting period(s) ... closed periods result in error, open periods require more validation
        List<ExpenseTransferAccountingLine> allLines = new ArrayList<ExpenseTransferAccountingLine>(document.getSourceAccountingLines());
        allLines.addAll(document.getTargetAccountingLines());
        for (ExpenseTransferAccountingLine transferLine : allLines) {
            // check we have enough data for validation, if not business rules will report error
            if (!containsNecessaryData(transferLine)) {
                continue;
            }

            // if closed report found then return error
            EffortCertificationReport closedReport = getClosedReportingPeriod(transferLine);
            if (closedReport != null) {
                putError(LaborKeyConstants.ERROR_EFFORT_CLOSED_REPORT_PERIOD, transferLine, closedReport);
                return false;
            }

            // if open report(s) found then add transfer line to list for further validation
            EffortCertificationReport openReport = getOpenReportingPeriod(transferLine);
            if (openReport != null) {
                transferLinesInOpenPeriod.add(transferLine);
            }
        }

        // verify transfers will not affect the open reporting period
        Map<String, KualiDecimal> accountPeriodTransfer = new HashMap<String, KualiDecimal>();
        EffortCertificationReport emplidReport = null;
        for (ExpenseTransferAccountingLine transferLine : transferLinesInOpenPeriod) {
            emplidReport = isEmployeeWithOpenCertification(transferLine, document.getEmplid());
            if (emplidReport != null) {
                // if employee has a report, transfer lines cannot use cost share sub-accounts
                if (isCostShareSubAccount(transferLine)) {
                    putError(LaborKeyConstants.ERROR_EFFORT_OPEN_PERIOD_COST_SHARE, transferLine, emplidReport);
                    return false;
                }

                // add line amount for validation later
                addAccountTransferAmount(accountPeriodTransfer, transferLine, emplidReport);
            }
            else {
                // if employee does not have a report, transfer lines cannot use CG accounts
                if (transferLine.getAccount().isForContractsAndGrants()) {
                    EffortCertificationReport openReport = getOpenReportingPeriod(transferLine);
                    putError(LaborKeyConstants.ERROR_EFFORT_OPEN_PERIOD_CG_ACCOUNT, transferLine, openReport);
                    return false;
                }
            }
        }

        // verify balance is same for accounts in transfer map
        for (String transferKey : accountPeriodTransfer.keySet()) {
            KualiDecimal transfer = accountPeriodTransfer.get(transferKey);
            if (transfer.isNonZero()) {
                String[] keyFields = StringUtils.split(transferKey, ",");
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EFFORT_OPEN_PERIOD_ACCOUNTS_NOT_BALANCED, new String[] { keyFields[4], keyFields[0], keyFields[1] });
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.service.SalaryTransferPeriodValidationService#disapproveSalaryExpenseDocument(org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument)
     */
    @Override
    public void disapproveSalaryExpenseDocument(SalaryExpenseTransferDocument document) throws Exception {
        // create note explaining why the document was disapproved
        String message = kualiConfigurationService.getPropertyValueAsString(LaborKeyConstants.EFFORT_AUTO_DISAPPROVE_MESSAGE);
        Note cancelNote = documentService.createNoteFromDocument( document, message);

        Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
        cancelNote.setAuthorUniversalIdentifier(principal.getPrincipalId());
        noteService.save(cancelNote);
        document.addNote(cancelNote);

        documentService.disapproveDocument(document, "disapproved - failed effort certification checks");
    }

    /**
     * Checks list of report definitions for a closed period.
     *
     * @param transferLine - transfer line to find report definition for
     * @return closed report or null if one is not found
     */
    protected EffortCertificationReport getClosedReportingPeriod(ExpenseTransferAccountingLine transferLine) {
        List<EffortCertificationReport> effortReports = getEffortReportDefinitionsForLine(transferLine);

        for (EffortCertificationReport report : effortReports) {
            if (KFSConstants.PeriodStatusCodes.CLOSED.equals(report.getEffortCertificationReportPeriodStatusCode())) {
                return report;
            }
        }

        return null;
    }

    /**
     * Checks list of report definitions for a open period.
     *
     * @param transferLine - transfer line to find report definition for
     * @return open report or null if one is not found
     */
    protected EffortCertificationReport getOpenReportingPeriod(ExpenseTransferAccountingLine transferLine) {
        List<EffortCertificationReport> effortReports = getEffortReportDefinitionsForLine(transferLine);

        for (EffortCertificationReport report : effortReports) {
            if (KFSConstants.PeriodStatusCodes.OPEN.equals(report.getEffortCertificationReportPeriodStatusCode())) {
                return report;
            }
        }

        return null;
    }

    /**
     * Returns the open report periods from the given list of report definitions.
     *
     * @param effortReports - list of report definitions that are either open or closed
     * @return open effort report definitions
     */
    protected List<EffortCertificationReport> getOpenReportDefinitions(List<EffortCertificationReport> effortReports) {
        List<EffortCertificationReport> openReports = new ArrayList<EffortCertificationReport>();

        for (EffortCertificationReport report : effortReports) {
            if (KFSConstants.PeriodStatusCodes.OPEN.equals(report.getEffortCertificationReportPeriodStatusCode())) {
                openReports.add(report);
            }
        }

        return openReports;
    }

    /**
     * Checks the sub account type code against the values defined for cost share.
     *
     * @param transferLine - line with sub account to check
     * @return true if sub account is cost share, false otherwise
     */
    protected boolean isCostShareSubAccount(ExpenseTransferAccountingLine transferLine) {
        boolean isCostShare = false;

        if (ObjectUtils.isNotNull(transferLine.getSubAccount()) && ObjectUtils.isNotNull(transferLine.getSubAccount().getA21SubAccount())) {
            A21SubAccount a21SubAccount = transferLine.getSubAccount().getA21SubAccount();
            String subAccountTypeCode = a21SubAccount.getSubAccountTypeCode();

            List<String> costShareSubAccountTypeCodes = effortCertificationService.getCostShareSubAccountTypeCodes();
            if (costShareSubAccountTypeCodes.contains(subAccountTypeCode)) {
                isCostShare = true;
            }
        }

        return isCostShare;
    }

    /**
     * Finds all open effort reports for the given transfer line, then checks if the given emplid has a certification for one of
     * those open reports.
     *
     * @param transferLine - line to find open reports for
     * @param emplid - emplid to check for certification
     * @return report which emplid has certification, or null
     */
    protected EffortCertificationReport isEmployeeWithOpenCertification(ExpenseTransferAccountingLine transferLine, String emplid) {
        List<EffortCertificationReport> effortReports = getEffortReportDefinitionsForLine(transferLine);
        List<EffortCertificationReport> openEffortReports = getOpenReportDefinitions(effortReports);

        return effortCertificationService.isEmployeeWithOpenCertification(openEffortReports, emplid);
    }

    /**
     * Adds the line amount to the given map that contains the total transfer amount for the account and period.
     *
     * @param accountPeriodTransfer - map holding the total transfers
     * @param effortReport - open report for transfer line
     * @param transferLine - line with amount to add
     */
    protected void addAccountTransferAmount(Map<String, KualiDecimal> accountPeriodTransfer, ExpenseTransferAccountingLine transferLine, EffortCertificationReport effortReport) {
        String transferKey = StringUtils.join(new Object[] { transferLine.getPayrollEndDateFiscalYear(), transferLine.getPayrollEndDateFiscalPeriodCode(), transferLine.getChartOfAccountsCode(), transferLine.getAccountNumber(), effortReport.getUniversityFiscalYear()+ "-" + effortReport.getEffortCertificationReportNumber() }, ",");

        KualiDecimal transferAmount = transferLine.getAmount().abs();
        if (transferLine instanceof ExpenseTransferSourceAccountingLine) {
            transferAmount = transferAmount.negated();
        }

        if (accountPeriodTransfer.containsKey(transferKey)) {
            transferAmount = transferAmount.add(accountPeriodTransfer.get(transferKey));
        }

        accountPeriodTransfer.put(transferKey, transferAmount);
    }

    /**
     * Gets open or closed report definitions for line pay period and pay type.
     *
     * @param transferLine - line to pull pay period and type from
     * @return - open or closed effort reports for period and type
     */
    protected List<EffortCertificationReport> getEffortReportDefinitionsForLine(ExpenseTransferAccountingLine transferLine) {
        Integer payFiscalYear = transferLine.getPayrollEndDateFiscalYear();
        String payFiscalPeriodCode = transferLine.getPayrollEndDateFiscalPeriodCode();
        String positionObjectGroupCode = transferLine.getLaborObject().getPositionObjectGroupCode();

        return effortCertificationService.findReportDefinitionsForPeriod(payFiscalYear, payFiscalPeriodCode, positionObjectGroupCode);
    }

    /**
     * Verfies the given tranfer line contains the necessary data for performing the effort validations.
     *
     * @param transferLine - line to check
     */
    protected boolean containsNecessaryData(ExpenseTransferAccountingLine transferLine) {
        //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(),
        //Both ExpenseTransferSourceAccountingLine and ExpenseTransferTargetAccountingLine do not have any updatable references
        transferLine.refreshNonUpdateableReferences();

        if (ObjectUtils.isNull(transferLine.getAccount()) || ObjectUtils.isNull(transferLine.getLaborObject()) || ObjectUtils.isNull(transferLine.getAmount())) {
            return false;
        }

        if (transferLine.getPayrollEndDateFiscalYear() == null || transferLine.getPayrollEndDateFiscalPeriodCode() == null) {
            return false;
        }

        return true;
    }

    /**
     * Determines whether the error should be associated with the source or target lines, and builds up parameters for error
     * message.
     *
     * @param errorKey - key for the error message
     * @param transferLine - transfer line which had error
     * @param report - report which conflicted with line
     */
    protected void putError(String errorKey, ExpenseTransferAccountingLine transferLine, EffortCertificationReport report) {
        String errorLines = KFSPropertyConstants.TARGET_ACCOUNTING_LINES;
        if (transferLine instanceof ExpenseTransferSourceAccountingLine) {
            errorLines = KFSPropertyConstants.SOURCE_ACCOUNTING_LINES;
        }

        String[] errorParameters = new String[3];
        errorParameters[0] = report.getUniversityFiscalYear() + "-" + report.getEffortCertificationReportNumber();
        errorParameters[1] = transferLine.getPayrollEndDateFiscalYear().toString();
        errorParameters[2] = transferLine.getPayrollEndDateFiscalPeriodCode();

        GlobalVariables.getMessageMap().putError(errorLines, errorKey, errorParameters);
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
     * Sets the effortCertificationService attribute value.
     *
     * @param effortCertificationService The effortCertificationService to set.
     */
    public void setEffortCertificationService(EffortCertificationModuleService effortCertificationService) {
        this.effortCertificationService = effortCertificationService;
    }

    /**
     * Sets the noteService attribute value.
     *
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}


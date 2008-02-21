/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.LaborLedgerBalance;
import org.kuali.kfs.service.LaborModuleService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.util.Message;
import org.kuali.kfs.util.MessageBuilder;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.EffortConstants.ExtractProcess;
import org.kuali.module.effort.EffortConstants.SystemParameters;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.report.EffortReportRegistry;
import org.kuali.module.effort.rules.LedgerBalanceFieldValidator;
import org.kuali.module.effort.service.EffortCertificationDocumentBuildService;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.effort.service.EffortCertificationReportDefinitionService;
import org.kuali.module.effort.service.EffortCertificationReportService;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;
import org.kuali.module.effort.util.ExtractProcessReportDataHolder;
import org.kuali.module.effort.util.LedgerBalanceConsolidationHelper;
import org.kuali.module.effort.util.LedgerBalanceWithMessage;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is an implemeation of Effort Certification Extract process, which extracts Labor Ledger records of the employees who were
 * paid on a grant or cost shared during the selected reporting period, and generates effort certification build/temporary document.
 * Its major tasks include:
 * 
 * <li>Identify employees who were paid on a grant or cost shared;</li>
 * <li>Select qualified Labor Ledger records for each identified employee;</li>
 * <li>Generate effort certification build document from the selected Labor Ledger records for each employee.</li>
 */
@Transactional
public class EffortCertificationExtractServiceImpl implements EffortCertificationExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationExtractServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;

    private LaborModuleService laborModuleService;
    private EffortCertificationDocumentBuildService effortCertificationDocumentBuildService;
    private EffortCertificationReportService effortCertificationReportService;
    private EffortCertificationReportDefinitionService effortCertificationReportDefinitionService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#extract()
     */
    @Logged
    public void extract() {
        Integer fiscalYear = EffortCertificationParameterFinder.getExtractReportFiscalYear();
        String reportNumber = EffortCertificationParameterFinder.getExtractReportNumber();

        this.extract(fiscalYear, reportNumber);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#extract(java.lang.Integer, java.lang.String)
     */
    @Logged
    public void extract(Integer fiscalYear, String reportNumber) {
        Map<String, String> fieldValues = EffortCertificationReportDefinition.buildKeyMap(fiscalYear, reportNumber);

        // check if a report has been defined and its docuemnts have not been generated.
        String errorMessage = this.validateReportDefintion(fiscalYear, reportNumber);
        errorMessage = StringUtils.isNotEmpty(errorMessage) ? errorMessage : this.existEffortCertificationDocument(fieldValues);
        if (StringUtils.isNotEmpty(errorMessage)) {
            LOG.fatal(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Map<String, List<String>> parameters = this.getSystemParameters();
        parameters.put(ExtractProcess.EXPENSE_OBJECT_TYPE, getExpenseObjectTypeCodes(fiscalYear));

        EffortCertificationReportDefinition reportDefinition = effortCertificationReportDefinitionService.findReportDefinitionByPrimaryKey(fieldValues);
        ExtractProcessReportDataHolder reportDataHolder = this.initializeReportData(reportDefinition);

        List<String> employees = this.findEmployeesEligibleForEffortCertification(reportDefinition);

        effortCertificationDocumentBuildService.removeExistingDocumentBuild(fieldValues);
        this.generateDocumentBuild(reportDefinition, employees, reportDataHolder, parameters);

        String reportsDirectory = EffortReportRegistry.getReportsDirectory();
        Date runDate = dateTimeService.getCurrentSqlDate();
        effortCertificationReportService.generateReportForExtractProcess(reportDataHolder, EffortReportRegistry.EFFORT_EXTRACT_SUMMARY, reportsDirectory, runDate);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#extract(java.lang.String,
     *      org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    @Logged
    public EffortCertificationDocumentBuild extract(String emplid, EffortCertificationReportDefinition reportDefinition) {
        Map<String, List<String>> parameters = this.getSystemParameters();
        parameters.put(ExtractProcess.EXPENSE_OBJECT_TYPE, getExpenseObjectTypeCodes(reportDefinition.getUniversityFiscalYear()));

        List<String> positionGroupCodes = effortCertificationReportDefinitionService.findPositionObjectGroupCodes(reportDefinition);
        Integer postingYear = universityDateService.getCurrentFiscalYear();
        ExtractProcessReportDataHolder reportDataHolder = this.initializeReportData(reportDefinition);

        return this.generateDocumentBuildByEmployee(postingYear, emplid, positionGroupCodes, reportDefinition, reportDataHolder, parameters);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#isEmployeesEligibleForEffortCertification(java.lang.String,
     *      org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    @Logged
    public boolean isEmployeeEligibleForEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition) {
        Map<String, Set<String>> earnCodePayGroups = effortCertificationReportDefinitionService.findReportEarnCodePayGroups(reportDefinition);
        List<String> balanceTypeList = EffortConstants.ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT;
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        return laborModuleService.isEmployeeWithPayType(emplid, reportPeriods, balanceTypeList, earnCodePayGroups);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#findEmployeesEligibleForEffortCertification(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    @Logged
    public List<String> findEmployeesEligibleForEffortCertification(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Set<String>> earnCodePayGroups = effortCertificationReportDefinitionService.findReportEarnCodePayGroups(reportDefinition);
        List<String> balanceTypeList = EffortConstants.ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT;
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        return laborModuleService.findEmployeesWithPayType(reportPeriods, balanceTypeList, earnCodePayGroups);
    }

    /**
     * check if a report has been defined. The combination of fiscal year and report number can determine a report definition.
     * 
     * @param fiscalYear the the given fiscalYear
     * @param reportNumber the the given report number
     * @return a message if a report has not been defined or its documents have been gerenated; otherwise, return null
     */
    private String validateReportDefintion(Integer fiscalYear, String reportNumber) {
        EffortCertificationReportDefinition reportDefinition = new EffortCertificationReportDefinition();
        reportDefinition.setUniversityFiscalYear(fiscalYear);
        reportDefinition.setEffortCertificationReportNumber(reportNumber);

        String errorMessage = effortCertificationReportDefinitionService.validateEffortCertificationReportDefinition(reportDefinition);
        return StringUtils.isNotEmpty(errorMessage) ? errorMessage : null;
    }

    /**
     * check if the docuemnts for the given report definition have not been generated. The combination of fiscal year and report
     * number can determine a report definition.
     * 
     * @param fieldValues the map containing fiscalYear and report number
     * @return a message if the documents have been gerenated; otherwise, return null
     */
    private String existEffortCertificationDocument(Map<String, String> fieldValues) {
        String fiscalYear = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String reportNumber = fieldValues.get(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);

        // check if any document has been generated for the selected report definition
        int countOfDocuments = businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues);
        if (countOfDocuments > 0) {
            return MessageBuilder.buildMessageWithPlaceHolder(EffortKeyConstants.ERROR_REPORT_DOCUMENT_EXIST, reportNumber, fiscalYear).getMessage();
        }

        return null;
    }

    /**
     * generate a document (build) as well as their detail lines for the given employees
     * 
     * @param reportDefinition the given report definition
     * @param employees the given employees
     * @param reportDataHolder the holder of report data
     * @param parameters the given system parameters
     */
    private void generateDocumentBuild(EffortCertificationReportDefinition reportDefinition, List<String> employees, ExtractProcessReportDataHolder reportDataHolder, Map<String, List<String>> parameters) {
        List<String> positionGroupCodes = effortCertificationReportDefinitionService.findPositionObjectGroupCodes(reportDefinition);
        Integer postingYear = universityDateService.getCurrentFiscalYear();

        for (String emplid : employees) {
            EffortCertificationDocumentBuild document = this.generateDocumentBuildByEmployee(postingYear, emplid, positionGroupCodes, reportDefinition, reportDataHolder, parameters);

            if (document != null) {
                List<EffortCertificationDocumentBuild> documents = new ArrayList<EffortCertificationDocumentBuild>();
                documents.add(document);

                businessObjectService.save(documents);

                reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_DETAIL_LINES_WRITTEN, this.getCountOfDetailLines(documents));
                reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_CERTIFICATIONS_WRITTEN, documents.size());
            }
        }
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_EMPLOYEES_SELECTED, employees.size());
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_ERRORS_FOUND, reportDataHolder.getLedgerBalancesWithMessage().size());
    }

    /**
     * extract the qualified labor ledger balance records of the given employee with the given report periods.
     * 
     * @param emplid the given employee id
     * @param positionGroupCodes the specified position group codes
     * @param reportDefinition the specified report definition
     * @param parameters the system paramters setup in front
     * @param reportDataHolder the given holder that contains any information to be written into the working report
     * @return the qualified labor ledger balance records of the given employee
     */
    private List<LaborLedgerBalance> getQualifiedLedgerBalances(String emplid, List<String> positionGroupCodes, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder, Map<String, List<String>> parameters) {
        List<LedgerBalanceWithMessage> ledgerBalancesWithMessage = reportDataHolder.getLedgerBalancesWithMessage();
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        // select ledger balances by the given employee and other criteria
        Collection<LaborLedgerBalance> ledgerBalances = this.selectLedgerBalanceForEmployee(emplid, positionGroupCodes, reportDefinition, parameters);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_BALANCES_READ, ledgerBalances.size());

        // clear up the ledger balance collection
        List<LaborLedgerBalance> validLedgerBalances = this.removeUnqualifiedLedgerBalances(ledgerBalances, reportDefinition, reportDataHolder);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_BALANCES_SELECTED, validLedgerBalances.size());

        // consolidate the pre-qualified ledger balances
        List<LaborLedgerBalance> consolidatedLedgerBalances = this.cosolidateLedgerBalances(validLedgerBalances, reportDefinition);

        // check the employee according to the pre-qualified ledger balances
        boolean isQualifiedEmployee = this.checkEmployeeBasedOnLedgerBalances(emplid, consolidatedLedgerBalances, reportDefinition, reportDataHolder, parameters);

        // abort all ledger balances if the employee is not qualified; otherwise, adopt the consolidated balances
        List<LaborLedgerBalance> qualifiedLedgerBalances = isQualifiedEmployee ? consolidatedLedgerBalances : null;

        return qualifiedLedgerBalances;
    }

    /**
     * remove the ledger balances without valid account, and nonzero total amount
     * 
     * @param ledgerBalances the given ledger balances
     * @param reportDefinition the given report definition
     * @param reportDataHolder the given holder that contains any information to be written into the working report
     */
    private List<LaborLedgerBalance> removeUnqualifiedLedgerBalances(Collection<LaborLedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder) {
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        List<LedgerBalanceWithMessage> ledgerBalancesWithMessage = reportDataHolder.getLedgerBalancesWithMessage();

        List<LaborLedgerBalance> validLedgerBalances = new ArrayList<LaborLedgerBalance>();

        for (LaborLedgerBalance balance : ledgerBalances) {
            // within the given periods, the total amount of a single balance cannot be ZERO
            Message errorAmountMessage = LedgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod(balance, reportPeriods);
            if (errorAmountMessage != null) {
                this.reportInvalidLedgerBalance(ledgerBalancesWithMessage, balance, errorAmountMessage);
            }

            // every balance record must be associated with a valid account
            Message invalidAccountMessage = LedgerBalanceFieldValidator.hasValidAccount(balance);
            if (invalidAccountMessage != null) {
                this.reportInvalidLedgerBalance(ledgerBalancesWithMessage, balance, invalidAccountMessage);
            }

            if (errorAmountMessage == null && invalidAccountMessage == null) {
                validLedgerBalances.add(balance);
            }
        }

        ledgerBalances = null;
        return validLedgerBalances;
    }

    /**
     * check all ledger balances of the given employee and see if they can meet certain requiremnets. If not, the employee would be
     * unqualified for effort reporting
     * 
     * @param emplid the given employee id
     * @param ledgerBalances the all pre-qualified ledger balances of the employee
     * @param reportDefinition the specified report definition
     * @param reportDataHolder the given holder that contains any information to be written into the working report
     * @param parameters the system paramters setup in front
     * @return true if all ledger balances as whole meet requirements; otherwise, return false
     */
    private boolean checkEmployeeBasedOnLedgerBalances(String emplid, List<LaborLedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder, Map<String, List<String>> parameters) {
        if (ledgerBalances == null || ledgerBalances.isEmpty()) {
            return false;
        }

        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        List<LedgerBalanceWithMessage> ledgerBalancesWithMessage = reportDataHolder.getLedgerBalancesWithMessage();

        // the total amount of all balances must be positive; otherwise, not to generate effort report for the employee
        Message nonpositiveTotalError = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportPeriods);
        if (nonpositiveTotalError != null) {
            this.reportEmployeeWithoutValidBalances(ledgerBalancesWithMessage, nonpositiveTotalError, emplid);
            return false;
        }

        // the specified employee must have at least one grant account
        Message grantAccountNotFoundError = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances);
        if (grantAccountNotFoundError != null) {
            this.reportEmployeeWithoutValidBalances(ledgerBalancesWithMessage, grantAccountNotFoundError, emplid);
            return false;
        }

        // check if there is at least one account funded by federal grants when an effort report can only be generated for an
        // employee with pay by federal grant
        boolean isFederalFundsOnly = Boolean.parseBoolean(parameters.get(SystemParameters.FEDERAL_ONLY_BALANCE_IND).get(0));
        if (isFederalFundsOnly) {
            List<String> federalAgencyTypeCodes = parameters.get(SystemParameters.FEDERAL_AGENCY_TYPE_CODE);

            Message federalFundsNotFoundError = LedgerBalanceFieldValidator.hasFederalFunds(ledgerBalances, federalAgencyTypeCodes);
            if (federalFundsNotFoundError != null) {
                this.reportEmployeeWithoutValidBalances(ledgerBalancesWithMessage, federalFundsNotFoundError, emplid);
                return false;
            }
        }

        return true;
    }

    /**
     * select the labor ledger balances for the specifed employee
     * 
     * @param emplid the given empolyee id
     * @param positionObjectGroupCodes the specified position object group codes
     * @param reportDefinition the specified report definition
     * @return the labor ledger balances for the specifed employee
     */
    private Collection<LaborLedgerBalance> selectLedgerBalanceForEmployee(String emplid, List<String> positionObjectGroupCodes, EffortCertificationReportDefinition reportDefinition, Map<String, List<String>> parameters) {
        List<String> expenseObjectTypeCodes = parameters.get(ExtractProcess.EXPENSE_OBJECT_TYPE);
        List<String> excludedAccountTypeCode = parameters.get(SystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT);
        List<String> emplids = Arrays.asList(emplid);
        List<String> laborObjectCodes = Arrays.asList(EffortConstants.LABOR_OBJECT_SALARY_CODE);

        Map<String, List<String>> fieldValues = new HashMap<String, List<String>>();
        fieldValues.put(KFSPropertyConstants.EMPLID, emplids);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, expenseObjectTypeCodes);
        fieldValues.put(EffortPropertyConstants.LABOR_OBJECT_FRINGE_OR_SALARY_CODE, laborObjectCodes);

        Map<String, List<String>> excludedFieldValues = new HashMap<String, List<String>>();
        excludedFieldValues.put(EffortPropertyConstants.ACCOUNT_ACCOUNT_TYPE_CODE, excludedAccountTypeCode);

        Set<Integer> fiscalYears = reportDefinition.getReportPeriods().keySet();
        List<String> balanceTypes = EffortConstants.ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT;

        return laborModuleService.findLedgerBalances(fieldValues, excludedFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
    }

    /**
     * consolidate the given labor ledger balances and determine whether they are qualified for effort reporting
     * 
     * @param ledgerBalances the given labor ledger balances
     * @param reportDefinition the specified report definition
     * @return a collection of ledger balances if they are qualified; otherwise, return null
     */
    private List<LaborLedgerBalance> cosolidateLedgerBalances(List<LaborLedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition) {
        List<LaborLedgerBalance> cosolidatedLedgerBalances = new ArrayList<LaborLedgerBalance>();

        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        Map<String, LaborLedgerBalance> ledgerBalanceMap = new HashMap<String, LaborLedgerBalance>();
        LedgerBalanceConsolidationHelper.consolidateLedgerBalances(ledgerBalanceMap, ledgerBalances, this.getConsolidationKeys());

        for (String key : ledgerBalanceMap.keySet()) {
            LaborLedgerBalance ledgerBalance = ledgerBalanceMap.get(key);

            KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods);
            if (totalAmount.isNonZero()) {
                cosolidatedLedgerBalances.add(ledgerBalance);
            }
        }

        return cosolidatedLedgerBalances;
    }

    // generate the effort certification document build for the given employee
    private EffortCertificationDocumentBuild generateDocumentBuildByEmployee(Integer postingYear, String emplid, List<String> positionGroupCodes, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder, Map<String, List<String>> parameters) {
        List<LaborLedgerBalance> qualifiedLedgerBalance = this.getQualifiedLedgerBalances(emplid, positionGroupCodes, reportDefinition, reportDataHolder, parameters);

        if (qualifiedLedgerBalance == null || qualifiedLedgerBalance.isEmpty()) {
            return null;
        }

        return effortCertificationDocumentBuildService.generateDocumentBuild(postingYear, reportDefinition, qualifiedLedgerBalance, parameters);
    }

    // add an error entry into error map
    private void reportInvalidLedgerBalance(List<LedgerBalanceWithMessage> ledgerBalancesWithMessage, LaborLedgerBalance ledgerBalance, Message message) {
        ledgerBalancesWithMessage.add(new LedgerBalanceWithMessage(ledgerBalance, message.toString()));
    }

    // add an error entry into error map
    private void reportEmployeeWithoutValidBalances(List<LedgerBalanceWithMessage> ledgerBalancesWithMessage, Message message, String emplid) {
        Class<? extends LaborLedgerBalance> clazz = laborModuleService.getLaborLedgerBalanceClass();
        LaborLedgerBalance ledgerBalance = ObjectUtil.createObject(clazz);
        ledgerBalance.setEmplid(emplid);
        this.reportInvalidLedgerBalance(ledgerBalancesWithMessage, ledgerBalance, message);
    }

    // store and cache relating system parameters in a Map for the future use
    private Map<String, List<String>> getSystemParameters() {
        Map<String, List<String>> parameters = new HashMap<String, List<String>>();

        parameters.put(SystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT, EffortCertificationParameterFinder.getAccountTypeCodes());
        parameters.put(SystemParameters.FEDERAL_ONLY_BALANCE_IND, EffortCertificationParameterFinder.getFederalOnlyBalanceIndicatorAsString());
        parameters.put(SystemParameters.FEDERAL_AGENCY_TYPE_CODE, EffortCertificationParameterFinder.getFederalAgencyTypeCodes());

        parameters.put(SystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE, EffortCertificationParameterFinder.getCostShareSubAccountTypeCode());
        parameters.put(SystemParameters.EXPENSE_SUB_ACCOUNT_TYPE_CODE, EffortCertificationParameterFinder.getExpenseSubAccountTypeCode());

        return parameters;
    }

    // get the expense object code setup in System Options
    private List<String> getExpenseObjectTypeCodes(Integer fiscalYear) {
        List<String> expenseObjectTypeCodes = new ArrayList<String>();
        expenseObjectTypeCodes.add(optionsService.getOptions(fiscalYear).getFinObjTypeExpenditureexpCd());

        return expenseObjectTypeCodes;
    }

    // get the count of detail lines associated with the given documents
    private int getCountOfDetailLines(List<EffortCertificationDocumentBuild> documents) {
        int numOfDetailLines = 0;
        for (EffortCertificationDocumentBuild document : documents) {
            numOfDetailLines += document.getEffortCertificationDetailLinesBuild().size();
        }
        return numOfDetailLines;
    }

    // get the field names used to build the keys for record consolidation
    private List<String> getConsolidationKeys() {
        List<String> consolidationKeys = new ArrayList<String>();

        consolidationKeys.add(KFSPropertyConstants.EMPLID);
        consolidationKeys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationKeys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationKeys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        consolidationKeys.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        consolidationKeys.add(KFSPropertyConstants.POSITION_NUMBER);

        return consolidationKeys;
    }

    // initialize the report data hold with default values
    private ExtractProcessReportDataHolder initializeReportData(EffortCertificationReportDefinition reportDefinition) {
        ExtractProcessReportDataHolder reportDataHolder = new ExtractProcessReportDataHolder(reportDefinition);

        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_BALANCES_READ, 0);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_BALANCES_SELECTED, 0);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_DETAIL_LINES_WRITTEN, 0);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_CERTIFICATIONS_WRITTEN, 0);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_EMPLOYEES_SELECTED, 0);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_ERRORS_FOUND, 0);

        return reportDataHolder;
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
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
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
     * Sets the effortCertificationDocumentBuildService attribute value.
     * 
     * @param effortCertificationDocumentBuildService The effortCertificationDocumentBuildService to set.
     */
    public void setEffortCertificationDocumentBuildService(EffortCertificationDocumentBuildService effortCertificationDocumentBuildService) {
        this.effortCertificationDocumentBuildService = effortCertificationDocumentBuildService;
    }

    /**
     * Sets the effortCertificationReportService attribute value.
     * 
     * @param effortCertificationReportService The effortCertificationReportService to set.
     */
    public void setEffortCertificationReportService(EffortCertificationReportService effortCertificationReportService) {
        this.effortCertificationReportService = effortCertificationReportService;
    }

    /**
     * Sets the universityDateService attribute value.
     * 
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Sets the effortCertificationReportDefinitionService attribute value.
     * 
     * @param effortCertificationReportDefinitionService The effortCertificationReportDefinitionService to set.
     */
    public void setEffortCertificationReportDefinitionService(EffortCertificationReportDefinitionService effortCertificationReportDefinitionService) {
        this.effortCertificationReportDefinitionService = effortCertificationReportDefinitionService;
    }
}
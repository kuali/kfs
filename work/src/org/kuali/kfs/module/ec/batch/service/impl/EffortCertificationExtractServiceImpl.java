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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.EffortConstants.ExtractProcess;
import org.kuali.module.effort.EffortConstants.SystemParameters;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.bo.EffortCertificationReportEarnPaygroup;
import org.kuali.module.effort.bo.EffortCertificationReportPosition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rules.LedgerBalanceFieldValidator;
import org.kuali.module.effort.service.EffortCertificationDocumentBuildService;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.effort.service.EffortCertificationReportService;
import org.kuali.module.effort.service.LaborEffortCertificationService;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;
import org.kuali.module.effort.util.EffortReportRegistry;
import org.kuali.module.effort.util.ExtractProcessReportDataHolder;
import org.kuali.module.effort.util.LedgerBalanceConsolidationHelper;
import org.kuali.module.effort.util.LedgerBalanceWithMessage;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.MessageBuilder;
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

    private LaborEffortCertificationService laborEffortCertificationService;
    private EffortCertificationDocumentBuildService effortCertificationDocumentBuildService;
    private EffortCertificationReportService effortCertificationReportService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#extract()
     */
    @Logged
    public void extract() {
        Integer fiscalYear = EffortCertificationParameterFinder.getReportFiscalYear();
        String reportNumber = EffortCertificationParameterFinder.getReportNumber();

        this.extract(fiscalYear, reportNumber);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationExtractService#extract(java.lang.Integer, java.lang.String)
     */
    @Logged
    public void extract(Integer fiscalYear, String reportNumber) {
        Map<String, String> fieldValues = EffortCertificationReportDefinition.buildKeyMap(fiscalYear, reportNumber);

        String errorMessage = this.validateReportDefintion(fieldValues);
        if (StringUtils.isEmpty(errorMessage)) {
            throw new IllegalArgumentException(errorMessage);
        }

        Map<String, List<String>> parameters = this.getSystemParameters();
        parameters.put(ExtractProcess.EXPENSE_OBJECT_TYPE, getExpenseObjectTypeCodes(fiscalYear));

        EffortCertificationReportDefinition reportDefinition = this.findReportDefinitionByPrimaryKey(fieldValues);
        ExtractProcessReportDataHolder reportDataHolder = new ExtractProcessReportDataHolder(reportDefinition);

        List<String> employees = this.findEmployeesWithValidPayType(reportDefinition);

        this.removeExistingDocumentBuild(fieldValues);
        this.generateDocumentBuild(reportDefinition, employees, reportDataHolder, parameters);

        String reportsDirectory = EffortReportRegistry.getReportsDirectory();
        Date runDate = dateTimeService.getCurrentSqlDate();
        effortCertificationReportService.generateReportForExtractProcess(reportDataHolder, EffortReportRegistry.EFFORT_EXTRACT_SUMMARY, reportsDirectory, runDate);
    }

    /**
     * check if a report has been defined and its docuemnts have not been generated. The combination of fiscal year and report
     * number can determine a report definition.
     * 
     * @param fieldValues the map containing fiscalYear and report number
     * @return a message if a report has not been defined or its documents have been gerenated; otherwise, return null
     */
    private String validateReportDefintion(Map<String, String> fieldValues) {
        String fiscalYear = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String reportNumber = fieldValues.get(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);
        String combinedFieldValues = new StringBuilder(fiscalYear).append(EffortConstants.VALUE_SEPARATOR).append(reportNumber).toString();

        // Fiscal Year is required
        if (StringUtils.isEmpty(fiscalYear)) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_MISSING, null).getMessage();
        }

        // Report Number is required
        if (StringUtils.isEmpty(reportNumber)) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_REPORT_NUMBER_MISSING, null).getMessage();
        }

        // check if a report has been defined
        EffortCertificationReportDefinition reportDefinition = this.findReportDefinitionByPrimaryKey(fieldValues);
        if (reportDefinition == null) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_OR_REPORT_NUMBER_INVALID, combinedFieldValues).getMessage();
        }

        // check if the selected report definition is still active
        if (!reportDefinition.isActive()) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_INACTIVE, combinedFieldValues).getMessage();
        }

        // check if any document has been generated for the selected report definition
        int countOfDocuments = businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues);
        if (countOfDocuments > 0) {
            return MessageBuilder.buildErrorMessageWithPlaceHolder(EffortKeyConstants.ERROR_REPORT_DOCUMENT_EXIST, fiscalYear, reportNumber).getMessage();
        }

        return null;
    }

    /**
     * find the employees who were paid based on a set of specified pay type within the given report periods. Here, a pay type can
     * be determined by earn code and pay group.
     * 
     * @param reportDefinition the specified report definition
     * @return the employees who were paid based on a set of specified pay type within the given report periods
     */
    private List<String> findEmployeesWithValidPayType(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Set<String>> earnCodePayGroups = this.findReportEarnCodePayGroups(reportDefinition);
        List<String> balanceTypeList = EffortConstants.ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT;
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        return laborEffortCertificationService.findEmployeesWithPayType(reportPeriods, balanceTypeList, earnCodePayGroups);
    }

    /**
     * clear up documents and detail lines (build) with the fiscal year and report number of the given field values
     * 
     * @param fieldValues the map containing fiscalYear and report number
     */
    private void removeExistingDocumentBuild(Map<String, String> fieldValues) {
        businessObjectService.deleteMatching(EffortCertificationDocumentBuild.class, fieldValues);
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
        List<String> positionGroupCodes = this.findPositionObjectGroupCodes(reportDefinition);
        Integer postingYear = universityDateService.getCurrentFiscalYear();

        for (String emplid : employees) {
            Collection<LedgerBalance> qualifiedLedgerBalance;
            qualifiedLedgerBalance = this.getQualifiedLedgerBalances(emplid, positionGroupCodes, reportDefinition, reportDataHolder, parameters);

            if (qualifiedLedgerBalance == null || qualifiedLedgerBalance.isEmpty()) {
                continue;
            }

            List<EffortCertificationDocumentBuild> documents;
            documents = effortCertificationDocumentBuildService.generateDocumentBuildList(postingYear, reportDefinition, qualifiedLedgerBalance, parameters);
            businessObjectService.save(documents);

            reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_DETAIL_LINES_WRITTEN, qualifiedLedgerBalance.size());
            reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_CERTIFICATIONS_WRITTEN, documents.size());
        }
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_EMPLOYEES_SELECTED, employees.size());
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_ERRORS_FOUND, reportDataHolder.getLedgerBalancesWithMessage().size());
    }

    /**
     * find all position object group codes for the given report definition
     * 
     * @param reportDefinition the specified report definition
     * @return all position object group codes for the given report definition
     */
    private List<String> findPositionObjectGroupCodes(EffortCertificationReportDefinition reportDefinition) {
        Map<String, String> fieldValues = reportDefinition.buildKeyMapForCurrentReportDefinition();
        Collection<EffortCertificationReportPosition> reportPosition = businessObjectService.findMatching(EffortCertificationReportPosition.class, fieldValues);

        List<String> positionGroupCodes = new ArrayList<String>();
        for (EffortCertificationReportPosition position : reportPosition) {
            positionGroupCodes.add(position.getEffortCertificationReportPositionObjectGroupCode());
        }

        return positionGroupCodes;
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
    private Collection<LedgerBalance> getQualifiedLedgerBalances(String emplid, List<String> positionGroupCodes, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder, Map<String, List<String>> parameters) {
        List<LedgerBalanceWithMessage> ledgerBalancesWithMessage = reportDataHolder.getLedgerBalancesWithMessage();
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        // select ledger balances by the given employee and other criteria
        Collection<LedgerBalance> ledgerBalances = this.selectLedgerBalanceByEmployee(emplid, positionGroupCodes, reportDefinition, parameters);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_BALANCES_READ, ledgerBalances.size());

        // clear up the ledger balance collection
        this.removeUnqualifiedLedgerBalances(ledgerBalances, reportDefinition, reportDataHolder);
        reportDataHolder.updateBasicStatistics(ExtractProcess.NUM_BALANCES_SELECTED, ledgerBalances.size());

        // consolidate the pre-qualified ledger balances
        Collection<LedgerBalance> consolidatedLedgerBalances = this.cosolidateLedgerBalances(ledgerBalances, reportDefinition);

        // check the employee according to the pre-qualified ledger balances
        boolean isQualifiedEmployee = this.checkEmployeeBasedOnLedgerBalances(emplid, consolidatedLedgerBalances, reportDefinition, reportDataHolder, parameters);

        // abort all ledger balances if the employee is not qualified; otherwise, adopt the consolidated balances
        Collection<LedgerBalance> qualifiedLedgerBalances = isQualifiedEmployee ? consolidatedLedgerBalances : null;

        return qualifiedLedgerBalances;
    }

    /**
     * remove the ledger balances without valid account, higher education function code, and nonzero total amount
     * 
     * @param ledgerBalances the given ledger balances
     * @param reportDefinition the given report definition
     * @param reportDataHolder the given holder that contains any information to be written into the working report
     */
    private void removeUnqualifiedLedgerBalances(Collection<LedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder) {
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        List<LedgerBalanceWithMessage> ledgerBalancesWithMessage = reportDataHolder.getLedgerBalancesWithMessage();

        for (LedgerBalance balance : ledgerBalances) {
            // within the given periods, the total amount of a single balance cannot be ZERO
            String errorMessage = LedgerBalanceFieldValidator.isNonZeroAmountBalanceWithinReportPeriod(balance, reportPeriods).getMessage();

            // every balance record must be associated with a valid account
            if (StringUtils.isEmpty(errorMessage)) {
                errorMessage = LedgerBalanceFieldValidator.hasValidAccount(balance).getMessage();
            }

            // the account of every balance record must have valid high education function code
            if (StringUtils.isEmpty(errorMessage)) {
                errorMessage = LedgerBalanceFieldValidator.hasHigherEdFunction(balance).getMessage();
            }

            // remove the unqualified ledger balance from the list and report the error
            if (StringUtils.isNotEmpty(errorMessage)) {
                this.reportInvalidLedgerBalance(ledgerBalancesWithMessage, balance, errorMessage);
                ledgerBalances.remove(balance);
            }
        }
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
    private boolean checkEmployeeBasedOnLedgerBalances(String emplid, Collection<LedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder, Map<String, List<String>> parameters) {
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        List<LedgerBalanceWithMessage> ledgerBalancesWithMessage = reportDataHolder.getLedgerBalancesWithMessage();

        // the total amount of all balances must be positive; otherwise, not to generate effort report for the employee
        String nonpositiveTotalError = LedgerBalanceFieldValidator.isTotalAmountPositive(ledgerBalances, reportPeriods).getMessage();
        if (StringUtils.isNotEmpty(nonpositiveTotalError)) {
            this.reportEmployeeWithoutValidBalances(ledgerBalancesWithMessage, nonpositiveTotalError, emplid);
            return false;
        }
        
        // an employee must not be paid by multiple organizations
        String multipleOrganizationError = LedgerBalanceFieldValidator.hasMultipleOrganizations(ledgerBalances).getMessage();
        if (StringUtils.isNotEmpty(multipleOrganizationError)) {
            this.reportEmployeeWithoutValidBalances(ledgerBalancesWithMessage, multipleOrganizationError, emplid);
            return false;
        }

        // the specified employee must have at least one grant account
        boolean fundGroupDenotesCGIndictor = Boolean.parseBoolean(parameters.get(SystemParameters.FUND_GROUP_DENOTES_CG_IND).get(0));
        List<String> fundGroupCodes = parameters.get(SystemParameters.CG_DENOTING_VALUE);

        String grantAccountNotFoundError = LedgerBalanceFieldValidator.hasGrantAccount(ledgerBalances, fundGroupDenotesCGIndictor, fundGroupCodes).getMessage();
        if (StringUtils.isNotEmpty(grantAccountNotFoundError)) {
            this.reportEmployeeWithoutValidBalances(ledgerBalancesWithMessage, grantAccountNotFoundError, emplid);
            return false;
        }

        // check if there is at least one account funded by federal grants when an effort report can only be generated for an
        // employee with pay by federal grant
        boolean isFederalFundsOnly = Boolean.parseBoolean(parameters.get(SystemParameters.FEDERAL_ONLY_BALANCE_IND).get(0));
        if (isFederalFundsOnly) {
            List<String> federalAgencyTypeCodes = parameters.get(SystemParameters.FEDERAL_AGENCY_TYPE_CODE);

            String federalFundsNotFoundError = LedgerBalanceFieldValidator.hasFederalFunds(ledgerBalances, federalAgencyTypeCodes).getMessage();
            if (StringUtils.isNotEmpty(federalFundsNotFoundError)) {
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
    private Collection<LedgerBalance> selectLedgerBalanceByEmployee(String emplid, List<String> positionObjectGroupCodes, EffortCertificationReportDefinition reportDefinition, Map<String, List<String>> parameters) {
        String expenseObjectTypeCode = parameters.get(ExtractProcess.EXPENSE_OBJECT_TYPE).get(0);
        String accountTypeCode = parameters.get(SystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT).get(0);

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, expenseObjectTypeCode);
        fieldValues.put(EffortPropertyConstants.LABOR_OBJECT_FRINGE_OR_SALARY_CODE, EffortConstants.LABOR_OBJECT_SALARY_CODE);

        Map<String, String> exclusiveFieldValues = new HashMap<String, String>();
        exclusiveFieldValues.put(EffortPropertyConstants.ACCOUNT_ACCOUNT_TYPE_CODE, accountTypeCode);

        Set<Integer> fiscalYears = reportDefinition.getReportPeriods().keySet();
        List<String> balanceTypes = EffortConstants.ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT;

        return laborEffortCertificationService.findLedgerBalances(fieldValues, exclusiveFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
    }

    /**
     * store the earn code and pay group combination in a Map for the specified report definition
     * 
     * @param reportDefinition the specified report definition
     * @return the earn code and pay group combination for the specified report definition as a Map
     */
    private Map<String, Set<String>> findReportEarnCodePayGroups(EffortCertificationReportDefinition reportDefinition) {
        Collection<EffortCertificationReportEarnPaygroup> reportEarnPay = this.findReportEarnPay(reportDefinition);
        Map<String, Set<String>> earnCodePayGroups = new HashMap<String, Set<String>>();

        for (EffortCertificationReportEarnPaygroup earnPay : reportEarnPay) {
            String payGroup = earnPay.getPayGroup();
            String earnCode = earnPay.getEarnCode();

            if (earnCodePayGroups.containsKey(payGroup)) {
                Set<String> earnCodeSet = earnCodePayGroups.get(payGroup);
                earnCodeSet.add(earnCode);
            }
            else {
                Set<String> earnCodeSet = new HashSet<String>();
                earnCodeSet.add(earnCode);
                earnCodePayGroups.put(payGroup, earnCodeSet);
            }
        }

        return earnCodePayGroups;
    }

    /**
     * find the earn code and pay group combination for the specified report definition
     * 
     * @param reportDefinition the specified report definition
     * @return the earn code and pay group combination for the specified report definition
     */
    private Collection<EffortCertificationReportEarnPaygroup> findReportEarnPay(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_TYPE_CODE, reportDefinition.getEffortCertificationReportTypeCode());

        return businessObjectService.findMatching(EffortCertificationReportEarnPaygroup.class, fieldValues);
    }

    /**
     * consolidate the given labor ledger balances and determine whether they are qualified for effort reporting
     * 
     * @param ledgerBalances the given labor ledger balances
     * @param reportDefinition the specified report definition
     * @return a collection of ledger balances if they are qualified; otherwise, return null
     */
    private Collection<LedgerBalance> cosolidateLedgerBalances(Collection<LedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition) {
        Collection<LedgerBalance> cosolidatedLedgerBalances = new ArrayList<LedgerBalance>();

        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        Map<String, LedgerBalance> ledgerBalanceMap = new HashMap<String, LedgerBalance>();
        LedgerBalanceConsolidationHelper.consolidateLedgerBalances(ledgerBalanceMap, ledgerBalances, this.getConsolidationKeys());

        for (String key : ledgerBalanceMap.keySet()) {
            LedgerBalance ledgerBalance = ledgerBalanceMap.get(key);

            KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods);
            if (totalAmount.isNonZero()) {
                cosolidatedLedgerBalances.add(ledgerBalance);
            }
        }

        return cosolidatedLedgerBalances;
    }

    /**
     * find a report definition by the primary key. The primary key is provided by the given field values.
     * 
     * @param fieldValues the given field values containing the primary key of a report definition
     * @return a report definition with the given primary key
     */
    private EffortCertificationReportDefinition findReportDefinitionByPrimaryKey(Map<String, String> fieldValues) {
        return (EffortCertificationReportDefinition) businessObjectService.findByPrimaryKey(EffortCertificationReportDefinition.class, fieldValues);
    }

    // add an error entry into error map
    private void reportInvalidLedgerBalance(List<LedgerBalanceWithMessage> ledgerBalancesWithMessage, LedgerBalance ledgerBalance, String message) {
        ledgerBalancesWithMessage.add(new LedgerBalanceWithMessage(ledgerBalance, message));
    }

    // add an error entry into error map
    private void reportEmployeeWithoutValidBalances(List<LedgerBalanceWithMessage> ledgerBalancesWithMessage, String message, String emplid) {
        LedgerBalance ledgerBalance = new LedgerBalance();
        ledgerBalance.setEmplid(emplid);
        this.reportInvalidLedgerBalance(ledgerBalancesWithMessage, ledgerBalance, message);
    }

    // store and cache relating system parameters in a Map for the future use
    private Map<String, List<String>> getSystemParameters() {
        Map<String, List<String>> parameters = new HashMap<String, List<String>>();

        parameters.put(SystemParameters.FUND_GROUP_DENOTES_CG_IND, EffortCertificationParameterFinder.getFundGroupDenotesCGIndicatorAsString());
        parameters.put(SystemParameters.CG_DENOTING_VALUE, EffortCertificationParameterFinder.getCGDenotingValues());
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
     * Sets the laborEffortCertificationService attribute value.
     * 
     * @param laborEffortCertificationService The laborEffortCertificationService to set.
     */
    public void setLaborEffortCertificationService(LaborEffortCertificationService laborEffortCertificationService) {
        this.laborEffortCertificationService = laborEffortCertificationService;
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
}
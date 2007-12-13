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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.cg.bo.AwardAccount;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.EffortSystemParameters;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.bo.EffortCertificationReportEarnPaygroup;
import org.kuali.module.effort.bo.EffortCertificationReportPosition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.service.EffortCertificationDocumentBuildGenerator;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;
import org.kuali.module.effort.util.ExtractProcessReportDataHolder;
import org.kuali.module.effort.util.LedgerBalanceConsolidationHelper;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborLedgerEntryService;
import org.kuali.module.labor.service.LaborObjectService;
import org.kuali.module.labor.util.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is an implemeation of Effort Certification Extract process, which extracts Labor Ledger records of the employees who were
 * paid on a grant or cost shared during the selected reporting period, and generates effort certification build/temporary document.
 * Its major tasks include:
 * 
 * <li>Identify employees who were paid on a grant or cost shared;</li>
 * <li>Select qualified Labor Ledger records for each identified employee;</li>
 * <li>Generate effor certification build document from the selected Labor Ledger records for each employee.</li>
 */
@Transactional
public class EffortCertificationExtractServiceImpl implements EffortCertificationExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationExtractServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;

    private LaborObjectService laborObjectService;
    private LaborLedgerEntryService laborLedgerEntryService;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    private EffortCertificationDocumentBuildGenerator effortCertificationDocumentBuildGenerator;

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

        Message errorMessage = this.validateReportDefintion(fieldValues);
        if (errorMessage != null) {
            LOG.error(errorMessage);
            throw new IllegalArgumentException(errorMessage.getMessage());
        }

        this.removeExistingDocumentBuild(fieldValues);

        Map<String, List<String>> parameters = this.getSystemParameters();
        parameters.put(EffortConstants.ExtractProcess.EXPENSE_OBJECT_TYPE, getExpenseObjectTypeCodes(fiscalYear));

        EffortCertificationReportDefinition reportDefinition = this.findReportDefinitionByPrimaryKey(fieldValues);
        List<String> positionGroupCodes = this.findPositionObjectGroupCodes(reportDefinition);
        
        ExtractProcessReportDataHolder reportDataHolder = new ExtractProcessReportDataHolder();
        reportDataHolder.setReportDefinition(reportDefinition);

        List<String> employeesWithValidPayType = this.findEmployeesWithValidPayType(reportDefinition);
        for (String emplid : employeesWithValidPayType) {
            Collection<LedgerBalance> qualifiedLedgerBalance = this.getQualifiedLedgerBalances(emplid, positionGroupCodes, reportDefinition, parameters, reportDataHolder);

            if (qualifiedLedgerBalance != null) {
                List<EffortCertificationDocumentBuild> documents;
                documents = effortCertificationDocumentBuildGenerator.generate(reportDefinition, qualifiedLedgerBalance, parameters);

                businessObjectService.save(documents);
            }
        }
    }

    /**
     * check if a report has been defined and its docuemnt has not been generated. The combination of fiscal year and report number
     * can determine a report definition.
     * 
     * @param fieldValues the map containing fiscalYear and report number
     * @return a message if a report has been defined and its document has not been gerenated; otherwise, return null
     */
    private Message validateReportDefintion(Map<String, String> fieldValues) {
        String fiscalYear = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String reportNumber = fieldValues.get(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);
        String inputValues = fiscalYear + " ," + reportNumber;

        // Fiscal Year is required
        if (StringUtils.isEmpty(fiscalYear)) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_MISSING, null, Message.TYPE_FATAL);
        }

        // Report Number is required
        if (StringUtils.isEmpty(reportNumber)) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_REPORT_NUMBER_MISSING, null, Message.TYPE_FATAL);
        }

        // check if a report has been defined
        EffortCertificationReportDefinition reportDefinition = this.findReportDefinitionByPrimaryKey(fieldValues);
        if (reportDefinition == null) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_OR_REPORT_NUMBER_INVALID, inputValues, Message.TYPE_FATAL);
        }

        // check if the selected report definition is still active
        if (!reportDefinition.isActive()) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_INACTIVE, inputValues, Message.TYPE_FATAL);
        }

        // check if any document has been generated for the selected report definition
        int countOfDocuments = businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues);
        if (countOfDocuments > 0) {
            return MessageBuilder.buildErrorMessageWithPlaceHolder(EffortKeyConstants.ERROR_REPORT_DOCUMENT_EXIST, Message.TYPE_FATAL, fiscalYear, reportNumber);
        }

        return null;
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
     * find the employees who were paid based on a set of specified pay type within the given report periods. Here, a pay type can
     * be determined by earn code and pay group.
     * 
     * @param reportDefinition the specified report definition
     * @return the employees who were paid based on a set of specified pay type within the given report periods
     */
    private List<String> findEmployeesWithValidPayType(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Set<String>> earnCodePayGroupMap = this.findReportEarnPayMap(reportDefinition);
        List<String> balanceTypeList = EffortConstants.ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT;
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        return laborLedgerEntryService.findEmployeesWithPayType(reportPeriods, balanceTypeList, earnCodePayGroupMap);
    }

    /**
     * extract the qualified labor ledger balance records of the given employee with the given report periods.
     * 
     * @param emplid the given employee id
     * @param positionGroupCodes the specified position group codes
     * @param reportDefinition the specified report definition
     * @param parameters the system paramters setup in front
     * @return the qualified labor ledger balance records of the given employee
     */
    private Collection<LedgerBalance> getQualifiedLedgerBalances(String emplid, List<String> positionGroupCodes, EffortCertificationReportDefinition reportDefinition, Map<String, List<String>> parameters, ExtractProcessReportDataHolder reportDataHolder) {
        Map<LedgerBalance, String> errorMap = reportDataHolder.getErrorMap();
        Map<String, Integer> statistics = reportDataHolder.getBasicStatistics();

        Collection<LedgerBalance> ledgerBalances = this.selectLedgerBalanceByEmployee(emplid, positionGroupCodes, reportDefinition, parameters);
        reportDataHolder.updateBasicStatistics("TODO", ledgerBalances.size()); // TODO
        
        // clear up the ledger balance collection
        this.removeUnqualifiedLedgerBalances(ledgerBalances, reportDefinition, reportDataHolder);
        
        // prepare an empty ledger balance for error report
        LedgerBalance emptyLedgerBalance = new LedgerBalance();
        emptyLedgerBalance.setEmplid(emplid);
        
        // the total amount of all balances must be positive; otherwise, not generate effort report for the employee
        Collection<LedgerBalance> qualifiedLedgerBalances = this.getCosolidatedLedgerBalances(ledgerBalances, reportDefinition);        
        if(qualifiedLedgerBalances == null) {
            String error = MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_NONPOSITIVE_PAYROLL_AMOUNT, emplid, Message.TYPE_FATAL).toString();
            errorMap.put(emptyLedgerBalance, error);                
            return null;
        }

        // the specified employee must have at least one grant account
        if (!hasGrantAccount(qualifiedLedgerBalances, parameters)) {
            String error = MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FUND_GROUP_NOT_FOUND, emplid, Message.TYPE_FATAL).toString();
            errorMap.put(emptyLedgerBalance, error);                
            return null;
        }

        // check if there is at least one account funded by federal grants when an effort report can only be generated for an
        // employee with pay by federal grant
        boolean isFederalFundsOnly = Boolean.parseBoolean(parameters.get(EffortSystemParameters.FEDERAL_ONLY_BALANCE_IND).get(0));
        if (isFederalFundsOnly) {
            if (!hasFederalFunds(qualifiedLedgerBalances, parameters)) {
                String error = MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_NOT_PAID_BY_FEDERAL_FUNDS, emplid, Message.TYPE_FATAL).toString();
                errorMap.put(emptyLedgerBalance, error);                
                return null;
            }
        }

        return ledgerBalances;
    }

    /**
     * remove the ledger balances without valid account, higher education function code, and total amount
     * 
     * @param ledgerBalances the given ledger balances
     * @param reportDefinition the given report definition
     */    
    private void removeUnqualifiedLedgerBalances(Collection<LedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition, ExtractProcessReportDataHolder reportDataHolder) {
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        Map<LedgerBalance, String> errorMap = reportDataHolder.getErrorMap();
        
        for (LedgerBalance balance : ledgerBalances) {
            // within the given periods, the total amount of a single balance cannot be ZERO
            KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(balance, reportPeriods);
            if (totalAmount.isZero()) {
                String error = MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_ZERO_PAYROLL_AMOUNT, null, Message.TYPE_FATAL).toString();
                errorMap.put(balance, error);
                
                ledgerBalances.remove(balance);
                continue;
            }

            // every balance record must have valid high education function code
            if (!this.hasValidAccount(balance)) {
                String account = balance.getChartOfAccountsCode() + ", " + balance.getAccountNumber(); 
                String error = MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_ACCOUNT_NUMBER_NOT_FOUND, account, Message.TYPE_FATAL).toString();
                errorMap.put(balance, error);                
                
                ledgerBalances.remove(balance);
                continue;
            }

            // every balance record must have valid high education function code
            if (!this.hasValidHigherEdFunction(balance)) {
                String account = balance.getAccountNumber(); 
                String error = MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_HIGHER_EDUCATION_CODE_NOT_FOUND, account, Message.TYPE_FATAL).toString();
                errorMap.put(balance, error);                
                
                ledgerBalances.remove(balance);
                continue;
            }
        }        
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
        String expenseObjectTypeCode = parameters.get(EffortConstants.ExtractProcess.EXPENSE_OBJECT_TYPE).get(0);
        String accountTypeCode = parameters.get(EffortSystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT).get(0);

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, expenseObjectTypeCode);
        fieldValues.put(EffortPropertyConstants.LABOR_OBJECT_FRINGE_OR_SALARY_CODE, EffortConstants.LABOR_OBJECT_SALARY_CODE);

        Map<String, String> exclusiveFieldValues = new HashMap<String, String>();
        exclusiveFieldValues.put(EffortPropertyConstants.ACCOUNT_ACCOUNT_TYPE_CODE, accountTypeCode);

        Set<Integer> fiscalYears = reportDefinition.getReportPeriods().keySet();
        List<String> balanceTypes = EffortConstants.ELIGIBLE_BALANCE_TYPES_FOR_EFFORT_REPORT;

        return laborLedgerBalanceService.findLedgerBalances(fieldValues, exclusiveFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
    }

    /**
     * store the earn code and pay group combination in a Map for the specified report definition
     * 
     * @param reportDefinition the specified report definition
     * @return the earn code and pay group combination for the specified report definition as a Map
     */
    private Map<String, Set<String>> findReportEarnPayMap(EffortCertificationReportDefinition reportDefinition) {
        Collection<EffortCertificationReportEarnPaygroup> reportEarnPay = this.findReportEarnPay(reportDefinition);
        Map<String, Set<String>> earnCodePayGroupMap = new HashMap<String, Set<String>>();

        for (EffortCertificationReportEarnPaygroup earnPay : reportEarnPay) {
            String payGroup = earnPay.getPayGroup();
            String earnCode = earnPay.getEarnCode();

            if (earnCodePayGroupMap.containsKey(payGroup)) {
                Set<String> earnCodeSet = earnCodePayGroupMap.get(payGroup);
                earnCodeSet.add(earnCode);
            }
            else {
                Set<String> earnCodeSet = new HashSet<String>();
                earnCodeSet.add(earnCode);
                earnCodePayGroupMap.put(payGroup, earnCodeSet);
            }
        }
        return earnCodePayGroupMap;
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
     * check if the given ledger balance has an account qualified for effort reporting
     * 
     * @param ledgerBalance the given ledger balance
     * @return true if the given ledger balance has an account qualified for effort reporting; otherwise, false
     */
    private boolean hasValidAccount(LedgerBalance ledgerBalance) {
        return ledgerBalance.getAccount() == null;
    }

    /**
     * check if the given ledger balance has a valid higher education function
     * 
     * @param ledgerBalance the given ledger balance
     * @return true if the given ledger balance has a valid higher education function; otherwise, false
     */
    private boolean hasValidHigherEdFunction(LedgerBalance ledgerBalance) {
        return ledgerBalance.getAccount().getFinancialHigherEdFunction() == null;
    }

    /**
     * consolidate the given labor ledger balances and determine whether they are qualified for effort reporting
     * 
     * @param ledgerBalances the given labor ledger balances
     * @param reportDefinition the specified report definition
     * @return a collection of ledger balances if they are qualified; otherwise, return null
     */
    private Collection<LedgerBalance> getCosolidatedLedgerBalances(Collection<LedgerBalance> ledgerBalances, EffortCertificationReportDefinition reportDefinition) {
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

        KualiDecimal totalAmountForEmployee = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(cosolidatedLedgerBalances, reportPeriods);
        return totalAmountForEmployee.isPositive() ? cosolidatedLedgerBalances : null;
    }

    /**
     * detemine whether there is at least one grant account in the given labor ledger balances
     * 
     * @param ledgerBalances the given labor ledger balances
     * @param parameters the system paramters setup in front
     * @return true if there is at least one grant account; otherwise, return false
     */
    private boolean hasGrantAccount(Collection<LedgerBalance> ledgerBalances, Map<String, List<String>> parameters) {
        List<String> fundGroupDenotesCGIndictor = parameters.get(EffortSystemParameters.FUND_GROUP_DENOTES_CG_IND);
        List<String> fundGroupCodes = parameters.get(EffortSystemParameters.CG_DENOTING_VALUE);
        List<String> subFundGroupCodes = parameters.get(EffortSystemParameters.CG_DENOTING_VALUE);

        for (LedgerBalance balance : ledgerBalances) {
            SubFundGroup subFundGroup = balance.getAccount().getSubFundGroup();
            if (subFundGroup == null) {
                continue;
            }

            boolean isfundGroupChecked = Boolean.parseBoolean(fundGroupDenotesCGIndictor.get(0));
            if (isfundGroupChecked && fundGroupCodes.contains(subFundGroup.getFundGroupCode())) {
                return true;
            }

            if (!isfundGroupChecked && subFundGroupCodes.contains(subFundGroup.getSubFundGroupCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * determine whether there is at least one account with federal funding
     * 
     * @param the given labor ledger balances
     * @param parameters the system paramters setup in front
     * @return true if there is at least one account with federal funding; otherwise, false
     */
    private boolean hasFederalFunds(Collection<LedgerBalance> ledgerBalances, Map<String, List<String>> parameters) {
        List<String> federalAgencyTypeCodes = parameters.get(EffortSystemParameters.FEDERAL_AGENCY_TYPE_CODE);

        for (LedgerBalance balance : ledgerBalances) {
            List<AwardAccount> awardAccountList = balance.getAccount().getAwards();

            for (AwardAccount awardAccount : awardAccountList) {
                String agencyTypeCode = awardAccount.getAward().getAgency().getAgencyTypeCode();
                if (federalAgencyTypeCodes.contains(agencyTypeCode)) {
                    return true;
                }

                if (awardAccount.getAward().getFederalPassThroughIndicator()) {
                    return true;
                }
            }
        }
        return false;
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

    // store relating system parameters in a Map for later
    private Map<String, List<String>> getSystemParameters() {
        Map<String, List<String>> parameters = new HashMap<String, List<String>>();

        parameters.put(EffortSystemParameters.FUND_GROUP_DENOTES_CG_IND, EffortCertificationParameterFinder.getFundGroupDenotesCGIndicatorAsString());
        parameters.put(EffortSystemParameters.CG_DENOTING_VALUE, EffortCertificationParameterFinder.getCGDenotingValues());
        parameters.put(EffortSystemParameters.ACCOUNT_TYPE_CODE_BALANCE_SELECT, EffortCertificationParameterFinder.getAccountTypeCodes());
        parameters.put(EffortSystemParameters.FEDERAL_ONLY_BALANCE_IND, EffortCertificationParameterFinder.getFederalOnlyBalanceIndicatorAsString());
        parameters.put(EffortSystemParameters.FEDERAL_AGENCY_TYPE_CODE, EffortCertificationParameterFinder.getFederalAgencyTypeCodes());

        parameters.put(EffortSystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE, EffortCertificationParameterFinder.getCostShareSubAccountTypeCode());
        parameters.put(EffortSystemParameters.EXPENSE_SUB_ACCOUNT_TYPE_CODE, EffortCertificationParameterFinder.getExpenseSubAccountTypeCode());

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
}
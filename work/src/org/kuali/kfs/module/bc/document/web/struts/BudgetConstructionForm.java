/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BCKeyLabelPair;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;


public class BudgetConstructionForm extends FinancialSystemTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionForm.class);

    protected PendingBudgetConstructionGeneralLedger newRevenueLine;
    protected PendingBudgetConstructionGeneralLedger newExpenditureLine;
    
    protected static String revenueObjectTypeCodesLookup;
    protected static String expenditureObjectTypeCodesLookup;

    protected boolean closingDocument = false;

    protected boolean hideDetails = false;
    protected boolean pickListClose = false;
    protected boolean securityNoAccess = false;
    protected boolean hideAdjustmentMeasurement = true;
    protected KualiDecimal revenueAdjustmentAmount;
    protected KualiDecimal expenditureAdjustmentAmount;

    protected List<BCKeyLabelPair> pushdownLevelKeyLabels;
    protected List<BCKeyLabelPair> pullupLevelKeyLabels;
    protected String pushdownKeyCode;
    protected String pullupKeyCode;
    protected List<BudgetConstructionAccountOrganizationHierarchy> accountOrgHierLevels;

    // a flag set during initial load to force a benefits calc and 2plg adjustment
    // at the first instance a BC doc becomes editable - which is detected in action execute
    protected boolean checkTwoPlugAdjustment = false;

    // holds Salary Setting associated rows as they looked before applying any DB changes from performSalarySetting
    // this is used to compare before and after state.
    protected HashMap<String, PendingBudgetConstructionGeneralLedger> preSalarySettingRows;

    // passed parms
    protected String backLocation;
    protected String returnAnchor;
    protected String returnFormKey;
    protected Integer universityFiscalYear;
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String subAccountNumber;
    protected boolean pickListMode;
    protected boolean accountReportsExist;
    protected boolean mainWindow = false;

    // holds anchor value to return to when doing doing balance inquiry
    // it is only set when setting up to do balance inquiry and reset in refresh
    protected String balanceInquiryReturnAnchor;

    protected String dashSubAccountNumber;
    protected String dashFinancialSubObjectCode;

    public BudgetConstructionForm() {
        super();

        // create objects used to hold data filled later either from this.populate or Action.loadDocument
        this.setNewExpenditureLine(new PendingBudgetConstructionGeneralLedger());
        this.setNewRevenueLine(new PendingBudgetConstructionGeneralLedger());
        this.setAccountOrgHierLevels(new ArrayList<BudgetConstructionAccountOrganizationHierarchy>());
        this.setPullupLevelKeyLabels(new ArrayList<BCKeyLabelPair>());
        this.setPushdownLevelKeyLabels(new ArrayList<BCKeyLabelPair>());

        this.setDashFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        this.setDashSubAccountNumber(KFSConstants.getDashSubAccountNumber());

        LOG.debug("creating BudgetConstructionForm");
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME;
    }
    
    /**
     * calls super.populate, then populates revenue and expenditure lines
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);
        String methodToCall = this.getMethodToCall();

        // now run through PBGL rev and exp lines
        BudgetConstructionDocument bcDoc = this.getBudgetConstructionDocument();
        if (StringUtils.isNotBlank(methodToCall)) {
            if (methodToCall.equals(BCConstants.INSERT_REVENUE_LINE_METHOD)) {
                PendingBudgetConstructionGeneralLedger revLine = getNewRevenueLine();

                // do uppercase on added lines only, since users can only update amounts on existing lines
                // this should only affect the line since the DD has auto-update=false on refs and collections
                // and only affect fields where xml attribute has forceUppercase="true"
                SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(revLine);

                populateRevenueLine(bcDoc, this.getNewRevenueLine());

            }
            else {
                this.populatePBGLLine(this.getNewRevenueLine());
            }

            if (methodToCall.equals(BCConstants.INSERT_EXPENDITURE_LINE_METHOD)) {
                PendingBudgetConstructionGeneralLedger expLine = getNewExpenditureLine();

                // do uppercase on added lines only, since users can only update amounts on existing lines
                // this should only affect the line since the DD has auto-update=false on refs and collections
                // and only affect fields where xml attribute has forceUppercase="true"
                SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(expLine);

                populateExpenditureLine(bcDoc, this.getNewExpenditureLine());

            }
            else {
                this.populatePBGLLine(this.getNewExpenditureLine());
            }

            // this is needed to retrieve ref objects, esp budgetConstructionMonthly
            populatePBGLLines();

            setDocTypeName(discoverDocumentTypeName());

        }

    }

    /**
     * Sets the persisted request amount to the actual request amount. This is normally used right after a save to the DB If
     * initSalarySetting is true, this also scrapes the current set of Salary Setting lines, including any 2PLG line into
     * preSalarySettingRows. preSalarySettingRows is used after return from Salary Setting to allow detection of any changes
     * 
     * @param initSalarySetting
     */
    public void initializePersistedRequestAmounts(boolean initSalarySetting) {

        if (initSalarySetting) {
            this.setPreSalarySettingRows(new HashMap<String, PendingBudgetConstructionGeneralLedger>());
        }

        BudgetConstructionDocument bcDoc = this.getBudgetConstructionDocument();

        Iterator revenueLines = bcDoc.getPendingBudgetConstructionGeneralLedgerRevenueLines().iterator();
        while (revenueLines.hasNext()) {
            PendingBudgetConstructionGeneralLedger revenueLine = (PendingBudgetConstructionGeneralLedger) revenueLines.next();
            revenueLine.setPersistedAccountLineAnnualBalanceAmount(revenueLine.getAccountLineAnnualBalanceAmount());
        }
        Iterator expenditureLines = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines().iterator();
        while (expenditureLines.hasNext()) {
            PendingBudgetConstructionGeneralLedger expenditureLine = (PendingBudgetConstructionGeneralLedger) expenditureLines.next();
            expenditureLine.setPersistedAccountLineAnnualBalanceAmount(expenditureLine.getAccountLineAnnualBalanceAmount());

            if (initSalarySetting) {
                if ((expenditureLine.getLaborObject() != null && expenditureLine.getLaborObject().isDetailPositionRequiredIndicator()) || expenditureLine.getFinancialObjectCode().equalsIgnoreCase(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG)) {
                    this.getPreSalarySettingRows().put(expenditureLine.getFinancialObjectCode() + expenditureLine.getFinancialSubObjectCode(), expenditureLine);
                }
            }
        }
    }

    /**
     * Sets the persisted request amount to the actual request amount. This is normally used right after a save to the DB.
     */
    public void initializePersistedRequestAmounts() {

        this.initializePersistedRequestAmounts(false);
    }

    /**
     * Populates the push or pull selection lists displayed in the drop down controls used by the pullup or pushdown actions. The
     * population considers the current level of the document and the user's BudgetConstructionDocument type approvals setup in
     * WorkFlow.
     * 
     * @param bcDoc
     * @param levels
     * @param isPull
     */
    public void populatePushPullLevelKeyLabels(BudgetConstructionDocument bcDoc, List<BudgetConstructionAccountOrganizationHierarchy> levels, boolean isPull) {

        if (!levels.isEmpty()) {
            // sanity check
            if (bcDoc.getOrganizationLevelCode() >= 0 && bcDoc.getOrganizationLevelCode() < levels.size()) {
                if (isPull) {
                    pullupLevelKeyLabels.clear();

                    // get the keys to search
                    HashMap<String, BudgetConstructionAccountOrganizationHierarchy> rvwHierMap = new HashMap<String, BudgetConstructionAccountOrganizationHierarchy>();

                    // start at level above current level and get any levels where the user is an approver
                    for (int i = (bcDoc.getOrganizationLevelCode() + 1); i < levels.size(); i++) {
                        BudgetConstructionAccountOrganizationHierarchy rvwHier = levels.get(i);
                        rvwHierMap.put(rvwHier.getOrganizationChartOfAccountsCode() + rvwHier.getOrganizationCode(), rvwHier);
                    }
                    if (!rvwHierMap.isEmpty()) {
                        try {
                            List<Organization> povOrgs = SpringContext.getBean(BudgetConstructionProcessorService.class).getProcessorOrgs(GlobalVariables.getUserSession().getPerson());
                            if (!povOrgs.isEmpty()) {
                                for (Organization povOrg : povOrgs) {
                                    if (rvwHierMap.containsKey(povOrg.getChartOfAccountsCode() + povOrg.getOrganizationCode())) {
                                        BudgetConstructionAccountOrganizationHierarchy level = rvwHierMap.get(povOrg.getChartOfAccountsCode() + povOrg.getOrganizationCode());
                                        SpringContext.getBean(PersistenceService.class).retrieveReferenceObject(level, "organization");
                                        pullupLevelKeyLabels.add(new BCKeyLabelPair(level.getOrganizationLevelCode().toString(), level.getOrganizationLevelCode().toString() + ":" + level.getOrganizationChartOfAccountsCode() + "-" + level.getOrganizationCode() + " " + level.getOrganization().getOrganizationName()));
                                    }
                                }
                            }
                        }
                        catch (Exception e) {
                            // any exception just leaves an empty pullup list
                        }
                    }
                }
                else {
                    pushdownLevelKeyLabels.clear();

                    // start at current doc level and add all that are below current level
                    for (int i = (bcDoc.getOrganizationLevelCode() - 1); i >= 0; i--) {
                        BudgetConstructionAccountOrganizationHierarchy level = levels.get(i);
                        SpringContext.getBean(PersistenceService.class).retrieveReferenceObject(level, "organization");
                        if (level.getOrganizationLevelCode() == 0) {
                            // push list level zero case needs special desc
                            pushdownLevelKeyLabels.add(new BCKeyLabelPair(level.getOrganizationLevelCode().toString(), level.getOrganizationLevelCode().toString() + ":" + level.getOrganizationChartOfAccountsCode() + "-" + level.getOrganizationCode() + " " + "Fiscal Officer Access Level"));
                        }
                        else {
                            pushdownLevelKeyLabels.add(new BCKeyLabelPair(level.getOrganizationLevelCode().toString(), level.getOrganizationLevelCode().toString() + ":" + level.getOrganizationChartOfAccountsCode() + "-" + level.getOrganizationCode() + " " + level.getOrganization().getOrganizationName()));
                        }
                    }
                }
            }
        }
    }


    /**
     * This method iterates over all of the rev and exp lines in the BC document. This is called to refresh ref objects for use by
     * validation
     */
    public void populatePBGLLines() {

        BudgetConstructionDocument bcDoc = this.getBudgetConstructionDocument();
        bcDoc.zeroTotals();

        Iterator revenueLines = bcDoc.getPendingBudgetConstructionGeneralLedgerRevenueLines().iterator();
        while (revenueLines.hasNext()) {
            PendingBudgetConstructionGeneralLedger revenueLine = (PendingBudgetConstructionGeneralLedger) revenueLines.next();
            this.populateRevenueLine(bcDoc, revenueLine);
        }
        Iterator expenditureLines = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines().iterator();
        while (expenditureLines.hasNext()) {
            PendingBudgetConstructionGeneralLedger expenditureLine = (PendingBudgetConstructionGeneralLedger) expenditureLines.next();
            this.populateExpenditureLine(bcDoc, expenditureLine);
        }
    }

    /**
     * Populates a PBGL revenue line bo using values from the struts form. This is in place to make sure that all of the composite
     * key objects have the correct values in them. This also adds line amounts to the revenue totals.
     * 
     * @param revenueLine
     */
    public void populateRevenueLine(BudgetConstructionDocument bcDoc, PendingBudgetConstructionGeneralLedger revenueLine) {
        populatePBGLLine(revenueLine);
        if (revenueLine.getFinancialBeginningBalanceLineAmount() != null) {
            bcDoc.setRevenueFinancialBeginningBalanceLineAmountTotal(bcDoc.getRevenueFinancialBeginningBalanceLineAmountTotal().add(revenueLine.getFinancialBeginningBalanceLineAmount()));
        }
        if (revenueLine.getAccountLineAnnualBalanceAmount() != null) {
            bcDoc.setRevenueAccountLineAnnualBalanceAmountTotal(bcDoc.getRevenueAccountLineAnnualBalanceAmountTotal().add(revenueLine.getAccountLineAnnualBalanceAmount()));
        }
    }

    /**
     * Populates a PBGL expenditure line bo using values from the struts form. This is in place to make sure that all of the
     * composite key objects have the correct values in them. This also adds line amounts to the expenditure totals.
     * 
     * @param expenditureLine
     */
    public void populateExpenditureLine(BudgetConstructionDocument bcDoc, PendingBudgetConstructionGeneralLedger expenditureLine) {
        populatePBGLLine(expenditureLine);
        if (expenditureLine.getFinancialBeginningBalanceLineAmount() != null) {
            bcDoc.setExpenditureFinancialBeginningBalanceLineAmountTotal(bcDoc.getExpenditureFinancialBeginningBalanceLineAmountTotal().add(expenditureLine.getFinancialBeginningBalanceLineAmount()));
        }
        if (expenditureLine.getAccountLineAnnualBalanceAmount() != null) {
            bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(expenditureLine.getAccountLineAnnualBalanceAmount()));
        }
        if ((expenditureLine.getFinancialObjectCode() != null) && (expenditureLine.getFinancialObjectCode().contentEquals(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG))) {
            // 2plg record exists
            bcDoc.setContainsTwoPlug(true);
        }
    }

    /**
     * Populates the dependent fields of objects contained within the PBGL line
     * 
     * @param line
     */
    protected void populatePBGLLine(PendingBudgetConstructionGeneralLedger line) {

        SpringContext.getBean(BudgetDocumentService.class).populatePBGLLine(line);

        // // final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "financialObject",
        // // "financialSubObject", "laborObject", "budgetConstructionMonthly"}));
        // final List REFRESH_FIELDS;
        // if (StringUtils.isNotBlank(line.getFinancialSubObjectCode())) {
        // REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT,
        // KFSPropertyConstants.FINANCIAL_SUB_OBJECT, BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        // }
        // else {
        // REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT,
        // BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        // }
        // // SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(line, REFRESH_FIELDS);
        // SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(line, REFRESH_FIELDS);

    }

    /**
     * Gets the budgetConstructionDocument
     * 
     * @return
     */
    public BudgetConstructionDocument getBudgetConstructionDocument() {
        return (BudgetConstructionDocument) getDocument();
    }

    /**
     * Sets the budgetConstructionDocument
     * 
     * @param budgetConstructionDocument
     */
    public void setBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument) {
        setDocument(budgetConstructionDocument);
    }

    /**
     * Gets the newExpenditureLine attribute.
     * 
     * @return Returns the newExpenditureLine.
     */
    public PendingBudgetConstructionGeneralLedger getNewExpenditureLine() {
        return newExpenditureLine;
    }

    /**
     * Sets the newExpenditureLine attribute value.
     * 
     * @param newExpenditureLine The newExpenditureLine to set.
     */
    public void setNewExpenditureLine(PendingBudgetConstructionGeneralLedger newExpenditureLine) {
        this.newExpenditureLine = newExpenditureLine;
    }

    /**
     * Gets the newRevenueLine attribute.
     * 
     * @return Returns the newRevenueLine.
     */
    public PendingBudgetConstructionGeneralLedger getNewRevenueLine() {
        return newRevenueLine;
    }

    /**
     * Sets the newRevenueLine attribute value.
     * 
     * @param newRevenueLine The newRevenueLine to set.
     */
    public void setNewRevenueLine(PendingBudgetConstructionGeneralLedger newRevenueLine) {
        this.newRevenueLine = newRevenueLine;
    }

    /**
     * Sets the defaults for fields not setable by the user for added lines. This assumes the document has been explictly loaded and
     * contains primary and candidate key values as in BudgetConstructionAction.loadDocument() or that populate reloads the values
     * from the JSP.
     * 
     * @param line
     * @param isRevenue
     */
    public void initNewLine(PendingBudgetConstructionGeneralLedger line, boolean isRevenue) {

        OptionsService optionsService = SpringContext.getBean(OptionsService.class);
        BudgetConstructionDocument tdoc = this.getBudgetConstructionDocument();

        line.setDocumentNumber(tdoc.getDocumentNumber());
        line.setUniversityFiscalYear(tdoc.getUniversityFiscalYear());
        line.setChartOfAccountsCode(tdoc.getChartOfAccountsCode());
        line.setAccountNumber(tdoc.getAccountNumber());
        line.setSubAccountNumber(tdoc.getSubAccountNumber());
        line.setFinancialBalanceTypeCode(optionsService.getOptions(tdoc.getUniversityFiscalYear()).getBaseBudgetFinancialBalanceTypeCd());
        line.setFinancialBeginningBalanceLineAmount(KualiInteger.ZERO);
        line.setAccountLineAnnualBalanceAmount(KualiInteger.ZERO);

        // KFSMI-7828 object type is now reset from object code table in BudgetConstructionAction.insertPBGLLine()
        if (isRevenue) {
            line.setFinancialObjectTypeCode(optionsService.getOptions(tdoc.getUniversityFiscalYear()).getFinObjectTypeIncomecashCode());
        }
        else {
            line.setFinancialObjectTypeCode(optionsService.getOptions(tdoc.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd());
        }

    }

    /**
     * Gets getBudgetConstructionDocument().isBudgetableDocument attribute.
     * 
     * @return Returns getBudgetConstructionDocument().isBudgetableDocument.
     */
    public boolean isBudgetableDocument() {
        return this.getBudgetConstructionDocument().isBudgetableDocument();
    }

    /**
     * Gets the hideDetails attribute.
     * 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * @return hideDetails attribute
     * @see #isHideDetails()
     */
    public boolean getHideDetails() {
        return isHideDetails();
    }

    /**
     * Sets the hideDetails attribute value.
     * 
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the hideAdjustmentMeasurement attribute.
     * 
     * @return Returns the hideAdjustmentMeasurement.
     */
    public boolean isHideAdjustmentMeasurement() {
        return hideAdjustmentMeasurement;
    }

    /**
     * Sets the hideAdjustmentMeasurement attribute value.
     * 
     * @param hideAdjustmentMeasurement The hideAdjustmentMeasurement to set.
     */
    public void setHideAdjustmentMeasurement(boolean hideAdjustmentMeasurement) {
        this.hideAdjustmentMeasurement = hideAdjustmentMeasurement;
    }

    /**
     * Gets the revenueAdjustmentAmount attribute.
     * 
     * @return Returns the revenueAdjustmentAmount.
     */
    public KualiDecimal getRevenueAdjustmentAmount() {
        return revenueAdjustmentAmount;
    }

    /**
     * Sets the revenueAdjustmentAmount attribute value.
     * 
     * @param revenueAdjustmentAmount The revenueAdjustmentAmount to set.
     */
    public void setRevenueAdjustmentAmount(KualiDecimal adjustmentAmount) {
        this.revenueAdjustmentAmount = adjustmentAmount;
    }

    /**
     * Gets the expenditureAdjustmentAmount attribute.
     * 
     * @return Returns the expenditureAdjustmentAmount.
     */
    public KualiDecimal getExpenditureAdjustmentAmount() {
        return expenditureAdjustmentAmount;
    }

    /**
     * Sets the expenditureAdjustmentAmount attribute value.
     * 
     * @param expenditureAdjustmentAmount The expenditureAdjustmentAmount to set.
     */
    public void setExpenditureAdjustmentAmount(KualiDecimal expenditureAdjustmentAmount) {
        this.expenditureAdjustmentAmount = expenditureAdjustmentAmount;
    }

    /**
     * Gets the benefitsCalculationDisabled attribute.
     * 
     * @return Returns the benefitsCalculationDisabled.
     */
    public boolean isBenefitsCalculationDisabled() {
        return SpringContext.getBean(BenefitsCalculationService.class).isBenefitsCalculationDisabled();
    }

    /**
     * Gets the salarySettingDisabled attribute.
     * 
     * @return Returns the salarySettingDisabled.
     */
    public boolean isSalarySettingDisabled() {
        return SpringContext.getBean(SalarySettingService.class).isSalarySettingDisabled();
    }

    /**
     * Gets the pickListMode attribute.
     * 
     * @return Returns the pickListMode.
     */
    public boolean isPickListMode() {
        return pickListMode;
    }

    /**
     * Sets the pickListMode attribute value.
     * 
     * @param pickListMode The pickListMode to set.
     */
    public void setPickListMode(boolean pickListMode) {
        this.pickListMode = pickListMode;
    }

    /**
     * Gets the accountReportsExist attribute.
     * 
     * @return Returns the accountReportsExist.
     */
    public boolean isAccountReportsExist() {
        accountReportsExist = false;

        if (this.getBudgetConstructionDocument().getDocumentNumber() != null) {
            if (SpringContext.getBean(BudgetDocumentService.class).isAccountReportsExist(this.getChartOfAccountsCode(), this.getAccountNumber())) {
                accountReportsExist = true;
            }
        }
        return accountReportsExist;
    }

    /**
     * Sets the accountReportsExist attribute value.
     * 
     * @param accountReportsExist The accountReportsExist to set.
     */
    public void setAccountReportsExist(boolean accountReportsExist) {
        this.accountReportsExist = accountReportsExist;
    }

    /**
     * Gets the pickListClose attribute.
     * 
     * @return Returns the pickListClose.
     */
    public boolean isPickListClose() {
        return pickListClose;
    }

    /**
     * Sets the pickListClose attribute value.
     * 
     * @param pickListClose The pickListClose to set.
     */
    public void setPickListClose(boolean pickListClose) {
        this.pickListClose = pickListClose;
    }

    /**
     * Gets the securityNoAccess attribute.
     * 
     * @return Returns the securityNoAccess.
     */
    public boolean isSecurityNoAccess() {
        return securityNoAccess;
    }

    /**
     * Sets the securityNoAccess attribute value.
     * 
     * @param securityNoAccess The securityNoAccess to set.
     */
    public void setSecurityNoAccess(boolean securityNoAccess) {
        this.securityNoAccess = securityNoAccess;
    }

    /**
     * Gets the backLocation attribute.
     * 
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * Sets the backLocation attribute value.
     * 
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * Gets the returnAnchor attribute.
     * 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * 
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute.
     * 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * 
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Gets the balanceInquiryReturnAnchor attribute.
     * 
     * @return Returns the balanceInquiryReturnAnchor.
     */
    public String getBalanceInquiryReturnAnchor() {
        return balanceInquiryReturnAnchor;
    }

    /**
     * Sets the balanceInquiryReturnAnchor attribute value.
     * 
     * @param balanceInquiryReturnAnchor The balanceInquiryReturnAnchor to set.
     */
    public void setBalanceInquiryReturnAnchor(String balanceInquiryReturnAnchor) {
        this.balanceInquiryReturnAnchor = balanceInquiryReturnAnchor;
    }

    /**
     * Gets the pullupKeyCode attribute.
     * 
     * @return Returns the pullupKeyCode.
     */
    public String getPullupKeyCode() {
        return pullupKeyCode;
    }

    /**
     * Sets the pullupKeyCode attribute value.
     * 
     * @param pullupKeyCode The pullupKeyCode to set.
     */
    public void setPullupKeyCode(String pullupKeyCode) {
        this.pullupKeyCode = pullupKeyCode;
    }

    /**
     * Gets the pullupLevelKeyLabels attribute.
     * 
     * @return Returns the pullupLevelKeyLabels.
     */
    public List<BCKeyLabelPair> getPullupLevelKeyLabels() {
        return pullupLevelKeyLabels;
    }

    /**
     * Sets the pullupLevelKeyLabels attribute value.
     * 
     * @param pullupLevelKeyLabels The pullupLevelKeyLabels to set.
     */
    public void setPullupLevelKeyLabels(List<BCKeyLabelPair> pullupLevelKeyLabels) {
        this.pullupLevelKeyLabels = pullupLevelKeyLabels;
    }

    /**
     * Gets the accountOrgHierLevels attribute.
     * 
     * @return Returns the accountOrgHierLevels.
     */
    public List<BudgetConstructionAccountOrganizationHierarchy> getAccountOrgHierLevels() {
        return this.accountOrgHierLevels;
    }

    /**
     * Sets the accountOrgHierLevels attribute value.
     * 
     * @param accountOrgHierLevels The accountOrgHierLevels to set.
     */
    public void setAccountOrgHierLevels(List<BudgetConstructionAccountOrganizationHierarchy> accountOrgHierLevels) {
        this.accountOrgHierLevels = accountOrgHierLevels;
    }

    /**
     * Gets the pushdownKeyCode attribute.
     * 
     * @return Returns the pushdownKeyCode.
     */
    public String getPushdownKeyCode() {
        return pushdownKeyCode;
    }

    /**
     * Sets the pushdownKeyCode attribute value.
     * 
     * @param pushdownKeyCode The pushdownKeyCode to set.
     */
    public void setPushdownKeyCode(String pushdownKeyCode) {
        this.pushdownKeyCode = pushdownKeyCode;
    }

    /**
     * Gets the pushdownLevelKeyLabels attribute.
     * 
     * @return Returns the pushdownLevelKeyLabels.
     */
    public List<BCKeyLabelPair> getPushdownLevelKeyLabels() {
        return pushdownLevelKeyLabels;
    }

    /**
     * Sets the pushdownLevelKeyLabels attribute value.
     * 
     * @param pushdownLevelKeyLabels The pushdownLevelKeyLabels to set.
     */
    public void setPushdownLevelKeyLabels(List<BCKeyLabelPair> pushdownLevelKeyLabels) {
        this.pushdownLevelKeyLabels = pushdownLevelKeyLabels;
    }

    /**
     * Gets the checkTwoPlugAdjustment attribute.
     * 
     * @return Returns the checkTwoPlugAdjustment.
     */
    public boolean isCheckTwoPlugAdjustment() {
        return checkTwoPlugAdjustment;
    }

    /**
     * Sets the checkTwoPlugAdjustment attribute value.
     * 
     * @param checkTwoPlugAdjustment The checkTwoPlugAdjustment to set.
     */
    public void setCheckTwoPlugAdjustment(boolean checkTwoPlugAdjustment) {
        this.checkTwoPlugAdjustment = checkTwoPlugAdjustment;
    }

    /**
     * Gets the preSalarySettingRows attribute.
     * 
     * @return Returns the preSalarySettingRows.
     */
    public HashMap<String, PendingBudgetConstructionGeneralLedger> getPreSalarySettingRows() {
        return preSalarySettingRows;
    }

    /**
     * Sets the preSalarySettingRows attribute value.
     * 
     * @param preSalarySettingRows The preSalarySettingRows to set.
     */
    public void setPreSalarySettingRows(HashMap<String, PendingBudgetConstructionGeneralLedger> preSalarySettingRows) {
        this.preSalarySettingRows = preSalarySettingRows;
    }

    /**
     * Gets the closingDocument attribute.
     * 
     * @return Returns the closingDocument.
     */
    public boolean isClosingDocument() {
        return closingDocument;
    }

    /**
     * Sets the closingDocument attribute value.
     * 
     * @param closingDocument The closingDocument to set.
     */
    public void setClosingDocument(boolean closingDocument) {
        this.closingDocument = closingDocument;
    }

    /**
     * Helper method to check edit mode Map for system view only entry
     */
    public boolean isSystemViewOnly() {
        return getEditingMode().containsKey(BCConstants.EditModes.SYSTEM_VIEW_ONLY);
    }

    /**
     * Helper method to check document actions Map for can edit entry
     */
    public boolean isEditAllowed() {
        return getDocumentActions().keySet().contains(KRADConstants.KUALI_ACTION_CAN_EDIT);
    }

    /**
     * Gets the mainWindow attribute.
     * 
     * @return Returns the mainWindow.
     */
    public boolean isMainWindow() {
        return mainWindow;
    }

    /**
     * Sets the mainWindow attribute value.
     * 
     * @param mainWindow The mainWindow to set.
     */
    public void setMainWindow(boolean mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Gets the revenueObjectTypeCodesLookup attribute. 
     * @return Returns the revenueObjectTypeCodesLookup.
     */
    public static String getRevenueObjectTypeCodesLookup() {
        if ( revenueObjectTypeCodesLookup == null ) {
            revenueObjectTypeCodesLookup = SpringContext.getBean(BudgetParameterService.class).getLookupObjectTypes(true);
        }
        return revenueObjectTypeCodesLookup;
    }

    /**
     * Gets the expenditureObjectTypeCodesLookup attribute. 
     * @return Returns the expenditureObjectTypeCodesLookup.
     */
    public static String getExpenditureObjectTypeCodesLookup() {
        if ( expenditureObjectTypeCodesLookup == null ) {
            expenditureObjectTypeCodesLookup = SpringContext.getBean(BudgetParameterService.class).getLookupObjectTypes(false);
        }
        return expenditureObjectTypeCodesLookup;
    }

    /**
     * Gets the dashSubAccountNumber attribute.
     * 
     * @return Returns the dashSubAccountNumber
     */
    
    public String getDashSubAccountNumber() {
        return dashSubAccountNumber;
    }

    /**	
     * Sets the dashSubAccountNumber attribute.
     * 
     * @param dashSubAccountNumber The dashSubAccountNumber to set.
     */
    public void setDashSubAccountNumber(String dashSubAccountNumber) {
        this.dashSubAccountNumber = dashSubAccountNumber;
    }

    /**
     * Gets the dashFinancialSubObjectCode attribute.
     * 
     * @return Returns the dashFinancialSubObjectCode
     */
    
    public String getDashFinancialSubObjectCode() {
        return dashFinancialSubObjectCode;
    }

    /**	
     * Sets the dashFinancialSubObjectCode attribute.
     * 
     * @param dashFinancialSubObjectCode The dashFinancialSubObjectCode to set.
     */
    public void setDashFinancialSubObjectCode(String dashFinancialSubObjectCode) {
        this.dashFinancialSubObjectCode = dashFinancialSubObjectCode;
    }
}

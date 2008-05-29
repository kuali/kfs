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
package org.kuali.module.budget.web.struts.form;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.exceptions.BudgetConstructionDocumentAuthorizationException;
import org.kuali.module.budget.service.BenefitsCalculationService;
import org.kuali.module.budget.service.SalarySettingService;


public class BudgetConstructionForm extends KualiTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionForm.class);

    private PendingBudgetConstructionGeneralLedger newRevenueLine;
    private PendingBudgetConstructionGeneralLedger newExpenditureLine;
    
    private boolean hideDetails = false;
    private boolean pickListClose = false;

    // passed parms
    private String returnAnchor;
    private String returnFormKey;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private boolean pickListMode;

    public BudgetConstructionForm() {
        super();

        // create objects used to hold data filled later either from this.populate or Action.loadDocument
        setDocument(new BudgetConstructionDocument());
        this.setNewExpenditureLine(new PendingBudgetConstructionGeneralLedger());
        this.setNewRevenueLine(new PendingBudgetConstructionGeneralLedger());

        LOG.debug("creating BudgetConstructionForm");
    }

    /**
     * calls super.populate, then populates revenue and expenditure lines
     * 
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);
        
        // now run through PBGL rev and exp lines
        String methodToCall = this.getMethodToCall();
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

            if (methodToCall.equals(BCConstants.INSERT_EXPENDITURE_LINE_METHOD)) {
                PendingBudgetConstructionGeneralLedger expLine = getNewExpenditureLine();

                // do uppercase on added lines only, since users can only update amounts on existing lines
                // this should only affect the line since the DD has auto-update=false on refs and collections
                // and only affect fields where xml attribute has forceUppercase="true"
                SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(expLine);

                populateExpenditureLine(bcDoc, this.getNewExpenditureLine());

            }

            // this is needed to retrieve ref objects, esp budgetConstructionMonthly
            populatePBGLLines();

            setDocTypeName(discoverDocumentTypeName());
            
        }

    }

    public void initializePersistedRequestAmounts() {

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
        }
    }

    /**
     * This method iterates over all of the rev and exp lines in the BC document. TODO verify this - and calls
     * prepareAccountingLineForValidationAndPersistence on each one. This is called to refresh ref objects for use by validation
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
    }

    /**
     * Populates the dependent fields of objects contained within the PBGL line
     * 
     * @param line
     */
    private void populatePBGLLine(PendingBudgetConstructionGeneralLedger line) {

        // final List REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { "financialObject",
        // "financialSubObject", "laborObject", "budgetConstructionMonthly"}));
        final List REFRESH_FIELDS;
        if (StringUtils.isNotBlank(line.getFinancialSubObjectCode())) {
            REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT, KFSPropertyConstants.FINANCIAL_SUB_OBJECT, BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        }
        else {
            REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT, BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        }
        // SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(line, REFRESH_FIELDS);
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(line, REFRESH_FIELDS);

    }

    /**
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#populateAuthorizationFields(org.kuali.core.document.authorization.DocumentAuthorizer)
     * 
     * Additionally checks for BC specific exceptions throwing BudgetConstructionDocumentAuthorizationException appropos
     */
    @Override
    public void populateAuthorizationFields(DocumentAuthorizer documentAuthorizer) {

        super.populateAuthorizationFields(documentAuthorizer);
        if (isFormDocumentInitialized()) {

            // graceless hack which takes advantage of the fact that here and only here will we have guaranteed access to the
            // correct DocumentAuthorizer
            if (getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_ORG_APPROVER)) {
                throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "open", getDocument().getDocumentHeader().getDocumentNumber(), "(user not organization approver)", this.isPickListMode());
            }
            if (getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.USER_BELOW_DOC_LEVEL)) {
                throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "open", getDocument().getDocumentHeader().getDocumentNumber(), "(user below document level)", this.isPickListMode());
            }
            if (getEditingMode().containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_IN_ACCOUNT_HIER)) {
                throw new BudgetConstructionDocumentAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "open", getDocument().getDocumentHeader().getDocumentNumber(), "(user not in account's review hierarchy)", this.isPickListMode());
            }
        }
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#useDocumentAuthorizer(org.kuali.core.document.authorization.DocumentAuthorizer)
     * 
     * Uses BudgetConstructionDocumentAuthorizer to get the editMode and set the action flags
     * This uses the BC security model to setup the authorization state
     */
    @Override
    protected void useDocumentAuthorizer(DocumentAuthorizer documentAuthorizer) {
        BudgetConstructionDocumentAuthorizer bcDocumentAuthorizer = (BudgetConstructionDocumentAuthorizer) documentAuthorizer;
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        setEditingMode(bcDocumentAuthorizer.getEditMode(getDocument(), kualiUser));
        
        // use BudgetConstructionDocumentAuthorizer method version using editingMode to set action flags
        setDocumentActionFlags(bcDocumentAuthorizer.getDocumentActionFlags(getDocument(), kualiUser, getEditingMode()));
    }

    public BudgetConstructionDocument getBudgetConstructionDocument() {
        return (BudgetConstructionDocument) getDocument();
    }

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

        if (isRevenue) {
            line.setFinancialObjectTypeCode(optionsService.getOptions(tdoc.getUniversityFiscalYear()).getFinObjectTypeIncomecashCode());
        }
        else {
            line.setFinancialObjectTypeCode(optionsService.getOptions(tdoc.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd());
        }

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

}

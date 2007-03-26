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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.ojb.BudgetConstructionDaoOjb;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;


public class BudgetConstructionForm extends KualiTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionForm.class);
    
    private PendingBudgetConstructionGeneralLedger newRevenueLine;
    private PendingBudgetConstructionGeneralLedger newExpenditureLine;

    private boolean hideDetails = true;


    public BudgetConstructionForm() {
        super();
        setDocument(new BudgetConstructionDocument());
        //TODO not sure these set method calls are even needed here
        this.setNewRevenueLine(new PendingBudgetConstructionGeneralLedger());
        this.setNewExpenditureLine(new PendingBudgetConstructionGeneralLedger());
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
        if (StringUtils.isNotBlank(methodToCall)){
            if (methodToCall.equals(BCConstants.INSERT_REVENUE_LINE_METHOD)){
                PendingBudgetConstructionGeneralLedger revLine = getNewRevenueLine();
                SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(revLine);
                initNewLine(revLine);
                revLine.setFinancialObjectTypeCode(BCConstants.FINANCIAL_OBJECT_TYPE_CODE_REV);
                populateRevenueLine(this.getNewRevenueLine());
            }
            if (methodToCall.equals(BCConstants.INSERT_EXPENDITURE_LINE_METHOD)){
                PendingBudgetConstructionGeneralLedger expLine = getNewExpenditureLine(); 
                SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(expLine);
                initNewLine(expLine);
                expLine.setFinancialObjectTypeCode(BCConstants.FINANCIAL_OBJECT_TYPE_CODE_EXP);
                populateExpenditureLine(this.getNewExpenditureLine());
            }

            populatePBGLLines();
            setDocTypeName(discoverDocumentTypeName());
        }

    }

    /**
     * This sets the default fields not setable by the user for added lines
     * It also uppercases text fields
     * 
     * @param line
     */
    public void initNewLine(PendingBudgetConstructionGeneralLedger line){
        // do uppercase on added lines only, since users can only update amounts on existing lines
        // this should only affect the line since the DD has auto-update=false on refs and collections
        // and only affect fields where xml attribute has forceUppercase="true"
        
        //moved to populate()
        //SpringServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(line);

        BudgetConstructionDocument tdoc = this.getBudgetConstructionDocument();

        line.setDocumentNumber(tdoc.getDocumentNumber());
        line.setUniversityFiscalYear(tdoc.getUniversityFiscalYear());
        line.setChartOfAccountsCode(tdoc.getChartOfAccountsCode());
        line.setAccountNumber(tdoc.getAccountNumber());
        line.setSubAccountNumber(tdoc.getSubAccountNumber());
        line.setFinancialBalanceTypeCode(BCConstants.FINANCIAL_BALANCE_TYPE_CODE_BB);
        if (StringUtils.isBlank(line.getFinancialSubObjectCode())){
            line.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        }
    }
    
    /**
     * This method iterates over all of the rev and exp lines in the BC document, and calls
     * prepareAccountingLineForValidationAndPersistence on each one.
     * This is called because a user could have updated already existing PBGL lines
     * that had blank values in composite key fields.
     */
    protected void populatePBGLLines(){
        Iterator revenueLines = this.getBudgetConstructionDocument().getPendingBudgetConstructionGeneralLedgerRevenueLines().iterator();
        while (revenueLines.hasNext()){
            PendingBudgetConstructionGeneralLedger revenueLine = (PendingBudgetConstructionGeneralLedger) revenueLines.next();
            this.populateRevenueLine(revenueLine);
        }
        Iterator expenditureLines = this.getBudgetConstructionDocument().getPendingBudgetConstructionGeneralLedgerExpenditureLines().iterator();
        while (expenditureLines.hasNext()){
            PendingBudgetConstructionGeneralLedger expenditureLine = (PendingBudgetConstructionGeneralLedger) expenditureLines.next();
            this.populateExpenditureLine(expenditureLine);
        }
    }
    
    /**
     * Populates a PBGL revenue line bo using values from the struts form.
     * This is in place to make sure that all of the composite key objects have the correct values in them.
     * 
     * @param revenueLine
     */
    public void populateRevenueLine(PendingBudgetConstructionGeneralLedger revenueLine){
        populatePBGLLine(revenueLine);
    }
    
    /**
     * Populates a PBGL expenditure line bo using values from the struts form.
     * This is in place to make sure that all of the composite key objects have the correct values in them.
     * 
     * @param expenditureLine
     */
    public void populateExpenditureLine(PendingBudgetConstructionGeneralLedger expenditureLine){
        populatePBGLLine(expenditureLine);
    }
    
    /**
     * Populates the dependent fields of objects contained within the PBGL line
     * that are visible to the user
     * 
     * @param line
     */
    private void populatePBGLLine(PendingBudgetConstructionGeneralLedger line){

        BudgetConstructionDocument tdoc = this.getBudgetConstructionDocument();

        if (ObjectUtils.isNull(line.getFinancialObject())) {
            line.setFinancialObject(new ObjectCode());
        }
        line.getFinancialObject().setUniversityFiscalYear(tdoc.getUniversityFiscalYear());
        line.getFinancialObject().setChartOfAccountsCode(line.getChartOfAccountsCode());

        if (!line.getFinancialSubObjectCode().equalsIgnoreCase(Constants.DASHES_SUB_OBJECT_CODE)){
            if (ObjectUtils.isNull(line.getFinancialSubObject())) {
                line.setFinancialSubObject(new SubObjCd());
            }
            line.getFinancialSubObject().setChartOfAccountsCode(line.getChartOfAccountsCode());
            line.getFinancialSubObject().setAccountNumber(line.getAccountNumber());
            line.getFinancialSubObject().setFinancialObjectCode(line.getFinancialObjectCode());
            line.getFinancialSubObject().setUniversityFiscalYear(tdoc.getUniversityFiscalYear());
        }
    }

    public BudgetConstructionDocument getBudgetConstructionDocument(){
        return (BudgetConstructionDocument) getDocument();
    }

    public void setBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument){
        setDocument(budgetConstructionDocument);
    }

    /**
     * Gets the newExpenditureLine attribute. 
     * @return Returns the newExpenditureLine.
     */
    public PendingBudgetConstructionGeneralLedger getNewExpenditureLine() {
        if (this.newExpenditureLine == null){
            this.setNewExpenditureLine(new PendingBudgetConstructionGeneralLedger());
        }
        return newExpenditureLine;
    }

    /**
     * Sets the newExpenditureLine attribute value.
     * @param newExpenditureLine The newExpenditureLine to set.
     */
    public void setNewExpenditureLine(PendingBudgetConstructionGeneralLedger newExpenditureLine) {
        this.newExpenditureLine = newExpenditureLine;
    }

    /**
     * Gets the newRevenueLine attribute. 
     * @return Returns the newRevenueLine.
     */
    public PendingBudgetConstructionGeneralLedger getNewRevenueLine() {
        if (this.newRevenueLine == null){
            this.setNewRevenueLine(new PendingBudgetConstructionGeneralLedger());
        }
        return newRevenueLine;
    }

    /**
     * Sets the newRevenueLine attribute value.
     * @param newRevenueLine The newRevenueLine to set.
     */
    public void setNewRevenueLine(PendingBudgetConstructionGeneralLedger newRevenueLine) {
        this.newRevenueLine = newRevenueLine;
    }

    /**
     * Gets the hideDetails attribute. 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * 
     * @return hideDetails attribute
     * @see #isHideDetails()
     */
    public boolean getHideDetails() {
        return isHideDetails();
    }

   /**
     * Sets the hideDetails attribute value.
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

   
}

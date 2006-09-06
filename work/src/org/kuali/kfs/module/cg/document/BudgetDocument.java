/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.document;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.IllegalObjectStateException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.DocumentInitiator;
import org.kuali.core.workflow.KualiDocumentXmlMaterializer;
import org.kuali.core.workflow.KualiTransactionalDocumentInformation;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetAbstractCostShare;
import org.kuali.module.kra.budget.bo.BudgetAbstractPeriodCostShare;
import org.kuali.module.kra.budget.bo.BudgetAdHocOrg;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetPeriodUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Budget
 * 
 * @author KRA (era_team@indiana.edu)
 */
public class BudgetDocument extends ResearchDocumentBase {


    private static final long serialVersionUID = -3561859858801995441L;
    private Integer budgetTaskNextSequenceNumber;
    private Integer budgetPeriodNextSequenceNumber;
    private Integer personnelNextSequenceNumber;
    private Integer nonpersonnelNextSequenceNumber;
    private Integer universityCostShareNextSequenceNumber;
    private Integer thirdPartyCostShareNextSequenceNumber;
    boolean forceRefreshOfBOSubListsForSave = true;
    boolean cleanseBudgetOnSave = true;

    private Budget budget;

    /**
     * Default no-arg constructor.
     */
    public BudgetDocument() {
        super();
        budget = new Budget();
        budget.setDocumentHeaderId(this.financialDocumentNumber);
        budgetTaskNextSequenceNumber = new Integer(1);
        budgetPeriodNextSequenceNumber = new Integer(1);
        personnelNextSequenceNumber = new Integer(1);
        nonpersonnelNextSequenceNumber = new Integer(1);

        universityCostShareNextSequenceNumber = new Integer(1);
        thirdPartyCostShareNextSequenceNumber = new Integer(1);
    }

    /**
     * @param o
     */
    public void setBudgetTaskNextSequenceNumber(Integer o) {
        budgetTaskNextSequenceNumber = o;
    }

    /**
     * @return budgetTaskNextSequenceNumber
     */
    public Integer getBudgetTaskNextSequenceNumber() {

        return budgetTaskNextSequenceNumber;
    }

    /**
     * @param o
     */
    public void setBudgetPeriodNextSequenceNumber(Integer o) {
        budgetPeriodNextSequenceNumber = o;
    }

    /**
     * @return budgetPeriodNextSequenceNumber
     */
    public Integer getBudgetPeriodNextSequenceNumber() {
        return budgetPeriodNextSequenceNumber;
    }

    /**
     * @param o
     */
    public void setPersonnelNextSequenceNumber(Integer o) {
        personnelNextSequenceNumber = o;
    }

    /**
     * @return budgetPersonnelNextSequenceNumber
     */
    public Integer getPersonnelNextSequenceNumber() {
        return personnelNextSequenceNumber;
    }

    /**
     * @see org.kuali.module.chart.bo.ResearchAdministrationDocumentBase#generatePEsfromAL(org.kuali.bo.AccountingLineBase, boolean,
     *      int)
     */
    public void generatePEsfromAL(AccountingLineBase line, boolean isSource, int counter) throws IllegalObjectStateException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.core.document.Document#getDocumentTitle()
     */
    public String getDocumentTitle() {
        // TODO Auto-generated method stub
        return "BudgetDocument";
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentHeaderId", this.financialDocumentNumber);

        return m;
    }

    /**
     * @return Returns the budget.
     */
    public Budget getBudget() {
        return budget;
    }

    /**
     * @param budget The budget to set.
     */
    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public int getPeriodListSize() {
        return this.budget.getPeriods().size();
    }

    public int getTaskListSize() {
        return this.budget.getTasks().size();
    }

    public void addPeriod(BudgetPeriod budgetPeriod) {
        if (budgetPeriod.getBudgetPeriodBeginDate() == null) {
            Date defaultNextBeginDate = this.getBudget().getDefaultNextPeriodBeginDate();
            if (defaultNextBeginDate != null) {
                budgetPeriod.setBudgetPeriodBeginDate(defaultNextBeginDate);
            }
        }

        budgetPeriod.setBudgetPeriodSequenceNumber(getBudgetPeriodNextSequenceNumber());
        budgetPeriod.setDocumentHeaderId(this.getFinancialDocumentNumber());
        this.budget.getPeriods().add(budgetPeriod);

        setBudgetPeriodNextSequenceNumber(new Integer(getBudgetPeriodNextSequenceNumber().intValue() + 1));
    }

    public void addTask(BudgetTask budgetTask) {
        budgetTask.setBudgetTaskSequenceNumber(getBudgetTaskNextSequenceNumber());
        budgetTask.setDocumentHeaderId(this.getFinancialDocumentNumber());
        if (this.budget.isAgencyModularIndicator() && this.budget.getTasks().size() == 0) {
            this.budget.getModularBudget().setBudgetModularTaskNumber(budgetTask.getBudgetTaskSequenceNumber());
        }
        this.budget.getTasks().add(budgetTask);

        setBudgetTaskNextSequenceNumber(new Integer(getBudgetTaskNextSequenceNumber().intValue() + 1));
    }

    public void addPersonnel(BudgetUser budgetUser) {
        budgetUser.initializeBudgetUser(this);
        budgetUser.setCurrentTaskNumber(((BudgetTask) this.getBudget().getTasks().get(0)).getBudgetTaskSequenceNumber());
        budget.getPersonnel().add(budgetUser);
        setPersonnelNextSequenceNumber(new Integer(getPersonnelNextSequenceNumber().intValue() + 1));
    }

    public void addNonpersonnel(BudgetNonpersonnel budgetNonpersonnel) {
        budgetNonpersonnel.setBudgetNonpersonnelSequenceNumber(getNonpersonnelNextSequenceNumber());
        this.budget.getNonpersonnelItems().add(budgetNonpersonnel);
        setNonpersonnelNextSequenceNumber(new Integer(getNonpersonnelNextSequenceNumber().intValue() + 1));
    }

    public void addUniversityCostShare(List<BudgetPeriod> periods, BudgetUniversityCostShare budgetUniversityCostShare) {
        budgetUniversityCostShare.setBudgetCostShareSequenceNumber(this.getUniversityCostShareNextSequenceNumber());
        budgetUniversityCostShare.populateKeyFields(this.getFinancialDocumentNumber(), periods);
        
        this.budget.getUniversityCostShareItems().add(budgetUniversityCostShare);
        
        setUniversityCostShareNextSequenceNumber(new Integer(getUniversityCostShareNextSequenceNumber().intValue() + 1));
    }

    public void addThirdPartyCostShare(List<BudgetPeriod> periods, BudgetThirdPartyCostShare budgetThirdPartyCostShare) {
        budgetThirdPartyCostShare.setBudgetCostShareSequenceNumber(this.getThirdPartyCostShareNextSequenceNumber());
        budgetThirdPartyCostShare.populateKeyFields(this.getFinancialDocumentNumber(), periods);

        this.budget.getThirdPartyCostShareItems().add(budgetThirdPartyCostShare);
        
        setThirdPartyCostShareNextSequenceNumber(new Integer(getThirdPartyCostShareNextSequenceNumber().intValue() + 1));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.Document#validate()
     */
    public void validate() throws IllegalObjectStateException {
        // TODO Auto-generated method stub
        super.validate();
    }

    /**
     * Gets the nonpersonnelNextSequenceNumber attribute.
     * 
     * @return Returns the nonpersonnelNextSequenceNumber.
     */
    public Integer getNonpersonnelNextSequenceNumber() {
        return nonpersonnelNextSequenceNumber;
    }

    /**
     * Sets the nonpersonnelNextSequenceNumber attribute value.
     * 
     * @param nonpersonnelNextSequenceNumber The nonpersonnelNextSequenceNumber to set.
     */
    public void setNonpersonnelNextSequenceNumber(Integer nonpersonnelNextSequenceNumber) {
        this.nonpersonnelNextSequenceNumber = nonpersonnelNextSequenceNumber;
    }

    /**
     * TODO Well, since there isn't a common name for the primary id property accross objects, we have to do this clumsily.
     * Eventually everything will be taken care of in the inherited class.
     * 
     */
    public Document copy() throws WorkflowException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Document copyDoc = super.copy();
        ObjectUtils.setObjectPropertyDeep(((BudgetDocument) copyDoc).getBudget(), "documentHeaderId", String.class, copyDoc.getFinancialDocumentNumber());
        return copyDoc;
    }

    /**
     * Gets the forceRefreshOfBOSubListsForSave attribute.
     * 
     * @return Returns the forceRefreshOfBOSubListsForSave.
     */
    public boolean isForceRefreshOfBOSubListsForSave() {
        return forceRefreshOfBOSubListsForSave;
    }

    /**
     * Sets the forceRefreshOfBOSubListsForSave attribute value.
     * 
     * @param forceRefreshOfBOSubListsForSave The forceRefreshOfBOSubListsForSave to set.
     */
    public void setForceRefreshOfBOSubListsForSave(boolean forceRefreshOfBOSubListsForSave) {
        this.forceRefreshOfBOSubListsForSave = forceRefreshOfBOSubListsForSave;
    }

    /**
     * @return Returns the budgetUniversityCostShareNextSequenceNumber.
     */
    public Integer getUniversityCostShareNextSequenceNumber() {
        return universityCostShareNextSequenceNumber;
    }

    /**
     * @param budgetUniversityCostShareNextSequenceNumber The budgetUniversityCostShareNextSequenceNumber to set.
     */
    public void setUniversityCostShareNextSequenceNumber(Integer budgetUniversityCostShareNextSequenceNumber) {
        this.universityCostShareNextSequenceNumber = budgetUniversityCostShareNextSequenceNumber;
    }

    public boolean isCleanseBudgetOnSave() {
        return cleanseBudgetOnSave;
    }

    public void setCleanseBudgetOnSave(boolean cleanseBudgetOnSave) {
        this.cleanseBudgetOnSave = cleanseBudgetOnSave;
    }

    /**
     * @return Returns the budgetThirdPartyCostShareNextSequenceNumber.
     */
    public Integer getThirdPartyCostShareNextSequenceNumber() {
        return thirdPartyCostShareNextSequenceNumber;
    }

    /**
     * @param budgetThirdPartyCostShareNextSequenceNumber The budgetThirdPartyCostShareNextSequenceNumber to set.
     */
    public void setThirdPartyCostShareNextSequenceNumber(Integer budgetThirdPartyCostShareNextSequenceNumber) {
        this.thirdPartyCostShareNextSequenceNumber = budgetThirdPartyCostShareNextSequenceNumber;
    }
    
    @Override
    public List buildListOfDeletionAwareLists() {
        List list = new ArrayList();
            
        Budget budget = this.getBudget();
            list.add(budget.getTasks());
            list.add(budget.getPeriods());
            list.add(budget.getNonpersonnelItems());
            list.add(budget.getAllUserAppointmentTaskPeriods(this.isForceRefreshOfBOSubListsForSave()));
            list.add(budget.getAllUserAppointmentTasks(this.isForceRefreshOfBOSubListsForSave()));
            list.add(budget.getPersonnel());

            list.add(budget.getAllThirdPartyCostSharePeriods(this.isForceRefreshOfBOSubListsForSave()));
            list.add(budget.getThirdPartyCostShareItems());

            list.add(budget.getAllUniversityCostSharePeriods(this.isForceRefreshOfBOSubListsForSave()));
            list.add(budget.getUniversityCostShareItems());

            list.add(budget.getUniversityCostSharePersonnelItems());
            list.add(budget.getAdHocPermissions());
            list.add(budget.getAdHocOrgs()); 

            if (budget.getIndirectCost() != null && budget.getIndirectCost().getBudgetTaskPeriodIndirectCostItems() != null) {
                list.add(budget.getIndirectCost().getBudgetTaskPeriodIndirectCostItems());
            } else {
                list.add(new ArrayList());
            }
            
            if (budget.getModularBudget() != null) {
                list.add(budget.getModularBudget().getBudgetModularPeriods());
            } else {
                list.add(new ArrayList());
            }
            
        return list;
    }
    
    @Override
    public void populateDocumentForRouting() {
        KualiTransactionalDocumentInformation transInfo = new KualiTransactionalDocumentInformation();
        DocumentInitiator initiatior = new DocumentInitiator();
        String initiatorNetworkId = documentHeader.getWorkflowDocument().getInitiatorNetworkId();
        try {
            KualiUser initiatorUser = SpringServiceLocator.getKualiUserService().getUser(new AuthenticationUserId(initiatorNetworkId));
            initiatior.setKualiUser(initiatorUser);
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        transInfo.setDocumentInitiator(initiatior);
        KualiDocumentXmlMaterializer xmlWrapper = new KualiDocumentXmlMaterializer();
        xmlWrapper.setDocument(this);
        xmlWrapper.setKualiTransactionalDocumentInformation(transInfo);
        //String xml = SpringServiceLocator.getXmlObjectSerializerService().toXml(xmlWrapper);
        String xml = buildOrgReportXml(this.getBudget().getAdHocOrgs());
        documentHeader.getWorkflowDocument().getRouteHeader().getDocumentContent().setApplicationContent(xml);
    }
    
    private String buildOrgReportXml(List<BudgetAdHocOrg> orgs) {
        StringBuffer xml = new StringBuffer("<documentContent>");
        for (BudgetAdHocOrg org: orgs) {
            xml.append("<chartOrg><chartOfAccountsCode>");
            xml.append(org.getFiscalCampusCode());
            xml.append("</chartOfAccountsCode><organizationCode>");
            xml.append(org.getPrimaryDepartmentCode());
            xml.append("</organizationCode></chartOrg>");
        }
        xml.append("</documentContent>");
        return xml.toString();
    }

}

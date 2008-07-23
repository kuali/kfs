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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * the base struts form for the detail salary setting: by position or by incumbent
 */
public abstract class DetailSalarySettingForm extends SalarySettingBaseForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailSalarySettingForm.class);

    private PendingBudgetConstructionAppointmentFunding newBCAFLine;

    private boolean addLine;

    private String positionNumber;
    private String emplid;
    private String personName;

    protected SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);

    /**
     * Constructs a DetailSalarySettingForm.java.
     */
    public DetailSalarySettingForm() {
        super();

        this.setEditingMode(new HashMap<String, String>());
        this.setNewBCAFLine(new PendingBudgetConstructionAppointmentFunding());
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        this.populateBCAFLines();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseForm#populateBCAFLines()
     */
    @Override
    public void populateBCAFLines() {
        super.populateBCAFLines();

        this.refreshBCAFLine(this.getNewBCAFLine());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseForm#refreshBCAFLine(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @Override
    public void refreshBCAFLine(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        super.refreshBCAFLine(appointmentFunding);

        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_INTENDED_INCUMBENT);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_ADMINISTRATIVE_POST);
    }

    /**
     * sets the default fields not setable by the user for added lines and any other required initialization
     * 
     * @param appointmentFunding the given appointment funding line
     */
    protected PendingBudgetConstructionAppointmentFunding createNewAppointmentFundingLine() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        
        if (this.isAddLine()) {
            appointmentFunding.setChartOfAccountsCode(this.getChartOfAccountsCode());
            appointmentFunding.setAccountNumber(this.getAccountNumber());
            appointmentFunding.setSubAccountNumber(this.getSubAccountNumber());
            appointmentFunding.setFinancialObjectCode(this.getFinancialObjectCode());
            appointmentFunding.setFinancialSubObjectCode(this.getFinancialSubObjectCode());
        }

        appointmentFunding.setUniversityFiscalYear(this.getUniversityFiscalYear());
        appointmentFunding.setAppointmentFundingDeleteIndicator(false);
        appointmentFunding.setNewLineIndicator(true);
        appointmentFunding.setAppointmentFundingDurationCode(BCConstants.APPOINTMENT_FUNDING_DURATION_DEFAULT);

        return appointmentFunding;
    }

    /**
     * Gets the newBCAFLine attribute.
     * 
     * @return Returns the newBCAFLine.
     */
    public PendingBudgetConstructionAppointmentFunding getNewBCAFLine() {
        return newBCAFLine;
    }

    /**
     * Sets the newBCAFLine attribute value.
     * 
     * @param newBCAFLine The newBCAFLine to set.
     */
    public void setNewBCAFLine(PendingBudgetConstructionAppointmentFunding newBCAFLine) {
        this.newBCAFLine = newBCAFLine;
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the addLine attribute.
     * 
     * @return Returns the addLine.
     */
    public boolean isAddLine() {
        return addLine;
    }

    /**
     * Sets the addLine attribute value.
     * 
     * @param addLine The addLine to set.
     */
    public void setAddLine(boolean addLine) {
        this.addLine = addLine;
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    /**
     * Gets the personName attribute.
     * 
     * @return Returns the personName.
     */
    public String getPersonName() {
        return personName;
    }

    /**
     * Sets the personName attribute value.
     * 
     * @param personName The personName to set.
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * determine whether the editing mode for detail salary setting is in single account mode or not
     */
    private boolean resetSingleAccountModeFlag() {
        PermissionService permissionService = SpringContext.getBean(PermissionService.class);
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        
        if(this.isBudgetByAccountMode()) {
            Account account = new Account();
            account.setAccountNumber(this.getAccountNumber());
            account.setChartOfAccountsCode(this.getChartOfAccountsCode());
            account.refreshReferenceObject(KFSPropertyConstants.ORGANIZATION);
    
            // instruct the detail salary setting by single account mode if current user is an account approver or delegate
            if (permissionService.isAccountManagerOrDelegate(account, personUserIdentifier)) {
                return true;
            }
        }

        // instruct the detail salary setting by multiple account mode if current user is an organization level approver
        List<Org> organizationReviewHierachy = permissionService.getOrganizationReviewHierachy(personUserIdentifier);
        if (organizationReviewHierachy != null && !organizationReviewHierachy.isEmpty()) {
            return false;
        }
        else {
            throw new RuntimeException("Access denied: not authorized to do the detail salary setting");
        }
    }
}

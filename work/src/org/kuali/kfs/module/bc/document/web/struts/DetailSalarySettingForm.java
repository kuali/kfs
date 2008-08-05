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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants.LockStatus;
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

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
    private PermissionService permissionService = SpringContext.getBean(PermissionService.class);
    private LockService lockService = SpringContext.getBean(LockService.class);
    
    private ErrorMap errorMap = GlobalVariables.getErrorMap();

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

        this.setSingleAccountMode(this.resetSingleAccountModeFlag());

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
     * acquire position and funding locks for the given appointment funding
     */
    public boolean acquirePositionAndFundingLocks() {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            boolean gotLocks = this.acquirePositionAndFundingLocks(appointmentFunding);

            if (!gotLocks) {
                return false;
            }
        }
        return true;
    }

    /**
     * acquire position and funding locks for the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @param fieldsHolder the given field holder
     * @return true if the position and funding locks for the given appointment funding are acquired successfully, otherwise, false
     */
    public boolean acquirePositionAndFundingLocks(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.info("acquirePositionAndFundingLocks() started");

        try {
            SalarySettingFieldsHolder fieldsHolder = this.getSalarySettingFieldsHolder();

            // update the access flags of the current funding line
            boolean updated = salarySettingService.updateAccessOfAppointmentFunding(appointmentFunding, fieldsHolder, this.isBudgetByAccountMode(), this.isSingleAccountMode(), this.getUniversalUser());
            if (!updated) {                
                errorMap.putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_FAIL_TO_UPDATE_FUNDING_ACCESS);
                this.releasePositionAndFundingLocks();
                
                LOG.info(BCKeyConstants.ERROR_FAIL_TO_UPDATE_FUNDING_ACCESS);
                return false;
            }

            // not to acquire any lock for the display-only funding line
            if (appointmentFunding.isDisplayOnlyMode() || !appointmentFunding.isBudgetable()) {
                LOG.info("isDisplayOnlyMode || not isBudgetable");
                return true;
            }

            // acquire position lock for the current funding line
            BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
            BudgetConstructionLockStatus positionLockingStatus = lockService.lockPosition(position, this.getUniversalUser());
            if (!LockStatus.SUCCESS.equals(positionLockingStatus.getLockStatus())) {
                errorMap.putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_FAIL_TO_LOCK_POSITION, position.toString());
                this.releasePositionAndFundingLocks();
                
                LOG.info(BCKeyConstants.ERROR_FAIL_TO_LOCK_POSITION);
                return false;
            }

            // acquire funding lock for the current funding line
            BudgetConstructionLockStatus fundingLockingStatus = lockService.lockFunding(appointmentFunding, this.getUniversalUser());
            if (!LockStatus.SUCCESS.equals(fundingLockingStatus.getLockStatus())) {
                errorMap.putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_FAIL_TO_LOCK_FUNDING, appointmentFunding.toString());
                this.releasePositionAndFundingLocks();
                
                LOG.info(BCKeyConstants.ERROR_FAIL_TO_LOCK_FUNDING);
                return false;
            }
        }
        catch (Exception e) {
            this.releasePositionAndFundingLocks();

            LOG.error("Failed when acquiring position/funding lock for " + appointmentFunding + "." + e);
            throw new RuntimeException("Failed when acquiring transaction lock for " + appointmentFunding + "." + e);
        }

        return true;
    }

    /**
     * acquire transaction locks for the savable appointment fundings
     * 
     * @return true if the transaction locks for all savable appointment fundings are acquired successfully, otherwise, false
     */
    public boolean acquireTransactionLocks() {
        for (PendingBudgetConstructionAppointmentFunding fundingLine : this.getSavableAppointmentFundings()) {
            try {
                BudgetConstructionLockStatus lockStatus = lockService.lockTransaction(fundingLine, this.getUniversalUser());

                if (!LockStatus.SUCCESS.equals(lockStatus.getLockStatus())) {
                    errorMap.putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_FAIL_TO_ACQUIRE_TRANSACTION_LOCK, fundingLine.toString());

                    this.releaseTransactionLocks();
                    return false;
                }
            }
            catch (Exception e) {
                this.releaseTransactionLocks();

                LOG.error("Failed when acquiring transaction lock for " + fundingLine + "." + e);
                throw new RuntimeException("Failed when acquiring transaction lock for " + fundingLine + "." + e);
            }
        }

        return true;
    }

    /**
     * release all position and funding locks acquired in current action by the current user
     */
    public void releasePositionAndFundingLocks() {
        List<PendingBudgetConstructionAppointmentFunding> releasableAppointmentFundings = this.getReleasableAppointmentFundings();
        lockService.unlockFunding(releasableAppointmentFundings, this.getUniversalUser());

        Set<BudgetConstructionPosition> lockedPositionSet = new HashSet<BudgetConstructionPosition>();
        for (PendingBudgetConstructionAppointmentFunding fundingLine : releasableAppointmentFundings) {
            lockedPositionSet.add(fundingLine.getBudgetConstructionPosition());
        }

        List<BudgetConstructionPosition> lockedPositions = new ArrayList<BudgetConstructionPosition>();
        lockedPositions.addAll(lockedPositionSet);
        lockService.unlockPostion(lockedPositions, this.getUniversalUser());
    }

    /**
     * release all the transaction locks acquired in current action by the current user
     */
    public void releaseTransactionLocks() {
        List<PendingBudgetConstructionAppointmentFunding> fundingsWithTransactionLocks = this.getReleasableAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : fundingsWithTransactionLocks) {
            lockService.unlockTransaction(appointmentFunding, this.getUniversalUser());
        }
    }

    /**
     * get the appointment fundings that can be saved
     */
    public List<PendingBudgetConstructionAppointmentFunding> getSavableAppointmentFundings() {
        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();

        // get the funding lines that can be saved
        for (PendingBudgetConstructionAppointmentFunding fundingLine : this.getAppointmentFundings()) {
            if (!fundingLine.isDisplayOnlyMode() && fundingLine.isBudgetable()) {
                savableAppointmentFundings.add(fundingLine);
            }
        }
        return savableAppointmentFundings;
    }

    /**
     * get the appointment fundings for which the position or funding locks can be released
     */
    public List<PendingBudgetConstructionAppointmentFunding> getReleasableAppointmentFundings() {
        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = this.getSavableAppointmentFundings();

        List<PendingBudgetConstructionAppointmentFunding> releasableAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        releasableAppointmentFundings.addAll(savableAppointmentFundings);

        return releasableAppointmentFundings;
    }

    /**
     * sets the default fields not setable by the user for added lines and any other required initialization
     * 
     * @param appointmentFunding the given appointment funding line
     */
    public PendingBudgetConstructionAppointmentFunding createNewAppointmentFundingLine() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();

        if (this.isAddLine() || this.isSingleAccountMode()) {
            appointmentFunding.setChartOfAccountsCode(this.getChartOfAccountsCode());
            appointmentFunding.setAccountNumber(this.getAccountNumber());
            appointmentFunding.setSubAccountNumber(this.getSubAccountNumber());
            appointmentFunding.setFinancialObjectCode(this.getFinancialObjectCode());
            appointmentFunding.setFinancialSubObjectCode(this.getFinancialSubObjectCode());
        }

        appointmentFunding.setUniversityFiscalYear(this.getUniversityFiscalYear());
        appointmentFunding.setAppointmentFundingDeleteIndicator(false);
        appointmentFunding.setNewLineIndicator(true);
        appointmentFunding.setAppointmentFundingDurationCode(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);

        return appointmentFunding;
    }

    /**
     * pick up the appointment fundings belonging to the specified account from a collection of fundings that are associated with a
     * position/incumbent.
     */
    public void pickAppointmentFundingsForSingleAccount() {
        List<PendingBudgetConstructionAppointmentFunding> excludedFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();        
        List<String> keyFields = this.getComparableFields();

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : this.getAppointmentFundings()) {              
            if (!ObjectUtil.equals(appointmentFunding, this, keyFields)) {
                excludedFundings.add(appointmentFunding);
            }
        }

        this.getAppointmentFundings().removeAll(excludedFundings);
    }

    /**
     * get the names of comparable fields that are considered to determine a single account, that is, the fundings are considered
     * being assocated with the given account if they have the same values of the fields as specified.
     */
    private List<String> getComparableFields() {
        List<String> comparableFields = new ArrayList<String>();        
        comparableFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        comparableFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        comparableFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        comparableFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        comparableFields.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        return comparableFields;
    }

    /**
     * determine whether the editing mode for detail salary setting is in single account mode or not
     */
    private boolean resetSingleAccountModeFlag() {
        if (this.isBudgetByAccountMode()) {
            Account account = new Account();
            account.setAccountNumber(this.getAccountNumber());
            account.setChartOfAccountsCode(this.getChartOfAccountsCode());
            account.refreshReferenceObject(KFSPropertyConstants.ORGANIZATION);

            // instruct the detail salary setting by single account mode if current user is an account approver or delegate
            if (permissionService.isAccountManagerOrDelegate(account, this.getUniversalUser())) {
                return true;
            }
        }

        // instruct the detail salary setting by multiple account mode if current user is an organization level approver
        List<Org> organizationReviewHierachy = permissionService.getOrganizationReviewHierachy(this.getUniversalUser());
        if (organizationReviewHierachy != null && !organizationReviewHierachy.isEmpty()) {
            return false;
        }
        else {
            throw new RuntimeException("Access denied: not authorized to do the detail salary setting");
        }
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
}

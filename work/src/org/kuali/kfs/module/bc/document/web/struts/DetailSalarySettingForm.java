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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.LockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.MessageMap;

/**
 * the base struts form for the detail salary setting: by position or by incumbent
 */
public abstract class DetailSalarySettingForm extends SalarySettingBaseForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailSalarySettingForm.class);

    private PendingBudgetConstructionAppointmentFunding newBCAFLine;
    private boolean addLine;

    private String positionNumber;
    private String emplid;
    private String name;

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
    private LockService lockService = SpringContext.getBean(LockService.class);
    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

    private static final List<String> comparableFields = getComparableFields();

    /**
     * Constructs a DetailSalarySettingForm.java.
     */
    public DetailSalarySettingForm() {
        super();

        this.setEditingMode(new HashMap<String, String>());
        this.setNewBCAFLine(new PendingBudgetConstructionAppointmentFunding());
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
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
     * acquire position and funding locks for all appointment fundings
     */
    public boolean acquirePositionAndFundingLocks(MessageMap errorMap) {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            boolean gotLocks = this.acquirePositionAndFundingLocks(appointmentFunding, errorMap);

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
     * @return true if the position and funding locks for the given appointment funding are acquired successfully, otherwise, false
     */
    public boolean acquirePositionAndFundingLocks(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        LOG.debug("acquirePositionAndFundingLocks() started");

        try {
            // not to acquire any lock for the display-only and non-budgetable funding line
            if (appointmentFunding.isDisplayOnlyMode() || !appointmentFunding.isBudgetable()) {
                return true;
            }

            // acquire position lock for the current funding line
            BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
            String positionNumber = position.getPositionNumber();
            Integer universityFiscalYear = position.getUniversityFiscalYear();
            String principalId = this.getPerson().getPrincipalId();
            Boolean positionWasAlreadyLocked = lockService.isPositionLockedByUser(positionNumber, universityFiscalYear, principalId);
            if (!positionWasAlreadyLocked) {
                BudgetConstructionLockStatus positionLockingStatus = lockService.lockPosition(position, this.getPerson());
                if (!LockStatus.SUCCESS.equals(positionLockingStatus.getLockStatus())) {
                    errorMap.putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_FAIL_TO_LOCK_POSITION, "Position Number:"+position.getPositionNumber()+", Fiscal Year:"+position.getUniversityFiscalYear().toString()+", Desc:"+position.getPositionDescription()+", Locked By:"+position.getPositionLockUser().getPrincipalName());

                    // gwp - added if test, unlock all others only when initially loading the screen
                    // not during the add line action
                    if (!appointmentFunding.isNewLineIndicator()) {
                        this.releasePositionAndFundingLocks();
                    }
                    return false;
                }
            }

            // acquire funding lock for the current funding line
            BudgetConstructionLockStatus fundingLockingStatus = this.getLockStatusForBudgetByAccountMode(appointmentFunding);
            if (fundingLockingStatus == null) {
                fundingLockingStatus = lockService.lockFunding(appointmentFunding, this.getPerson());
            }

            if (!LockStatus.SUCCESS.equals(fundingLockingStatus.getLockStatus())) {
                String lockUserName = SpringContext.getBean(PersonService.class).getPerson(fundingLockingStatus.getAccountLockOwner()).getPrincipalName();
                errorMap.putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_FAIL_TO_LOCK_FUNDING, appointmentFunding.getAppointmentFundingString()+", Document Locked By:"+lockUserName);

                // gwp - added if test, unlock all others only when initially loading the screen
                // not during the add line action
                if (!appointmentFunding.isNewLineIndicator()) {
                    this.releasePositionAndFundingLocks();
                }
                else {
                    // adding a new line, just release the earlier position lock
                    // if we just issued it above, not from other line
                    if (!positionWasAlreadyLocked) {
                        lockService.unlockPosition(positionNumber, universityFiscalYear, principalId);
                    }
                }
                return false;
            }
        }
        catch (Exception e) {
            this.releasePositionAndFundingLocks();

            String errorMessage = "Failed when acquiring position/funding lock for " + appointmentFunding;
            LOG.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }

        return true;
    }

    /**
     * update the access modes of all appointment fundings
     */
    public boolean updateAccessMode(MessageMap errorMap) {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            boolean accessModeUpdated = this.updateAccessMode(appointmentFunding, errorMap);

            if (!accessModeUpdated) {
                return false;
            }
        }
        return true;
    }

    /**
     * update the access mode of the given appointment funding
     *
     * @param appointmentFunding the given appointment funding
     * @return true if the access mode of the given appointment funding are updated successfully, otherwise, false
     */
    public boolean updateAccessMode(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        LOG.debug("updateAccessMode() started");

        try {
            SalarySettingFieldsHolder fieldsHolder = this.getSalarySettingFieldsHolder();

            // update the access flags of the current funding line
            boolean updated = salarySettingService.updateAccessOfAppointmentFunding(appointmentFunding, fieldsHolder, this.isBudgetByAccountMode(), this.isEditAllowed(), this.getPerson());
            if (!updated) {
                errorMap.putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_FAIL_TO_UPDATE_FUNDING_ACCESS, appointmentFunding.getAppointmentFundingString());
                return false;
            }
        }
        catch (Exception e) {
            String errorMessage = "Failed to update the access mode of " + appointmentFunding + ".";
            LOG.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }

        return true;
    }

    /**
     * acquire transaction locks for the savable appointment fundings
     *
     * @return true if the transaction locks for all savable appointment fundings are acquired successfully, otherwise, false
     */
    public boolean acquireTransactionLocks(MessageMap messageMap) {
        LOG.debug("acquireTransactionLocks() started");

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : this.getSavableAppointmentFundings()) {
            try {
                BudgetConstructionLockStatus transactionLockStatus = this.getLockStatusForBudgetByAccountMode(appointmentFunding);
                if (transactionLockStatus == null) {
                    transactionLockStatus = lockService.lockTransaction(appointmentFunding, this.getPerson());
                }

                if (!LockStatus.SUCCESS.equals(transactionLockStatus.getLockStatus())) {
                    messageMap.putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_FAIL_TO_ACQUIRE_TRANSACTION_LOCK, appointmentFunding.getAppointmentFundingString());

                    this.releaseTransactionLocks();
                    return false;
                }
            }
            catch (Exception e) {
                this.releaseTransactionLocks();
                this.releasePositionAndFundingLocks();

                LOG.error("Failed when acquiring transaction lock for " + appointmentFunding, e);
                throw new RuntimeException("Failed when acquiring transaction lock for " + appointmentFunding, e);
            }
        }

        return true;
    }

    /**
     * release all position and funding locks acquired in current action by the current user
     */
    public void releasePositionAndFundingLocks() {
        LOG.debug("releasePositionAndFundingLocks() started");

        List<PendingBudgetConstructionAppointmentFunding> releasableAppointmentFundings = this.getReleasableAppointmentFundings();
        lockService.unlockFunding(releasableAppointmentFundings, this.getPerson());

        Set<BudgetConstructionPosition> lockedPositionSet = new HashSet<BudgetConstructionPosition>();
        for (PendingBudgetConstructionAppointmentFunding fundingLine : releasableAppointmentFundings) {
            lockedPositionSet.add(fundingLine.getBudgetConstructionPosition());
            LOG.info("fundingLine: " + fundingLine);
        }

        LOG.info("releasePositionAndFundingLocks()" + lockedPositionSet);
        List<BudgetConstructionPosition> lockedPositions = new ArrayList<BudgetConstructionPosition>();
        lockedPositions.addAll(lockedPositionSet);
        lockService.unlockPostion(lockedPositions, this.getPerson());
        for (BudgetConstructionPosition position : lockedPositionSet) {
            LOG.info("fundingLine: " + position.getPositionLockUserIdentifier());
        }
    }

    /**
     * release all the transaction locks acquired in current action by the current user
     */
    public void releaseTransactionLocks() {
        LOG.debug("releaseTransactionLocks() started");

        List<PendingBudgetConstructionAppointmentFunding> fundingsWithTransactionLocks = this.getReleasableAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : fundingsWithTransactionLocks) {
            lockService.unlockTransaction(appointmentFunding, this.getPerson());
        }
    }

    /**
     * get the appointment fundings that can be saved
     */
    public List<PendingBudgetConstructionAppointmentFunding> getSavableAppointmentFundings() {
        LOG.debug("getSavableAppointmentFundings() started");

        // get the funding lines that can be saved
        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        for (PendingBudgetConstructionAppointmentFunding fundingLine : this.getAppointmentFundings()) {

            // save-able line is one that is edit-able
            // rules should catch attempts to save active, !purged, !isBudgetable lines
            if (!fundingLine.isDisplayOnlyMode()) {
                savableAppointmentFundings.add(fundingLine);
            }
        }
        return savableAppointmentFundings;
    }

    /**
     * get the appointment fundings for which the position or funding locks can be released
     */
    public List<PendingBudgetConstructionAppointmentFunding> getReleasableAppointmentFundings() {
        LOG.debug("getReleasableAppointmentFundings() started");

        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = this.getSavableAppointmentFundings();
        List<PendingBudgetConstructionAppointmentFunding> releasableAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        releasableAppointmentFundings.addAll(savableAppointmentFundings);

        return releasableAppointmentFundings;
    }

    /**
     * determine whether there is any active funding line in the given savable appointment funding lines
     */
    public List<PendingBudgetConstructionAppointmentFunding> getActiveFundingLines() {
        LOG.debug("getActiveFundingLines() started");

        List<PendingBudgetConstructionAppointmentFunding> activeFundingLines = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : this.getSavableAppointmentFundings()) {
            if (!appointmentFunding.isAppointmentFundingDeleteIndicator()) {
                activeFundingLines.add(appointmentFunding);
            }
        }
        return activeFundingLines;
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

            // empty field implies dashes and is fixed in add action
            if (this.getSubAccountNumber().equals(KFSConstants.getDashSubAccountNumber())) {
                appointmentFunding.setSubAccountNumber(KFSConstants.EMPTY_STRING);
            }
            else {
                appointmentFunding.setSubAccountNumber(this.getSubAccountNumber());
            }
            appointmentFunding.setFinancialObjectCode(this.getFinancialObjectCode());

            // empty field implies dashes and is fixed in add action
            if (this.getFinancialSubObjectCode().equals(KFSConstants.getDashFinancialSubObjectCode())) {
                appointmentFunding.setFinancialSubObjectCode(KFSConstants.EMPTY_STRING);
            }
            else {
                appointmentFunding.setFinancialSubObjectCode(this.getFinancialSubObjectCode());
            }
        }

        appointmentFunding.setUniversityFiscalYear(this.getUniversityFiscalYear());
        appointmentFunding.setAppointmentFundingDeleteIndicator(false);
        appointmentFunding.setNewLineIndicator(true);
        appointmentFunding.setAppointmentFundingDurationCode(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);

        return appointmentFunding;
    }

    /**
     * pick up the appointment fundings belonging to the specified account from a collection of fundings that are associated with a
     * position/incumbent
     */
    public void pickAppointmentFundingsForSingleAccount() {
        LOG.debug("pickAppointmentFundingsForSingleAccount() started");

        List<PendingBudgetConstructionAppointmentFunding> excludedFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : this.getAppointmentFundings()) {
            if (!ObjectUtil.equals(appointmentFunding, this, comparableFields)) {
                excludedFundings.add(appointmentFunding);
            }
        }

        this.getAppointmentFundings().removeAll(excludedFundings);
    }

    /**
     * Funding/transaction locks are not required for the lines associated with a document already open in budget by account mode
     */
    private BudgetConstructionLockStatus getLockStatusForBudgetByAccountMode(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("getLockStatusForBudgetByAccountMode() started");

        BudgetConstructionLockStatus transactionLockStatus = null;

        if (this.isBudgetByAccountMode() && ObjectUtil.equals(appointmentFunding, this, comparableFields)) {
            transactionLockStatus = new BudgetConstructionLockStatus();
            transactionLockStatus.setLockStatus(LockStatus.SUCCESS);
        }

        return transactionLockStatus;
    }

    /**
     * get the names of comparable fields that are considered to determine a single account, that is, the fundings are considered
     * being assocated with the given account if they have the same values of the fields as specified.
     */
    public static List<String> getComparableFields() {
        List<String> comparableFields = new ArrayList<String>();
        comparableFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        comparableFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        comparableFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        return comparableFields;
    }

    /**
     * determine whether the editing mode for detail salary setting is in single account mode or not
     */
    private boolean resetSingleAccountModeFlag() {

        List<Organization> processorOrgs = SpringContext.getBean(BudgetConstructionProcessorService.class).getProcessorOrgs(this.getPerson());
        Boolean isOrgApprover = processorOrgs != null && !processorOrgs.isEmpty();

        if (this.isBudgetByAccountMode()) {

            Account account = new Account();
            account.setAccountNumber(this.getAccountNumber());
            account.setChartOfAccountsCode(this.getChartOfAccountsCode());
            account = (Account) businessObjectService.retrieve(account);

            RoleService roleService = KimApiServiceLocator.getRoleService();
            Map<String,String> qualification = new HashMap<String,String>();
            qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
            qualification.put(KfsKimAttributes.ACCOUNT_NUMBER, getAccountNumber());
            qualification.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);

            List<String> roleId = new ArrayList<String>();
            roleId.add(roleService.getRoleIdByNamespaceCodeAndName(KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME));

            Boolean isFiscalOfficerOrDelegate = roleService.principalHasRole(getPerson().getPrincipalId(), roleId, qualification);

            return (isFiscalOfficerOrDelegate && !isOrgApprover);
        }

        // instruct the detail salary setting by multiple account mode if current user is an organization level approver
        if (isOrgApprover) {
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
     * Gets the name attribute.
     *
     * @return Returns the name.
     */
    public String name() {
        return name;
    }

    /**
     * Sets the name attribute value.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#shouldPropertyBePopulatedInForm(java.lang.String,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request) {

        if (super.shouldPropertyBePopulatedInForm(requestParameterName, request)) {
            return true;
        }
        else {
            // make sure special disabled fields are allowed to be populated
            if (requestParameterName.endsWith(BCPropertyConstants.APPOINTMENT_REQUESTED_CSF_AMOUNT) || requestParameterName.endsWith(BCPropertyConstants.APPOINTMENT_REQUESTED_CSF_TIME_PERCENT) || requestParameterName.endsWith(BCPropertyConstants.APPOINTMENT_FUNDING_REASON_AMOUNT)
                    || requestParameterName.endsWith(BCPropertyConstants.APPOINTMENT_CHART_OF_ACCOUNT)) {
                return true;
            }
            else {
                return false;
            }
        }
    }
}

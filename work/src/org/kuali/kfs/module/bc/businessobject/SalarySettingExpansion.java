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

package org.kuali.kfs.module.bc.businessobject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.context.SpringContext;


public class SalarySettingExpansion extends PendingBudgetConstructionGeneralLedger implements PendingBudgetConstructionAppointmentFundingAware {

    private List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding;

    /**
     * Constructs a SalarySettingExpansion.java.
     */
    public SalarySettingExpansion() {
        super();

        pendingBudgetConstructionAppointmentFunding = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
    }

    /**
     * Gets the pendingBudgetConstructionAppointmentFunding attribute.
     *
     * @return Returns the pendingBudgetConstructionAppointmentFunding.
     */
    public List<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFunding() {
        return pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * Sets the pendingBudgetConstructionAppointmentFunding attribute value.
     *
     * @param pendingBudgetConstructionAppointmentFunding The pendingBudgetConstructionAppointmentFunding to set.
     */
    @Deprecated
    public void setPendingBudgetConstructionAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding) {
        this.pendingBudgetConstructionAppointmentFunding = pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * determine whehter the salary is paid at hourly rate
     *
     * @return true if the salary is paid at hourly rate; otherwise, false
     */
    public boolean isHourlyPaid() {
        Integer fiscalYear = this.getUniversityFiscalYear();
        String chartOfAccountsCode = this.getChartOfAccountsCode();
        String objectCode = this.getFinancialObjectCode();

        return SpringContext.getBean(SalarySettingService.class).isHourlyPaidObject(fiscalYear, chartOfAccountsCode, objectCode);
    }

    /**
     * build the given salary expansion key string
     */
    public String getSalarySettingExpansionString() {
        String pattern = "{0};{1};{2};{3};{4}";

        return MessageFormat.format(pattern, this.getChartOfAccountsCode(), this.getAccountNumber(), this.getSubAccountNumber(), this.getFinancialObjectCode(), this.getFinancialSubObjectCode());
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {

        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(this.getPendingBudgetConstructionAppointmentFunding());

        List<BudgetConstructionAppointmentFundingReason> appointmentFundingReasons = new ArrayList<BudgetConstructionAppointmentFundingReason>();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding: this.getPendingBudgetConstructionAppointmentFunding()){
            appointmentFundingReasons.addAll(appointmentFunding.getBudgetConstructionAppointmentFundingReason());
        }
        managedLists.add(appointmentFundingReasons);

        return managedLists;
    }
}

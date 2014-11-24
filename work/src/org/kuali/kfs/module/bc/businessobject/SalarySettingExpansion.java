/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

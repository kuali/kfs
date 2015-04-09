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
package org.kuali.kfs.module.bc.document.validation;

import java.util.List;

import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Rule classes wishing to respond to that event should implement this interface.
 */
public interface SalarySettingRule extends BusinessRule {
    
    /**
     * process the rules before the given appointment funding is saved
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the appointment funding can pass all rule before saved, otherwise, false
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, SynchronizationCheckType synchronizationCheckType);

    /**
     * process the rules before the given appointment funding is saved
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the appointment funding can pass all rule before saved, otherwise, false
     */
    public boolean processQuickSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * process the rules before the given appointment funding is created
     * 
     * @param existingAppointmentFundings the existing appointment fundings
     * @param appointmentFunding the given appointment funding
     * @return true if the appointment funding can pass all rule before created, otherwise, false
     */
    public boolean processAddAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding, SynchronizationCheckType synchronizationCheckType);

    /**
     * process the rules before adjusting the salary amount of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the appointment funding can pass all rule before adjusting, otherwise, false
     */
    public boolean processAdjustSalaraySettingLinePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * process the rules before the pay rate and salary amount of the given appointment funding can be normalized
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the appointment funding can pass all rule before normalizing, otherwise, false
     */
    public boolean processNormalizePayrateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding);
}

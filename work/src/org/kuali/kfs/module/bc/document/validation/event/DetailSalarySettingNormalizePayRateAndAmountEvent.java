/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.validation.event;

import java.util.Collection;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.validation.DetailSalarySettingSaveRule;

/**
 * Event triggered when the detail salary setting screen is saved.
 */
public class DetailSalarySettingNormalizePayRateAndAmountEvent extends BudgetExpansionEvent {
    Collection<PendingBudgetConstructionAppointmentFunding> appointmentFunding;


    public DetailSalarySettingNormalizePayRateAndAmountEvent(String errorPathPrefix, Collection<PendingBudgetConstructionAppointmentFunding> appointmentFunding) {
        super(errorPathPrefix);
        this.appointmentFunding = appointmentFunding;
    }

    @Override
    public Class getExpansionRuleInterfaceClass() {
        return DetailSalarySettingSaveRule.class;
    }

    @Override
    public boolean invokeExpansionRuleMethod(BusinessRule rule) {
        return ((DetailSalarySettingSaveRule) rule).processNormalizePayrateAndAmount(appointmentFunding);
    }

}

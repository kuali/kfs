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
package org.kuali.kfs.module.bc.document.service;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.rice.krad.util.MessageMap;

/**
 * define a set of validations methods for buddget construction
 */
public interface BudgetConstructionRuleHelperService {

    /**
     * test if the given appointment funding is associated with a labor detail allowed object
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a labor detail allowed object; otherwise, false
     */
    public boolean hasDetailPositionRequiredObjectCode(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * test if the given appointment funding is associated with a valid account
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a valid account; otherwise, false
     */
    public boolean hasValidAccount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * test if the given appointment funding is associated with a valid chart of accounts
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a valid chart of accounts; otherwise, false
     */
    public boolean hasValidChart(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * test if the given appointment funding is associated with a valid incumbent
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a valid incumbent; otherwise, false
     */
    public boolean hasValidIncumbent(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * separate quick salary setting test if the given appointment funding is associated with a valid incumbent
     *
     * @param appointmentFunding
     * @param errorMap
     * @return
     */
    public boolean hasValidIncumbentQuickSalarySetting(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * test if the given appointment funding is associated with a valid financial object
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a valid financial object; otherwise, false
     */
    public boolean hasValidObjectCode(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * test if the given appointment funding is associated with a valid budget position
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a valid budget position; otherwise, false
     */
    public boolean hasValidPosition(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * test if the given appointment funding is associated with a valid sub account
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a valid sub account; otherwise, false
     */
    public boolean hasValidSubAccount(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * test if the given appointment funding is associated with a valid sub object
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the error map that contains errors if any
     * @return true if the given appointment funding is associated with a valid sub object; otherwise, false
     */
    public boolean hasValidSubObjectCode(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the given appointment funding can be associated with a valid budget construction document
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given appointment funding can be associated with a valid budget construction document; otherwise, false
     */
    public boolean isAssociatedWithValidDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap, String errorPropertyName);

    /**
     * determine wether the given budget document is allowed to be budgeted
     *
     * @param budgetConstructionDocument the given budget document
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given budget document can be budgeted; otherwise, false
     */
    public boolean isBudgetableDocument(BudgetConstructionDocument budgetConstructionDocument, MessageMap errorMap, String errorPropertyName);

    /**
     * determine whether the given object requires a detail position
     *
     * @param financialObject the given financial object
     * @param currentValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given object requires a detail position; otherwise, false
     */
    public boolean isDetailPositionRequiredObjectCode(ObjectCode financialObject, String currentValue, MessageMap errorMap, String errorPropertyName);

    /**
     * determine if the fields in the given appointment funding line are in the correct formats defined in the data dictionary
     *
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @return true if the fields in the given appointment funding line are in the correct formats defined in the data dictionary;
     *         otherwise, false
     */
    public boolean isFieldFormatValid(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap);

    /**
     * determine whether the given account is valid
     *
     * @param account the given account
     * @param currentValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given account is valid; otherwise, false
     */
    public boolean isValidAccount(Account account, String currentValue, MessageMap errorMap, String errorPropertyName);

    /**
     * determine whether the given chart is valid
     *
     * @param chart the given chart
     * @param invalidValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given chart is valid; otherwise, false
     */
    public boolean isValidChart(Chart chart, String currentValue, MessageMap errorMap, String errorPropertyName);

    /**
     * determine whether the given budget construction intended incumbent is valid
     *
     * @param intendedIncumbent the given budget construction intended incumbent
     * @param currentValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given budget construction intended incumbent is valid; otherwise, false
     */
    public boolean isValidIncumbent(BudgetConstructionIntendedIncumbent intendedIncumbent, String currentValue, MessageMap errorMap, String errorPropertyName);

    /**
     * Runs existence and active tests on the ObjectCode reference This method is different than the one in
     * AccountingLineRuleHelper in that it adds the bad value to the errormessage This method signature should probably be added to
     * AccountingLineRuleHelper
     *
     * @param objectCode the given object code
     * @param currentValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given object code is valid; otherwise, false
     */
    public boolean isValidObjectCode(ObjectCode objectCode, String currentValue, MessageMap errorMap, String errorPropertyName);

    /**
     * determine whether the given budget construction position is valid
     *
     * @param position the given budget construction position
     * @param currentValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given budget construction position is valid; otherwise, false
     */
    public boolean isValidPosition(BudgetConstructionPosition position, String currentValue, MessageMap errorMap, String errorPropertyName);

    /**
     * determine whether the given sub account is valid
     *
     * @param subAccount the given sub account
     * @param currentValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given sub account is valid; otherwise, false
     */
    public boolean isValidSubAccount(SubAccount subAccount, String currentValue, MessageMap errorMap, String errorPropertyName);

    /**
     * Runs existence and active tests on the SubObjectCode reference This method is different than the one in
     * AccountingLineRuleHelper in that it adds the bad value to the errormessage This method signature should probably be added to
     * AccountingLineRuleHelper
     *
     * @param subObjectCode the given sub object
     * @param currentValue the given current value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given sub object is valid; otherwise, false
     */
    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, String currentValue, MessageMap errorMap, String errorPropertyName);
}

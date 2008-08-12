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
package org.kuali.kfs.module.bc.document.service;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.ErrorMap;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;

/**
 * define a set of validations methods for buddget construction
 */
public interface BudgetConstructionRuleHelperService {

    /**
     * determine wether the given budget document is allowed to be budgeted
     * 
     * @param budgetConstructionDocument the given budget document
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given budget document can be budgeted; otherwise, false
     */
    public abstract boolean isBudgetableDocument(BudgetConstructionDocument budgetConstructionDocument, ErrorMap errorMap, String errorPropertyName);

    /**
     * determine whether the given appoinment funding can be associated with a valid budget contruction document
     * 
     * @param appointmentFunding the given appointment funding
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given appoinment funding can be associated with a valid budget contruction document; otherwise, false
     */
    public abstract boolean isAssociatedWithValidDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap, String errorPropertyName);

    /**
     * determine whether the given chart is valid
     * 
     * @param chart the given chart
     * @param invalidValue the given invalid value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given chart is valid; otherwise, false
     */
    public abstract boolean isValidChart(Chart chart, String invalidValue, ErrorMap errorMap, String errorPropertyName);

    /**
     * determine whether the given account is valid
     * 
     * @param account the given account
     * @param invalidValue the given invalid value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given account is valid; otherwise, false
     */
    public abstract boolean isValidAccount(Account account, String invalidValue, ErrorMap errorMap, String errorPropertyName);

    /**
     * determine whether the given sub account is valid
     * 
     * @param subAccount the given sub account
     * @param invalidValue the given invalid value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given sub account is valid; otherwise, false
     */
    public abstract boolean isValidSubAccount(SubAccount subAccount, String invalidValue, ErrorMap errorMap, String errorPropertyName);

    /**
     * Runs existence and active tests on the SubObjectCode reference This method is differenct than the one in
     * AccountingLineRuleHelper in that it adds the bad value to the errormessage This method signature should probably be added to
     * AccountingLineRuleHelper
     * 
     * @param subObjectCode the given sub object
     * @param invalidValue the given invalid value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given sub object is valid; otherwise, false
     */
    public abstract boolean isValidSubObjectCode(SubObjCd subObjectCode, String invalidValue, ErrorMap errorMap, String errorPropertyName);

    /**
     * Runs existence and active tests on the ObjectCode reference This method is differenct than the one in
     * AccountingLineRuleHelper in that it adds the bad value to the errormessage This method signature should probably be added to
     * AccountingLineRuleHelper
     * 
     * @param objectCode the given object code
     * @param invalidValue the given invalid value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given object code is valid; otherwise, false
     */
    public abstract boolean isValidObjectCode(ObjectCode objectCode, String invalidValue, ErrorMap errorMap, String errorPropertyName);

    /**
     * determine whether the given budget contruction position is valid
     * 
     * @param position the given budget contruction position
     * @param invalidValue the given invalid value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given budget contruction position is valid; otherwise, false
     */
    public abstract boolean isValidPosition(BudgetConstructionPosition position, String invalidValue, ErrorMap errorMap, String errorPropertyName);

    /**
     * determine whether the given budget contruction intended incumbent is valid
     * 
     * @param intendedIncumbent the given budget contruction intended incumbent
     * @param invalidValue the given invalid value
     * @param errorMap the given error map that can hold the error message if any
     * @param errorPropertyName the specified property name that is tested
     * @return true if the given budget contruction intended incumbent is valid; otherwise, false
     */
    public abstract boolean isValidIncumbent(BudgetConstructionIntendedIncumbent intendedIncumbent, String invalidValue, ErrorMap errorMap, String errorPropertyName);

    /**
     * build the error message with the given label and invalid value
     * 
     * @param label the given label
     * @param invalidValue the given invalid value
     * @return the error message built from the given label and invalid value
     */
    public abstract String getErrorMessage(Class<? extends BusinessObject> businessObjectClass, String attributeName, String invalidValue);

}
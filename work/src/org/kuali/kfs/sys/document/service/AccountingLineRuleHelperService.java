/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.krad.datadictionary.DataDictionary;

public interface AccountingLineRuleHelperService {
    /**
     * This method validates that an accounting line object contains values that actually exist in the DB. SubAccount and SubObject
     * are only checked if there are values in them. The others throw errors if they area null.
     * 
     * @param accountingLine
     * @parm accountingLineByPosition
     * @return success or failure of validating the AccountingLine
     * @throws IllegalStateException
     */
    public abstract boolean validateAccountingLine(AccountingLine accountingLine);
    
    /**
     * This method validates that the chart is active for use in the system.
     * 
     * @param chart
     * @param accountIdentifyingPropertyName 
     * @param dataDictionary
     * @return boolean True if the Chart is valid.
     */
    public abstract boolean isValidChart(String accountIdentifyingPropertyName, Chart chart, DataDictionary dataDictionary);
    
    /**
     * This method validates that the chart is active for use in the system.
     * 
     * @param chart
     * @param dataDictionary
     * @param accountIdentifyingPropertyName
     * @param errorPropertyName
     * @return boolean True if the Chart is valid.
     */
    public abstract boolean isValidChart(Chart chart, DataDictionary dataDictionary, String errorPropertyName, String accountIdentifyingPropertyName);
    
    /**
     * This method validates that the account is active for use in the system.
     * 
     * @param accountIdentifyingPropertyName
     * @param account
     * @param dataDictionary
     * @return boolean True if it is valid.
     */
    public abstract boolean isValidAccount(String accountIdentifyingPropertyName, Account account, DataDictionary dataDictionary);
    
    /**
     * This method validates that the account is active for use in the system.
     * 
     * @param account
     * @param dataDictionary
     * @param accountIdentifyingPropertyName
     * @param errorPropertyName
     * @return boolean True if it is valid.
     */
    public abstract boolean isValidAccount(Account account, DataDictionary dataDictionary, String errorPropertyName, String accountIdentifyingPropertyName);
    
    /**
     * Checks that the given overrideCode is sufficient for the given BO, adding errors to the global map if not.
     * 
     * @param line
     * @param overrideCode
     * @return whether the given overrideCode is sufficient for the given BO.
     */
    public abstract boolean hasRequiredOverrides(AccountingLine line, String overrideCode);
    
    /**
     * This method validates that a sub account is active.
     * 
     * @param accountIdentifyingPropertyName
     * @param subAccount
     * @param dataDictionary
     * @return true if it exists
     */
    public abstract boolean isValidSubAccount(String accountIdentifyingPropertyName, SubAccount subAccount, DataDictionary dataDictionary);
    
    /**
     * This method validates that a sub account is active.
     * 
     * @param subAccount
     * @param dataDictionary
     * @param accountIdentifyingPropertyName
     * @param errorPropertyName
     * @return true if it exists
     */
    public abstract boolean isValidSubAccount(SubAccount subAccount, DataDictionary dataDictionary, String errorPropertyName, String accountIdentifyingPropertyName);
    
    /**
     * This method validates that an object code is active.
     * 
     * @param accountIdentifyingPropertyName 
     * @param objectCode
     * @param dataDictionary
     * @return boolean True if the object code is valid.
     */
    public abstract boolean isValidObjectCode(String accountIdentifyingPropertyName, ObjectCode objectCode, DataDictionary dataDictionary);
    
    /**
     * This method validates that an object code is active.
     * 
     * @param objectCode
     * @param dataDictionary
     * @param accountIdentifyingPropertyName
     * @param errorPropertyName
     * @return boolean True if the object code is valid.
     */
    public abstract boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName, String accountIdentifyingPropertyName);
    
    /**
     * This method validates that a sub object code is active.
     * 
     * @param accountIdentifyingPropertyName 
     * @param subObjectCode
     * @param dataDictionary
     * @return boolean True if it is valid.
     */
    public abstract boolean isValidSubObjectCode(String accountIdentifyingPropertyName, SubObjectCode subObjectCode, DataDictionary dataDictionary);
    
    /**
     * This method validates that a sub object code is active.
     * 
     * @param subObjectCode
     * @param dataDictionary
     * @param errorPropertyName
     * @return boolean True if it is valid.
     */
    public abstract boolean isValidSubObjectCode(SubObjectCode subObjectCode, DataDictionary dataDictionary, String errorPropertyName, String accountIdentifyingPropertyName);
    
    /**
     * This method validates that a project code is active.
     * 
     * @param accountIdentifyingPropertyName
     * @param projectCode
     * @param dataDictionary
     * @return boolean True if it is valid.
     */
    public abstract boolean isValidProjectCode(String accountIdentifyingPropertyName, ProjectCode projectCode, DataDictionary dataDictionary);
    
    /**
     * This method validates that a project code is active.
     * 
     * @param projectCode
     * @param dataDictionary
     * @param errorPropertyName
     * @param accountIdentifyingPropertyName
     * @return boolean True if it is valid.
     */
    public abstract boolean isValidProjectCode(ProjectCode projectCode, DataDictionary dataDictionary, String errorPropertyName, String accountIdentifyingPropertyName);
    
    /**
     * For the most part, object type codes aren't required on an accounting line; however, in some situations (e.g. Journal
     * Voucher) they are entered directly into the accounting line and must be validated. In those cases, they must be validated for
     * activeness.
     * 
     * @param accountIdentifyingPropertyName
     * @param objectTypeCode
     * @param dataDictionary
     * @return boolean True if the object type code is valid, false otherwise.
     */
    public abstract boolean isValidObjectTypeCode(String accountIdentifyingPropertyName, ObjectType objectTypeCode, DataDictionary dataDictionary);
    
    /**
     * For the most part, object type codes aren't required on an accounting line; however, in some situations (e.g. Journal
     * Voucher) they are entered directly into the accounting line and must be validated. In those cases, they must be validated for
     * activeness.
     * 
     * @param objectTypeCode
     * @param dataDictionary
     * @param accountIdentifyingPropertyName
     * @param errorPropertyName
     * @return boolean True if the object type code is valid, false otherwise.
     */
    public abstract boolean isValidObjectTypeCode(ObjectType objectTypeCode, DataDictionary dataDictionary, String errorPropertyName, String accountIdentifyingPropertyName);
    
    /**
     * @return short label for chart code defined in data dictionary
     */
    public abstract String getChartLabel();
    
    /**
     * @return short label for account number defined in data dictionary
     */
    public abstract String getAccountLabel();
    
    /**
     * @return short label for sub account number defined in data dictionary
     */
    public abstract String getSubAccountLabel();
    
    /**
     * @return short label for object code defined in data dictionary
     */
    public abstract String getObjectCodeLabel();
    
    /**
     * @return short label for sub object code defined in data dictionary
     */
    public abstract String getSubObjectCodeLabel();
    
    /**
     * @return short label for project code defined in data dictionary
     */
    public abstract String getProjectCodeLabel();
    
    /**
     * @return short label for object type code defined in data dictionary
     */
    public abstract String getObjectTypeCodeLabel();
    
    /**
     * @return short label for object sub type code defined in data dictionary
     */
    public abstract String getObjectSubTypeCodeLabel();
    
    /**
     * @return short label for organization code defined in data dictionary
     */
    public abstract String getOrganizationCodeLabel();
    
    /**
     * @return short label for fund group code defined in data dictionary
     */
    public abstract String getFundGroupCodeLabel();
    
    /**
     * @return short label for sub fund group code defined in data dictionary
     */
    public abstract String getSubFundGroupCodeLabel();
}

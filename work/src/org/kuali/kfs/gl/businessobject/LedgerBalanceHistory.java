/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject;




/**
 * Interface for BalanceHistory used by GL and Labor
 */
public interface LedgerBalanceHistory {
    public Integer getUniversityFiscalYear();
    public String getChartOfAccountsCode();
    public String getAccountNumber();
    public String getSubAccountNumber();
    public String getFinancialObjectCode();
    public String getFinancialSubObjectCode();
    public String getFinancialBalanceTypeCode();
    public String getFinancialObjectTypeCode();
    
    public boolean compareAmounts(Balance balance);
}

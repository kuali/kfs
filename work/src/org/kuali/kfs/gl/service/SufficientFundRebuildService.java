/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.Collection;

import org.kuali.module.gl.bo.SufficientFundRebuild;

public interface SufficientFundRebuildService {
    public Collection getAll();

    public Collection getAllAccountEntries();

    public Collection getAllObjectEntries();

    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode);

    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode);

    public void save(SufficientFundRebuild sfrb);

    public void delete(SufficientFundRebuild sfrb);
}

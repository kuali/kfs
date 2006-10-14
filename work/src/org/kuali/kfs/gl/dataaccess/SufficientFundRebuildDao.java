/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/dataaccess/SufficientFundRebuildDao.java,v $
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
package org.kuali.module.gl.dao;

import java.util.Collection;

import org.kuali.module.gl.bo.SufficientFundRebuild;

public interface SufficientFundRebuildDao {
    public Collection getAll();

    public Collection getByType(String accountFinancialObjectTypeCode);

    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode);

    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode);

    public void save(SufficientFundRebuild sfrb);

    public void delete(SufficientFundRebuild sfrb);

    /**
     * This method should only be used in unit tests. It loads all the gl_sf_rebuild_t rows in memory into a collection. This won't
     * sace for production.
     * 
     * @return
     */
    public Collection testingGetAllEntries();
}

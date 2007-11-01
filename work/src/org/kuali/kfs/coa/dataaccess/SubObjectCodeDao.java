/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.dao;

import org.kuali.module.chart.bo.SubObjCd;


/**
 * This interface defines basic methods that SubObjectCode Dao's must provide
 */
public interface SubObjectCodeDao {

    /**
     * Retrieves a SubObjectCode by primary key.
     * 
     * @param universityFiscalYear - part of composite key
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @param financialObjectCode - part of composite key
     * @param financialSubObjectCode - part of composite key
     * @return a {@link SubObjectCode} based on primary keys
     */
    public SubObjCd getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode);
}

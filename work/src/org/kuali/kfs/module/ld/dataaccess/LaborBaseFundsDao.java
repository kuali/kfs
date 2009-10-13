/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.dataaccess;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;

/**
 * This is the data access object for account status base funds.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds
 */
public interface LaborBaseFundsDao {

    /**
     * This method finds the records of base budget for each Labor object according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of base budgets for Labor objects
     */
    List<AccountStatusBaseFunds> findLaborBaseFunds(Map fieldValues, boolean isConsolidated);
}

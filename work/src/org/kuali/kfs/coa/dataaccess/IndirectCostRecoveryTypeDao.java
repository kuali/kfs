/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;

/**
 * This interface defines the data access methods for {@link IndirectCostRecoveryRateDetail}
 */
public interface IndirectCostRecoveryTypeDao {

    /**
     * This method looks up all
     * 
     * @{link IndirectCostRecoveryRateDetail} by the fiscal year, series ID, and balance type code
     * @param universityFiscalYear
     * @param financialSeriesId
     * @param balanceTypeCode
     * @return collection of
     * @{link IndirectCostRecoveryRateDetail}s that match these criteria
     */
    public IndirectCostRecoveryType getByPrimaryKey(String code);
}

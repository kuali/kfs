/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;



/**
 * The DAO interface that declares methods needed for the balancing process. This is for the GL and Labor EntryHistory BO.
 */
public interface LedgerEntryHistoryBalancingDao {
    /**
     * @param year for which to return the sum
     * @return sum of the rowCount column for the given fiscal year
     */
    public Integer findSumRowCountGreaterOrEqualThan(Integer year);
}

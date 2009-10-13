/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;

/**
 * This interface defines data access for {@link IndirectCostRecoveryExclusionType}
 */
public interface IndirectCostRecoveryExclusionTypeDao {
    /**
     * This method returns a specific {@link IndirectCostRecoveryExclusionType} based on the criteria passed in
     * 
     * @param accountIndirectCostRecoveryTypeCode
     * @param chartOfAccountsCode
     * @param objectCode
     * @return the {@link IndirectCostRecoveryExclusionType} found by the criteria passed in
     */
    public IndirectCostRecoveryExclusionType getByPrimaryKey(String accountIndirectCostRecoveryTypeCode, String chartOfAccountsCode, String objectCode);
}

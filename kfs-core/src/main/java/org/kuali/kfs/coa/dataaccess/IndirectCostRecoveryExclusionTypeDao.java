/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

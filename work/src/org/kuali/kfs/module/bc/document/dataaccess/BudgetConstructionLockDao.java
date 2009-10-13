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
package org.kuali.kfs.module.bc.document.dataaccess;

import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;

/**
 * Provides methods for retrieving locks.
 */
public interface BudgetConstructionLockDao {

    /**
     * Retrieves all current account locks for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return budget headers that are locked
     */
    public List<BudgetConstructionHeader> getAllAccountLocks(String lockUnivId);

    /**
     * Retrieves all current transaction locks for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return budget headers that are locked
     */
    public List<BudgetConstructionHeader> getAllTransactionLocks(String lockUnivId);

    /**
     * Retrieves all funding locks that do not have a corresponding position lock for the given user (or all locks if user is
     * null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return funding locks records
     */
    public List<BudgetConstructionFundingLock> getOrphanedFundingLocks(String lockUnivId);

    /**
     * Retrieves all current position/funding locks for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return position/funding records that are locked.
     */
    public List<PendingBudgetConstructionAppointmentFunding> getAllPositionFundingLocks(String lockUnivId);

    /**
     * Retrieves all current position locks without a funding lock for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId universal id that will be used in lock query
     * @return positions that are locked.
     */
    public List<BudgetConstructionPosition> getOrphanedPositionLocks(String lockUnivId);

}

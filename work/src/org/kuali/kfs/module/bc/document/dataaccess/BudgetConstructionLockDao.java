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

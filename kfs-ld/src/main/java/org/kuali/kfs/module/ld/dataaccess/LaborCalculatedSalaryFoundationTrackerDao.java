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
package org.kuali.kfs.module.ld.dataaccess;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;
import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.LaborCalculatedSalaryFoundationTracker;

/**
 * This is the data access object for calculated salary foundation tracker
 * 
 * @see org.kuali.kfs.module.ld.businessobject.CalculatedSalaryFoundationTracker
 */
public interface LaborCalculatedSalaryFoundationTrackerDao {

    /**
     * This method finds the CSF trackers according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of CSF trackers
     */
    List<LaborCalculatedSalaryFoundationTracker> findCSFTrackers(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the CSF trackers according to input fields and values and converts the trackers into AccountStatusBaseFunds
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of base budgets for Labor objects
     */
    List<AccountStatusBaseFunds> findCSFTrackersAsAccountStatusBaseFunds(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the CSF trackers according to input fields and values and converts the trackers into EmployeeFunding
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of CSF trackers as EmployeeFunding
     */
    List<EmployeeFunding> findCSFTrackersAsEmployeeFunding(Map fieldValues, boolean isConsolidated);
}

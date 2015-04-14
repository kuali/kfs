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
package org.kuali.kfs.module.ld.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;

/**
 * This interface provides its clients with access to labor base fund entries in the backend data store.
 */
public interface LaborBaseFundsService {

    /**
     * This method finds the records of base budget for each Labor object according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of base budgets for Labor objects
     */
    List<AccountStatusBaseFunds> findLaborBaseFunds(Map fieldValues, boolean isConsolidated);

    /**
     * This method finds the records of base budget for each Labor object according to input fields and values
     * 
     * @param fieldValues the input fields and values
     * @param isConsolidated consolidation option is applied or not
     * @return a collection of base budgets for Labor objects
     */
    List<AccountStatusBaseFunds> findAccountStatusBaseFundsWithCSFTracker(Map fieldValues, boolean isConsolidated);

}

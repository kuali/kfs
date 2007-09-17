/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;

/**
 * This class is an interface to data access objects for general labor related inquiries 
 */
public interface LaborDao {
 
    /**
     * 
     * This method returns a CSF Tracker total for a given selection criteria
     * @param fieldValues
     * @return
     */
    @Deprecated
    Object getCSFTrackerTotal(Map fieldValues);

    /**
     * 
     * This method returns an encumberence total for a given selection criteria
     * @param fieldValues
     * @return
     */
    Object getEncumbranceTotal(Map fieldValues);
    
    /**
     * 
     * This method returns base funds data
     * @param fieldValues
     * @param isConsolidated
     * @return Collection
     */
    @Deprecated
    Iterator getBaseFunds(Map fieldValues, boolean isConsolidated);
    
    /**
     * 
     * This method returns current funds data
     * @param fieldValues
     * @param isConsolidated
     * @return Collection
     */
    Iterator getCurrentFunds(Map fieldValues, boolean isConsolidated);
    
    /**
     * 
     * This method returns current funds data
     * @param fieldValues
     * @return Iterator
     */
    @Deprecated
    Iterator getEmployeeFunding(Map fieldValues);
    
    /**
     * 
     * This method returns current July1 Position Funding data
     * @param fieldValues
     * @return Collection
     */
    Collection getJuly1PositionFunding(Map fieldValues);
 
    
     /**
      * 
      * This method is used to verify if the select labor object code is active.
      * @param String the labor object code to be checked
      * @return
      */
    @Deprecated
    public boolean isActiveLaborObjectCode(AccountingDocument accountingDocument, AccountingLine accountingLine);
    
    
    Collection getJuly1(Map fieldValues);
}

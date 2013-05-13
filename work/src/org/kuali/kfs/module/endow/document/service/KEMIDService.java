/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.KEMID;

public interface KEMIDService {

    /**
     * Gets a KEMID by primary key.
     * @param kemid
     * @return a KEMID
     */
    public KEMID getByPrimaryKey(String kemid);
    
    /**
     * Validate if a KEMID is a true endowment (or permanently restricted endowment).
     * If the Type Code for a KEMID has a Principal Restriction Code (END_TYP_T: TYP_PRIN_RESTR_CD) where the Permanent Indicator for the Restriction code (END_TYP_RESTR_CD_T:PERM) is Yes, then the KEMID is a true or permanently restricted endowment.  
     * A true endowment or Permanently restricted endowment is one in which the principal funds cannot be expended.   
     * @param kemid
     * @return true or false
     */
    public boolean isTrueEndowment(String kemid);
    
    /**
     * Gets all the KEMIDs matching the specified cash sweep id.
     * 
     * @param cashSweepId
     * @return Collection of KEMID
     */
    public Collection<KEMID> getByCashSweepId(Integer cashSweepId);
    
    /**
     * Gets all the KEMIDs matching the specified ACI principle id. 
     *
     * @param aciPrincipleId
     * @return Collection of KEMID
     */
    public Collection<KEMID> getByPrincipleAciId(Integer aciPrincipleId);
    
    /**
     * Gets all the KEMIDs matching the specified ACI income id.
     * 
     * @param aciIncomeId
     * @return Collection of KEMID
     */
    public Collection<KEMID> getByIncomeAciId(Integer aciIncomeId);
    
    /**
     * Retrieves all kemId records where closed indicator = 'N'
     */
    public abstract Collection<KEMID> getAllKemIdWithClosedIndicatorNo();
}

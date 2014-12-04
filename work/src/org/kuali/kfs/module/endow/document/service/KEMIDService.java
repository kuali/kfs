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

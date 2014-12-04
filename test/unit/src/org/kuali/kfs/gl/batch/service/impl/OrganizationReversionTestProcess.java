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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSConstants;

/**
 * An extension of the OrganizationReversionProcess which does not actually write OriginEntries
 */
public class OrganizationReversionTestProcess extends OrganizationReversionProcessImpl {

    /**
     * Overridden to not create output file
     * @see org.kuali.kfs.gl.batch.service.impl.OrganizationReversionProcessImpl#organizationReversionProcess(java.util.Map, java.util.Map)
     */
    @Override
    public void organizationReversionProcess(Map jobParameters, Map<String, Integer> organizationReversionCounts) {
        this.setJobParameters(jobParameters);
        this.setOrganizationReversionCounts(organizationReversionCounts);

        initializeProcess();
        
        Iterator<Balance> balances = getBalanceService().findOrganizationReversionBalancesForFiscalYear((Integer) jobParameters.get(KFSConstants.UNIV_FISCAL_YR), isUsePriorYearInformation());
        processBalances(balances);
            
    }

    /**
     * Overridden to do nothing
     * @see org.kuali.kfs.gl.batch.service.impl.OrganizationReversionProcessImpl#writeOriginEntries(java.util.List)
     */
    @Override
    protected int writeOriginEntries(List<OriginEntryFull> originEntriesToWrite) {
        return this.getGeneratedOriginEntries().size(); 
    }

}

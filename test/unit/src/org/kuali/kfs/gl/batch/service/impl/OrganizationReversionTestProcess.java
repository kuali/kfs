/*
 * Copyright 2009 The Kuali Foundation
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

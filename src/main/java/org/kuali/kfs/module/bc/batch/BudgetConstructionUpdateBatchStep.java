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
package org.kuali.kfs.module.bc.batch;

import java.util.Date;

import org.kuali.kfs.module.bc.batch.service.GenesisService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.batch.AbstractStep;

public class BudgetConstructionUpdateBatchStep extends AbstractStep {

    protected GenesisService genesisService;

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        genesisService.bCUpdateStep(BudgetParameterFinder.getBaseFiscalYear());

        return true;
    }

    /**
     * Sets the genesisService attribute value.
     *
     * @param genesisService The genesisService to set.
     */
    public void setGenesisService(GenesisService genesisService) {
        this.genesisService = genesisService;
    }

}

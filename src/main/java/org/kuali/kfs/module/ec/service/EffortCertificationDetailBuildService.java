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
package org.kuali.kfs.module.ec.service;

import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * Provides the facilities that can generate detail line (build) for effort certification from the given labor ledger balance record
 */
public interface EffortCertificationDetailBuildService {

    /**
     * generate a detail line (build) for effort certification from the given labor ledger balance
     * 
     * @param postingYear the fiscal year when the detail line is generated
     * @param ledgerBalance the given labor ledger balance
     * @param reportDefinition the given report definition
     * @return a detail line (build) for effort certification from the given labor ledger balance
     */
    public EffortCertificationDetailBuild generateDetailBuild(Integer postingYear, LaborLedgerBalance ledgerBalance, EffortCertificationReportDefinition reportDefinition);
}

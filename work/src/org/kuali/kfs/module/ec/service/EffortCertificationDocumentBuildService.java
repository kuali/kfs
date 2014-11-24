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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * Provide the facility used to generate documents (build) from the labor ledger balances
 */
public interface EffortCertificationDocumentBuildService {
    
    /**
     * clear up documents and detail lines (build) with the fiscal year and report number of the given field values
     * 
     * @param fieldValues the map containing fiscalYear and report number
     */
    public void removeExistingDocumentBuild(Map<String, String> fieldValues);

    /**
     * generate documents(build) for effort certification from the given collection of labor ledger balance
     * 
     * @param postingYear the fiscal year when the document is generated
     * @param reportDefinition the given report definition of effort certification
     * @param ledgerBalances the given labor ledger balances
     * @return documents(build) for effort certification from the given collection of labor ledger balance
     */
    public List<EffortCertificationDocumentBuild> generateDocumentBuildList(Integer postingYear, EffortCertificationReportDefinition reportDefinition, List<LaborLedgerBalance> ledgerBalances);
    
    /**
     * generate a document(build) for effort certification from the given collection of labor ledger balance
     * 
     * @param postingYear the fiscal year when the document is generated
     * @param reportDefinition the given report definition of effort certification
     * @param ledgerBalances the given labor ledger balances
     * @return a document(build) for effort certification from the given collection of labor ledger balance
     */
    public EffortCertificationDocumentBuild generateDocumentBuild(Integer postingYear, EffortCertificationReportDefinition reportDefinition, List<LaborLedgerBalance> ledgerBalances);
}

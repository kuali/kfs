/*
 * Copyright 2007 The Kuali Foundation
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

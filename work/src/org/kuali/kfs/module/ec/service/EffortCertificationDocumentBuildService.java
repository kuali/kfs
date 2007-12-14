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
package org.kuali.module.effort.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.labor.bo.LedgerBalance;

/**
 * Provide the facility used to generate documents (build) from the labor ledger balances
 */
public interface EffortCertificationDocumentBuildService {

    /**
     * generate documents(build) for effort certification from the given collection of labor ledger balance
     * 
     * @param reportDefinition the given report definition of effort certification
     * @param ledgerBalances the given labor ledger balances
     * @param parameters the parameters setup in system parameters
     * @return documents(build) for effort certification from the given collection of labor ledger balance
     */
    public List<EffortCertificationDocumentBuild> generateDocumentBuild(EffortCertificationReportDefinition reportDefinition, Collection<LedgerBalance> ledgerBalances, Map<String, List<String>> parameters);
}

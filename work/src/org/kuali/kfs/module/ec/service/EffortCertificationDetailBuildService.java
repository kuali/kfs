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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.bo.LaborLedgerBalance;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;

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
     * @param parameters the parameters setup in the system parameters
     * @return a detail line (build) for effort certification from the given labor ledger balance
     */
    public EffortCertificationDetailBuild generateDetailBuild(Integer postingYear, LaborLedgerBalance ledgerBalance, EffortCertificationReportDefinition reportDefinition, Map<String, List<String>> parameters);
}

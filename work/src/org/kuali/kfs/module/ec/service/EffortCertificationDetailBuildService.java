/*
 * Copyright 2007-2008 The Kuali Foundation
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

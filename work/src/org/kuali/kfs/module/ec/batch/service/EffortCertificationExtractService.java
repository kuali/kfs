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

import org.kuali.module.effort.bo.EffortCertificationReportDefinition;

/**
 * The interface defines the methods that extract Labor Ledger records of the employees who were paid on a grant or cost shared
 * during the selected reporting period.
 */
public interface EffortCertificationExtractService {

    /**
     * extract Labor Ledger records of the employees who were paid on a grant or cost shared during a reporting period. The
     * reporting period can be determined by both of fiscal year and report number declared as system parameters.
     */
    public void extract();

    /**
     * extract Labor Ledger records of the employees who were paid on a grant or cost shared during a reporting period. The
     * reporting period can be determined by both of fiscal year and report number.
     * 
     * @param fiscalYear the given fiscal year with which Labor ledgers can be extracted.
     * @param reportNumber the given report number to run.
     */
    public void extract(Integer fiscalYear, String reportNumber);
    
    
}

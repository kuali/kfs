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
package org.kuali.kfs.sys.service;

/**
 * This interface specifies a ReportWriterService that needs to be aware of the fiscal year when
 * generating reports.
 * 
 * Note that the implementing service may need to implement mechanisms to ensure that if multiple reports are 
 * generated using this service (in particular the same instance), that each report uses the proper fiscal year.
 */
public interface FiscalYearAwareReportWriterService extends ReportWriterService {
    /**
     * Sets the fiscal year
     * @param fiscalYear
     */
    public void setFiscalYear(Integer fiscalYear);
}

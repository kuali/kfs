/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AgencyImportData;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.sys.batch.BatchInputFileType;

public interface AgencyDataImportService {
    
    public boolean importAgencyData();
    
    public boolean importAgencyDataFile(String dataFileName, BatchInputFileType inputFileType);
    
    public List<AgencyStagingData> validateAgencyData(AgencyImportData agencyData, String dataFileName);
    
    /**
     * 
     * This method gets all valid Agency Staging data and moves it to the historical expense table.
     * @return
     */
    boolean moveAgencyDataToHistoricalExpenseTable();

    /**
     * 
     * This method matches all valid Agency Staging data with Credit Card Staging data. Matched data is moved to the historical expense table.
     * @return
     */
    boolean matchExpenses();

}

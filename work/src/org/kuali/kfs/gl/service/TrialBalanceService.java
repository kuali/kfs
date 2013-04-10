/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;

import java.util.Collection;
import java.util.List;


/**
 * An interface which declares methods needed for using Balance
 */
public interface TrialBalanceService {

    /**
     * This method finds the balance records according to input fields an values
     * 
     * @param fieldValues the input fields an values
     * @return the summary records of trial balance entries
     */
    List findTrialBalance(String selectedFiscalYear, String chartCode);

    /**
     * This method generate trial balance report in PDF format
     * 
     * @param dataSource
     * @param fiscalYear
     */
    String generateReportForExtractProcess(Collection dataSource, String fiscalYear);
}

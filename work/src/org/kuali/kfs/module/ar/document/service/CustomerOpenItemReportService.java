/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class defines all the service methods for Customer Open Item Report.
 */
public interface CustomerOpenItemReportService {

    /**
     * This method populates CustomerOpenItemReportDetails (Customer History Report).
     *
     * @param customerNumber
     */
    public List getPopulatedReportDetails(String customerNumber);

    /**
     * This method populates CustomerOpenItemReportDetails (Customer Open Item Report)
     *
     * @param urlParameters
     */
    public List getPopulatedReportDetails(Map urlParameters);

    /**
     * This method gives list of document numbers from customerNumber.
     * 
     * @param customerNumber
     * @return
     */
    public Collection<String> getDocumentNumbersOfReferenceReports(String customerNumber);
    
    /**
     * This method populates CustomerOpenItemReportDetails  with customer's unpaid/unapplied amount.
     * 
     * @param customerNumber
     * @param document
     * @return
     */

    public List getPopulatedUnpaidUnappliedAmountReportDetails(String customerNumber, String document);
}

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
package org.kuali.kfs.pdp.dataaccess;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.DailyReport;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.ExtractionUnit;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;

public interface PaymentDetailDao {
    /**
     * @param custPaymentDocNbr
     * @param fdocTypeCode
     * @param orgCode the value of the system parameter PURAP_PDP_ORG_CODE
     * @param subUnitCode the value of the system parameter PURAP_PDP_SUB_UNIT_CODE
     * @return
     */
    public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode, String orgCode, String subUnitCode);

    /**
     * Retrieves list of <code>DisbursementNumberRange</code> records for the given processing campus which are active and have
     * range start date before or equal to the current date.
     *
     * @param campus processing campus to retrieve ranges for
     * @return List of <code>DisbursementNumberRange</code> records found
     */
    public List<DisbursementNumberRange> getDisbursementNumberRanges(String campus);

    /**
     * This returns the data required for the daily report
     *
     * @param currentSqlDate the current Sql date
     * @return
     */
    public List<DailyReport> getDailyReportData(Date currentSqlDate);

    /**
     * This will return an iterator of all the cancelled payment details that haven't already been processed
     * @param extractionUnits a List of ExtractionUnit objects to represent each of the unit/sub-unit combinations to get PaymentDetails for
     * @return
     */
    public Iterator getUnprocessedCancelledDetails(List<ExtractionUnit> extractionUnits);

    /**
     * This will return all the ACH payments that need an email sent
     *
     * @return
     */
    public Iterator getAchPaymentsWithUnsentEmail();

    /**
     * This will return an iterator of all the paid payment details that haven't already been processed
     * @param extractionUnits a List of ExtractionUnit objects to represent each of the unit/sub-unit combinations to get PaymentDetails for
     * @return
     */
    public Iterator getUnprocessedPaidDetails(List<ExtractionUnit> extractionUnits);
}

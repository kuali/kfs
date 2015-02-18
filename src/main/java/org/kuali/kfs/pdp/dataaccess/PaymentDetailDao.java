/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

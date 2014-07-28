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
package org.kuali.kfs.module.ar.report.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.report.util.CustomerAgingReportDataHolder;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This interface contains methods related to the Customer Aging Report.
 */
public interface CustomerAgingReportService {

    /**
     * This method calculates aging report amounts for the given invoice details and report run date.
     *
     * @param invoiceDetails
     * @param reportRunDate
     * @return CustomerAgingReportDataHolder containing aging report amounts and known customers
     */
    public CustomerAgingReportDataHolder calculateAgingReportAmounts(Collection<CustomerInvoiceDetail> invoiceDetails, Date reportRunDate);

    /**
     * This method generates a database query and retrieves the total of all invoices for the given processing chart and org codes,
     * which have billing dates within the given date range, for which there is an outstanding balance due.
     *
     * @param chart Processing chart code for the invoices being retrieved.
     * @param org Processing org code for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice balances due.
     * @param end Ending date of the date range used to find invoice balances due.
     * @return
     */
    HashMap<String, KualiDecimal> findInvoiceAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    /**
     * This method generates a database query and retrieves the applied amount of all invoices for the
     * given processing chart and org codes, which have billing dates within the given date range.
     *
     * @param chart Processing chart code for the invoices being retrieved.
     * @param org Processing org code for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice applied amount.
     * @param end Ending date of the date range used to find invoice applied amount.
     * @return
     */
    HashMap<String, KualiDecimal> findAppliedAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    /**
     * This method generates a database query and retrieves the discount amount of all invoices for the
     * given processing chart and org codes, which have billing dates within the given date range.
     *
     * @param chart Processing chart code for the invoices being retrieved.
     * @param org Processing org code for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice discount amount.
     * @param end Ending date of the date range used to find invoice discount amount.
     * @return
     */
    HashMap<String, KualiDecimal> findDiscountAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    // BILLING CHART AND ORG METHODS

    /**
     * This method generates a database query and retrieves the total of all invoices for the given billing chart and org codes,
     * which have billing dates within the given date range, for which there is an outstanding balance due.
     *
     * @param chart Billing chart code for the invoices being retrieved.
     * @param org Billing org code for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice balances due.
     * @param end Ending date of the date range used to find invoice balances due.
     * @return
     */
    HashMap<String, KualiDecimal> findInvoiceAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    /**
     * This method generates a database query and retrieves the applied amount of all invoices for the
     * given billing chart and org codes, which have billing dates within the given date range.
     *
     * @param chart Billing chart code for the invoices being retrieved.
     * @param org Billing org code for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice applied amount.
     * @param end Ending date of the date range used to find invoice applied amount.
     * @return
     */
    HashMap<String, KualiDecimal> findAppliedAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    /**
     * This method generates a database query and retrieves the discount amount of all invoices for the
     * given billing chart and org codes, which have billing dates within the given date range.
     *
     * @param chart Billing chart code for the invoices being retrieved.
     * @param org Billing org code for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice discount amount.
     * @param end Ending date of the date range used to find invoice discount amount.
     * @return
     */
    HashMap<String, KualiDecimal> findDiscountAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    // ACCOUNT METHODS

    /**
     * This method generates a database query and retrieves the total of all invoices for the given chart and account,
     * which have billing dates within the given date range, for which there is an outstanding balance due.
     *
     * @param chart Chart code for the invoices being retrieved.
     * @param org Account Number for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice balances due.
     * @param end Ending date of the date range used to find invoice balances due.
     * @return
     */
    HashMap<String, KualiDecimal> findInvoiceAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end);

    /**
     * This method generates a database query and retrieves the applied amount of all invoices for the
     * given chart and account, which have billing dates within the given date range.
     *
     * @param chart Chart code for the invoices being retrieved.
     * @param org Account Number for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice balances due.
     * @param end Ending date of the date range used to find invoice balances due.
     * @return
     */
    HashMap<String, KualiDecimal> findAppliedAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end);

    /**
     * This method generates a database query and retrieves the discount amount of all invoices for the
     * given chart and account, which have billing dates within the given date range.
     *
     * @param chart Chart code for the invoices being retrieved.
     * @param org Account Number for the invoices being retrieved.
     * @param begin Beginning date of the date range used to find invoice balances due.
     * @param end Ending date of the date range used to find invoice balances due.
     * @return
     */
    HashMap<String, KualiDecimal> findDiscountAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end);

    /**
     * This method returns the total of writeoff amount for the particular customer
     *
     * @param customerNumber customerNumber for which writeoff amount should be fetched
     * @return Returns total of writeoff amount for the Customer.
     */
    KualiDecimal findWriteOffAmountByCustomerNumber(String customerNumber);
}

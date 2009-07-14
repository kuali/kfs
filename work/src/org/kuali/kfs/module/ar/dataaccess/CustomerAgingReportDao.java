/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.HashMap;

import org.kuali.rice.kns.util.KualiDecimal;

public interface CustomerAgingReportDao {
    HashMap<String, KualiDecimal> findInvoiceAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findAppliedAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findDiscountAmountByProcessingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findInvoiceAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findAppliedAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findDiscountAmountByBillingChartAndOrg(String chart, String org, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findInvoiceAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findAppliedAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end);

    HashMap<String, KualiDecimal> findDiscountAmountByAccount(String chart, String account, java.sql.Date begin, java.sql.Date end);
}

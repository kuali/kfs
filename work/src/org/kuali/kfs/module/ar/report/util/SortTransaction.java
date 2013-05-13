/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.util;

import java.sql.Date;
import java.util.Comparator;

public class SortTransaction implements Comparator<CustomerStatementDetailReportDataHolder> {

    @Override
    public int compare(CustomerStatementDetailReportDataHolder c1, CustomerStatementDetailReportDataHolder c2) {
        Date c1Date = c1.getDocumentFinalDate();
        Date c2Date = c2.getDocumentFinalDate();
        int result = 0;
        if (c1Date == null) {
            result = (c2Date == null)? 0 : -1;
        } else {
            result = (c2Date == null) ? 1 : c1Date.compareTo(c2Date);
        }

        if (result == 0) {
            result = c1.getDocType().compareTo(c2.getDocType());
        }
        return result;
    }
}

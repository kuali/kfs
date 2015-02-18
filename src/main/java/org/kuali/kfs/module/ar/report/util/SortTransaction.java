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

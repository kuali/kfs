package org.kuali.kfs.module.ar.report.util;

import java.sql.Date;
import java.util.Comparator;

public class SortTransaction implements Comparator<CustomerStatementDetailReportDataHolder> {

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

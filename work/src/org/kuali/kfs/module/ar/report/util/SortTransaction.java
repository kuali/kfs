package org.kuali.kfs.module.ar.report.util;

import java.util.Comparator;

public class SortTransaction implements Comparator<CustomerStatementDetailReportDataHolder> {

    public int compare(CustomerStatementDetailReportDataHolder c1, CustomerStatementDetailReportDataHolder c2) {
        int result = c1.getDocumentFinalDate().compareTo(c2.getDocumentFinalDate());
        if (result == 0) {
            result = c1.getDocType().compareTo(c2.getDocType());
        }        
        return result;
    }
}

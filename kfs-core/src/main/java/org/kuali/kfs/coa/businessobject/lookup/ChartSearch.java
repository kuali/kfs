package org.kuali.kfs.coa.businessobject.lookup;

import org.kuali.kfs.coa.businessobject.Chart;

public class ChartSearch {


    public Chart retrieveByChartCode(String code) {
        Chart chart = new Chart();
        chart.setChartOfAccountsCode(code);
        chart.setFinChartOfAccountDescription("BLOOMINGTON AUX");
        return chart;
    }
}

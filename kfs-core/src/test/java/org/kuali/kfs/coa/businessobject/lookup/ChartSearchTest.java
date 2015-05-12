package org.kuali.kfs.coa.businessobject.lookup;

import org.junit.Test;
import org.kuali.kfs.coa.businessobject.Chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChartSearchTest {

    @Test
    public void retrieveBLChartByCode() {
        String code = "BL";
        ChartSearch chartSearch = new ChartSearch();
        Chart chart = chartSearch.retrieveByChartCode(code);
        assertNotNull("Chart is expected to be non null", chart);
        assertEquals("Chart code should be BL", code, chart.getChartOfAccountsCode());
    }

    @Test
    public void retrieveBAChartByCode() {
        String code = "BA";
        ChartSearch chartSearch = new ChartSearch();
        Chart chart = chartSearch.retrieveByChartCode(code);
        assertNotNull("Chart is expected to be non null", chart);
        assertEquals("Chart code should be BA", code, chart.getChartOfAccountsCode());
        assertEquals("Chart description is not what we expected", "BLOOMINGTON AUX", chart.getFinChartOfAccountDescription());
    }

}

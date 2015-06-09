package org.kuali.kfs.coa.businessobject.lookup;

import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.PersistenceStructureService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChartSearchTest {

    private LookupService lookupService;

    @Before
    public void setUp() throws Exception {
        lookupService = new FakeChartLookupService();

    }

    @Test
    public void retrieveBLChartByCode() {
        String code = "BL";
        ChartSearch chartSearch = new ChartSearch(lookupService, null, null, null);
        Chart chart = chartSearch.findChart(code);
        assertSameChart(code, chart, "BLOOMINGTON");
    }

    @Test
    public void retrieveBAChartByCode() {
        String code = "BA";
        ChartSearch chartSearch = new ChartSearch(lookupService, null, null, null);
        Chart chart = chartSearch.findChart(code);
        assertSameChart(code, chart, "BLOOMINGTON AUX");
    }

    protected void assertSameChart(String code, Chart chart, String expectedDescription) {
        assertNotNull("Chart is expected to be non null", chart);
        assertEquals("Chart code should be " + code, code, chart.getChartOfAccountsCode());
        assertEquals("Chart description is not what we expected", expectedDescription, chart.getFinChartOfAccountDescription());
    }

    private class FakeChartLookupService implements LookupService {
        @Override
        public <T> Collection<T> findCollectionBySearchUnbounded(Class<T> example, Map<String, String> formProps) {
            return null;
        }

        @Override
        public <T> Collection<T> findCollectionBySearch(Class<T> example, Map<String, String> formProps) {
            Chart chart = new Chart();
            final String code = formProps.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            chart.setChartOfAccountsCode(code);
            if (code.equals("BA")) {
                chart.setFinChartOfAccountDescription("BLOOMINGTON AUX");
            } else {
                chart.setFinChartOfAccountDescription("BLOOMINGTON");
            }
            return (Collection<T>) Arrays.asList(new Chart[]{chart});
        }

        @Override
        public <T> Collection<T> findCollectionBySearchHelper(Class<T> example, Map<String, String> formProperties, boolean unbounded) {
            return null;
        }

        @Override
        public <T> T findObjectBySearch(Class<T> example, Map<String, String> formProps) {
            return null;
        }

        @Override
        public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps) {
            return false;
        }
    }
}

package org.kuali.kfs.coa.businessobject.lookup;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.LookupService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChartSearch {

    protected LookupService lookupService;

    public ChartSearch(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    public Chart retrieveByChartCode(String code) {
        Chart chart = findChart(code);
        return chart;
    }

    protected Chart findChart(String code) {
        Map<String, String> searchProperties = new ConcurrentHashMap<>();
        searchProperties.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, code);
        Collection charts = lookupService.findCollectionBySearch(Chart.class, searchProperties);
        return (Chart) charts.iterator().next();
    }

}

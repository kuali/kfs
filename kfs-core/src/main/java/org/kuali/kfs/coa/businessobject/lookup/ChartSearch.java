package org.kuali.kfs.coa.businessobject.lookup;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.rest.ChartResourceRepresentation;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.LookupService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChartSearch extends ChartResourceRepresentation {

    protected LookupService lookupService;

    public ChartSearch(LookupService lookupService, DataDictionaryService dataDictionaryService, ConfigurationService configurationService) {
        super(dataDictionaryService, configurationService);
        this.lookupService = lookupService;
    }

    public Map<String, Object> retrieveByChartCode(String code) {
        return representChartForLookup(findChart(code));
    }

    protected Chart findChart(String code) {
        Map<String, String> searchProperties = new ConcurrentHashMap<>();
        searchProperties.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, code);
        Collection charts = lookupService.findCollectionBySearch(Chart.class, searchProperties);
        return (Chart) charts.iterator().next();
    }

    protected Map<String, Object> representChartForLookup(Chart chart) {
        final BusinessObjectEntry entry = (BusinessObjectEntry)dataDictionaryService.getDataDictionary().getBusinessObjectEntryForConcreteClass(Chart.class.getName());
        Map<String, Object> identity = new ConcurrentHashMap<>();
        return entry.getLookupDefinition().getResultFieldNames().stream().reduce(identity,
                (matsya, resultFieldName) -> {
                    return collectValue(matsya, chart, resultFieldName, true);
                },
                (matsya, resultFieldName) -> {
                    return matsya;
                });
    }

}

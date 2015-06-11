package org.kuali.kfs.coa.businessobject.inquiry;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.rest.ChartResourceRepresentation;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChartInquiry extends ChartResourceRepresentation {
    protected BusinessObjectService businessObjectService;

    public ChartInquiry(BusinessObjectService businessObjectService, DataDictionaryService dataDictionaryService, ConfigurationService configurationService) {
        super(dataDictionaryService, configurationService);
        this.businessObjectService = businessObjectService;
    }

    public Chart findChart(String chartOfAccountsCode) {
        final Chart chart = businessObjectService.findBySinglePrimaryKey(Chart.class, chartOfAccountsCode);
        return chart;
    }

    public Map<String, Object> retrieveByChartCode(String chartofAccountsCode) {
        return representChartForInquiry(findChart(chartofAccountsCode));
    }

    protected Map<String, Object> representChartForInquiry(final Chart chart) {
        final BusinessObjectEntry entry = (BusinessObjectEntry)dataDictionaryService.getDataDictionary().getBusinessObjectEntryForConcreteClass(Chart.class.getName());
        Map<String, Object> identity = new ConcurrentHashMap<>();
        return entry.getInquiryDefinition().getInquirySections().stream().reduce(identity,
                (rep, inquirySection) -> {
                   return inquirySection.getInquiryFieldNames().stream().reduce(rep,
                           (repr, fieldName) -> {
                               return collectValue(repr, chart, fieldName, false);
                           },
                           (repr, otherRep) -> {
                               return repr;
                           });
                },
                (rep, otherRep) -> {
                    return rep;
                });
    }
}

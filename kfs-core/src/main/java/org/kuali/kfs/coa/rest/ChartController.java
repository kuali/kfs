package org.kuali.kfs.coa.rest;


import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.LookupService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChartController {

    // Lines commented out here and line 378 of the web.xml render a different result that partially works in a different way

//    @RequestMapping(value="/lookup/coa/chart/{code}", method= RequestMethod.GET, produces="application/json")
    @RequestMapping(value="/coa/chart/{code}", method= RequestMethod.GET, produces="application/json")
    public @ResponseBody Map<String, String> lookupChartByPrimaryKey(@PathVariable String code) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final ChartSearch chartSearch = new ChartSearch(lookupService);
//        return chartSearch.retrieveByChartCode("BA");
        final Chart chart = chartSearch.retrieveByChartCode("BL");
        Map<String, String> m = new ConcurrentHashMap<>();
        m.put("name", chart.getName());
        return m;
    }
}
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

@Controller
@RequestMapping(value="/coa")
public class ChartController {

    @RequestMapping(value="/chart/{chartofAccountsCode}", method= RequestMethod.GET, produces="application/json")
    public @ResponseBody Chart lookupChartByPrimaryKey(@PathVariable String chartOfAccountsCode) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final ChartSearch chartSearch = new ChartSearch(lookupService);
        return chartSearch.retrieveByChartCode(chartOfAccountsCode);
    }
}

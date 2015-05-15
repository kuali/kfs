package org.kuali.kfs.coa.rest;


import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.LookupService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value="/kfs-dev/lookup")
public class ChartController {

    @RequestMapping(value="/coa/chart", method= RequestMethod.GET, produces="application/json")
    public @ResponseBody Chart lookupChartByPrimaryKey(@RequestParam("chartOfAccountsCode") String chartOfAccountsCode) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final ChartSearch chartSearch = new ChartSearch(lookupService);
        return chartSearch.retrieveByChartCode(chartOfAccountsCode);
    }
}

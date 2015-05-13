package org.kuali.kfs.coa.rest;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.LookupService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("coa/chart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChartSearchResource {
    @GET
    public Chart lookupChartByPrimaryKey(@QueryParam("chartOfAccountsCode") String chartOfAccountsCode) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final ChartSearch chartSearch = new ChartSearch(lookupService);
        return chartSearch.retrieveByChartCode(chartOfAccountsCode);
    }
}

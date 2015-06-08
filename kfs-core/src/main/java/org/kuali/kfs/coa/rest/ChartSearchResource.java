package org.kuali.kfs.coa.rest;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.LookupService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Path("coa/chart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChartSearchResource {


    @GET
    @Path("/{code}")
    public List<Chart> lookupChartByPrimaryKey(@PathParam("code") String chartOfAccountsCode) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final ChartSearch chartSearch = new ChartSearch(lookupService);
        return Arrays.asList(chartSearch.retrieveByChartCode(chartOfAccountsCode));
    }
}

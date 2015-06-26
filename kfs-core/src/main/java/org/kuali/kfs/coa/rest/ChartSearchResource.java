package org.kuali.kfs.coa.rest;

import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.LookupService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Path("coa/chart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChartSearchResource {

    @GET
    public List<Map<String, Object>> lookupChartByPrimaryKey(@QueryParam("code") String chartOfAccountsCode) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        final ConfigurationService configurationService = SpringContext.getBean(ConfigurationService.class);
        final ChartSearch chartSearch = new ChartSearch(lookupService, dataDictionaryService, configurationService);
        return Arrays.asList(chartSearch.retrieveByChartCode(chartOfAccountsCode));
    }


}

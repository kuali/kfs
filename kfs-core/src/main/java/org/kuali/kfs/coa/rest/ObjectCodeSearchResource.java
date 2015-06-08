package org.kuali.kfs.coa.rest;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.coa.businessobject.lookup.ObjectCodeSearch;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.LookupService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Path("coa/object")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ObjectCodeSearchResource {


    @GET
    @Path("/{code}")
    public List<ObjectCode> lookupObjectCodeByPrimaryKey(@QueryParam("fiscalYYear") Integer fiscalYear, @QueryParam("chartCode") String chartOfAccountsCode, @QueryParam("objectCode") String objectCode) {
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final ObjectCodeSearch objectSearch = new ObjectCodeSearch(lookupService);
        return Arrays.asList(objectSearch.retrieveByPK(fiscalYear, chartOfAccountsCode, objectCode));
    }
}

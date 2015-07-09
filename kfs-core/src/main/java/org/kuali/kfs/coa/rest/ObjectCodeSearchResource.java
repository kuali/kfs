package org.kuali.kfs.coa.rest;

import io.swagger.annotations.Api;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.lookup.ChartSearch;
import org.kuali.kfs.coa.businessobject.lookup.ObjectCodeSearch;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.LookupService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Api(value = "coa/objectCode")
@Path("coa/objectCode")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ObjectCodeSearchResource {
    private final UniversityDateService universityDateService;

    public ObjectCodeSearchResource(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


    @GET
   // @Path("/{code}")
    public List<ObjectCode> lookupObjectCodeByPrimaryKey(@QueryParam("fiscalYear") Integer fiscalYear, @QueryParam("chartCode") String chartOfAccountsCode, @QueryParam("objectCode") String objectCode) {
        if (fiscalYear == null) {
            fiscalYear = universityDateService.getCurrentFiscalYear();
        }
        final LookupService lookupService = SpringContext.getBean(LookupService.class);
        final ObjectCodeSearch objectSearch = new ObjectCodeSearch(lookupService);
        return Arrays.asList(objectSearch.retrieveByPK(fiscalYear, chartOfAccountsCode, objectCode));
    }
}

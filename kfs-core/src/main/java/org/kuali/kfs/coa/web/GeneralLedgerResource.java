package org.kuali.kfs.coa.web;

import org.kuali.kfs.coa.service.GeneralLedgerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeneralLedgerResource {
    private static volatile UniversityDateService universityDateService;
    private static volatile GeneralLedgerService generalLedgerService;

    @GET
    @Path("accountBalanceByConsolidation/chart/{chartOfAccountsCode}/account/{accountNumber}")
    public Map<String, Object> getGeneralLedgerResource(@PathParam("chartOfAccountsCode") String chartOfAccountsCode, @PathParam("accountNumber") String accountNumber) {
        //final Integer fiscalYear = getUniversityDateService().getCurrentFiscalYear();
        final Integer fiscalYear = 2009;
        return getGeneralLedgerService().lookupAccountBalancesByConsolidation(fiscalYear, chartOfAccountsCode, accountNumber);
    }

    public UniversityDateService getUniversityDateService(){
        if (universityDateService == null) {
            universityDateService = SpringContext.getBean(UniversityDateService.class);
        }
        return universityDateService;
    }

    public GeneralLedgerService getGeneralLedgerService() {
        if (generalLedgerService == null) {
            generalLedgerService = SpringContext.getBean(GeneralLedgerService.class);
        }
        return generalLedgerService;
    }
}

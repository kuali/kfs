package org.kuali.kfs.sys.web;

import com.sun.jersey.spi.resource.Singleton;
import org.kuali.kfs.sys.businessobject.datadictionary.FinancialSystemBusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;

import javax.ws.rs.*;

@Singleton
@Path("/datadictionary")
@Produces("application/json")
@Consumes("application/json")
public class DataDictionaryResource {
    private DataDictionaryService dataDictionaryService;

    public DataDictionaryResource(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    @GET
    @Path("/businessObjectEntry/{businessObjectName}")
    public FinancialSystemBusinessObjectEntry getBusinessObjectEntry(@PathParam("businessObjectName") String businessObjectName) {
        return (FinancialSystemBusinessObjectEntry)dataDictionaryService.getDataDictionary().getBusinessObjectEntry(businessObjectName);
    }
}

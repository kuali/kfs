package org.kuali.kfs.sys.web;

import com.sun.jersey.spi.resource.Singleton;
import org.kuali.kfs.sys.businessobject.datadictionary.FinancialSystemBusinessObjectEntry;
import org.kuali.kfs.sys.service.DataDictionaryService;

import javax.ws.rs.*;
import java.util.List;
import java.util.Map;

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

    @GET
    @Path("/businessObjectEntry")
    public List<Map<String, Object>> getAllBusinessObjectEntries() {
        return dataDictionaryService.getBusinessObjectEntriesList();
    }
}

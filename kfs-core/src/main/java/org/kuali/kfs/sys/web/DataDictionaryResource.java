package org.kuali.kfs.sys.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.spi.resource.Singleton;
import org.kuali.kfs.sys.businessobject.datadictionary.FinancialSystemBusinessObjectEntry;
import org.kuali.kfs.sys.service.DataDictionaryService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @PUT
    @Path("/businessObjectEntry/{businessObjectName}")
    public Response putBusinessObjectEntry(@PathParam("businessObjectName") String businessObjectName, byte[] businessObjectEntryAsJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        FinancialSystemBusinessObjectEntry financialSystemBusinessObjectEntry = mapper.readValue(businessObjectEntryAsJson, FinancialSystemBusinessObjectEntry.class);
        dataDictionaryService.updateDictionaryEntry(financialSystemBusinessObjectEntry);
        Map<String, String> responseMap= new ConcurrentHashMap<>();
        responseMap.put("message", "business object entry updated successfully");
        return Response.ok(responseMap).build();
    }
}

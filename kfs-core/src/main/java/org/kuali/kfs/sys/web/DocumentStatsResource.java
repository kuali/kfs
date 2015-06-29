package org.kuali.kfs.sys.web;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.DocumentStatsService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DocumentStatsResource {
    protected volatile static DocumentStatsService documentStatsService;

    @GET
    @Path("initiatedDocumentsByDocumentType")
    public List<Map<String, Integer>> getInitiatedDocsByDocumentType(@DefaultValue("10")@QueryParam("limit") Integer limit,
                                                                     @DefaultValue("30") @QueryParam("days") Integer days) {
        return getDocumentStatsService().reportNumInitiatedDocsByDocType(limit, days);
    }

    @GET
    @Path("reportNumDocsByStatusByDocType")
    public List<Map<String, Map<String, Integer>>> reportNumDocsByStatusByDocType(@DefaultValue("5")@QueryParam("limit") Integer limit,
                                                                     @DefaultValue("30") @QueryParam("days") Integer days) {
        return getDocumentStatsService().reportNumDocsByStatusByDocType(limit, days);
    }

    protected static DocumentStatsService getDocumentStatsService() {
        if (documentStatsService == null) {
            documentStatsService = SpringContext.getBean(DocumentStatsService.class);
        }
        return documentStatsService;
    }
}

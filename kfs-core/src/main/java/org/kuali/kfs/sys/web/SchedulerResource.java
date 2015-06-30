package org.kuali.kfs.sys.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerResource {

    protected static volatile SchedulerService schedulerService;


    @GET
    @Path("jobs")
    public Map<String, List<BatchJobStatus>> getGroupedJobs() {
        return getSchedulerService().getGroupedJobs();
    }


    @GET
    @Path("job/{groupId}/{jobName}")
    public BatchJobStatus getJobDetail(@PathParam("groupId") String groupId, @PathParam("jobName") String jobName) {
        return getSchedulerService().getJob(groupId, jobName);
    }

    @POST
    @Path("job/{groupId}/{jobName}")
    public Response modifyJob(@PathParam("groupId") String groupId,
                              @PathParam("jobName") String jobName,
                              ObjectNode body) {
        if (body.has("command")) {
            String command = body.get("command").asText();
            BatchJobStatus job = getSchedulerService().getJob(groupId, jobName);
            switch (command) {
                case "schedule":
                    job.schedule();
                    return Response.ok().build();
                case "unschedule" :
                    job.unschedule();
                    return Response.ok().build();
                default: return Response.status(400).entity("Command not recognized").build();
            }

        }
        return Response.status(400).entity("Command not recognized").build();
    }

    protected SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService = SpringContext.getBean(SchedulerService.class);
        }
        return schedulerService;
    }
}

package org.kuali.kfs.sys.web;

import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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

    protected SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService = SpringContext.getBean(SchedulerService.class);
        }
        return schedulerService;
    }
}

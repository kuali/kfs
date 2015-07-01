package org.kuali.kfs.sys.web;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.DateTime;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerResource {

    protected static volatile SchedulerService schedulerService;
    protected static volatile DateTimeService dateTimeService;


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
    @Path("job/scheduler/{groupId}/{jobName}")
    public Response scheduleJob(@PathParam("groupId") String groupId,
                              @PathParam("jobName") String jobName) {

        BatchJobStatus job = getSchedulerService().getJob(groupId, jobName);
        if (job != null) {
            job.schedule();
            return Response.ok(job).build();
        }
        return Response.status(404).entity("Specified job does not exist").build();
    }

    @POST
    @Path("job/unscheduler/{groupId}/{jobName}")
    public Response unscheduleJob(@PathParam("groupId") String groupId,
                              @PathParam("jobName") String jobName) {
        BatchJobStatus job = getSchedulerService().getJob(groupId, jobName);
        if (job != null) {
            job.unschedule();
            BatchJobStatus unscheduledJob = getSchedulerService().getJob("unscheduled", jobName);
            return Response.ok(unscheduledJob).build();
        }
        return Response.status(404).entity("Specified job does not exist").build();
    }

    @POST
    @Path("job/runner/{groupId}/{jobName}")
    public Response createJob(@PathParam("groupId") String groupId,
                              @PathParam("jobName") String jobName,
                              JsonNode body) {
        BatchJobStatus job = getSchedulerService().getJob(groupId, jobName);
        if (job != null) {
            int startStep = 1;
            if (body.has("startStep")) {
                 startStep = body.get("startStep").asInt();
            }

            int endStep = job.getNumSteps();
            if (body.has("endStep")) {
                endStep = body.get("startStep").asInt();
            }

            if (startStep > endStep) {
                return Response.status(400).entity("Invalid input").build();
            }

            DateTime startDateTime = new DateTime();
            if (body.has("startDate")) {
                try {
                    Date parsedDate = getDateTimeService().convertToDate(body.get("startDate").asText());
                    DateTime parsedDateTime = new DateTime(parsedDate);
                    startDateTime = startDateTime.withYear(parsedDateTime.getYear());
                    startDateTime = startDateTime.withMonthOfYear(parsedDateTime.getMonthOfYear());
                    startDateTime = startDateTime.withDayOfMonth(parsedDateTime.getDayOfMonth());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            if (body.has("startTime")) {
                try {
                    Date parsedDate = getDateTimeService().convertToDateTime(body.get("startTime").asText());
                    DateTime parsedDateTime = new DateTime(parsedDate);
                    startDateTime = startDateTime.withHourOfDay(parsedDateTime.getHourOfDay());
                    startDateTime = startDateTime.withMinuteOfHour(parsedDateTime.getMinuteOfHour());
                    startDateTime = startDateTime.withSecondOfMinute(parsedDateTime.getSecondOfMinute());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            String emailAddress = null;
            if (body.has("resultsEMail")) {
                emailAddress = body.get("resultsEMail").asText();
            }

            job.runJob(startStep, endStep, startDateTime.toDate(), emailAddress);

            return Response.ok(job).build();
        }
        return Response.status(404).entity("Specified job does not exist").build();
    }

    protected SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService = SpringContext.getBean(SchedulerService.class);
        }
        return schedulerService;
    }

    protected DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }
}

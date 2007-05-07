/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.web.struts.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.BatchJobStatus;
import org.kuali.kfs.service.SchedulerService;
import org.kuali.kfs.util.SpringServiceLocator;

public class KualiBatchJobModifyAction extends KualiAction {
    
    private static final String JOB_NAME_PARAMETER = "name";
    private static final String JOB_GROUP_PARAMETER = "group";
    private static final String START_STEP_PARAMETER = "startStep";
    private static final String END_STEP_PARAMETER = "endStep";
    private static final String START_TIME_PARAMETER = "startTime";
    private static final String EMAIL_PARAMETER = "emailAddress";
    private static final String START_TIME_FORMAT = "MM/dd/yyyy HH:mm";
    private static final String JOB_ADMIN_PARAMETER_SUFFIX = "_WORKGROUP";

    private static SchedulerService schedulerService;
    private static KualiConfigurationService configService;
    
    private SchedulerService getSchedulerService() {
        if ( schedulerService == null ) {
            schedulerService = SpringServiceLocator.getSchedulerService();
        }
        return schedulerService;
    }
    
    public static KualiConfigurationService getConfigService() {
        if ( configService == null ) {
            configService = SpringServiceLocator.getKualiConfigurationService();
        }
        return configService;
    }

    private BatchJobStatus getJob( HttpServletRequest request ) {
        // load the given job and map into the form
        String jobName = request.getParameter( JOB_NAME_PARAMETER );
        String jobGroup = request.getParameter( JOB_GROUP_PARAMETER );
        
        return getSchedulerService().getJob( jobGroup, jobName );        
    }

    
    private boolean canModifyJob( BatchJobStatus job, String actionType ) {
        try {
            checkJobAuthorization(job, actionType);
        } catch ( AuthorizationException ex ) {
            return false;
        }
        return true;
    }
    
    /**
     * Performs the actual authorization check for a given job and action against the current user.  This 
     * method can be overridden by sub-classes if more granular controls are desired.
     * 
     * @param job
     * @param actionType
     * @throws AuthorizationException
     */
    protected void checkJobAuthorization( BatchJobStatus job, String actionType ) throws AuthorizationException {
        if ( getConfigService().hasApplicationParameter(KFSConstants.ParameterGroups.SYSTEM, KFSConstants.SystemGroupParameterNames.JOB_ADMIN_WORKGROUP) ) {            
            String adminWorkgroup = getConfigService().getApplicationParameterValue(KFSConstants.ParameterGroups.SYSTEM, KFSConstants.SystemGroupParameterNames.JOB_ADMIN_WORKGROUP);
            if ( !GlobalVariables.getUserSession().getUniversalUser().isMember(adminWorkgroup) ) {
                throw new AuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), actionType, job.getFullName() );
            }
        }
        if ( getConfigService().hasApplicationParameter(KFSConstants.ParameterGroups.SYSTEM, job.getName() + JOB_ADMIN_PARAMETER_SUFFIX) ) {
            String jobSpecificAdminWorkgroup = getConfigService().getApplicationParameterValue(KFSConstants.ParameterGroups.SYSTEM, job.getName() + JOB_ADMIN_PARAMETER_SUFFIX );
            if ( !GlobalVariables.getUserSession().getUniversalUser().isMember(jobSpecificAdminWorkgroup) ) {
                throw new AuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), actionType, job.getFullName() );
            }
            
        }
    }
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {        
        BatchJobStatus job = getJob( request );
        
        request.setAttribute( "job", job );
        request.setAttribute( "canRunJob", canModifyJob(job, "runJob") );
        request.setAttribute( "canSchedule", canModifyJob(job, "schedule") );
        request.setAttribute( "canUnschedule", canModifyJob(job, "unschedule") );
        request.setAttribute( "canStopJob", canModifyJob(job, "stopJob") );
        request.setAttribute( "userEmailAddress", GlobalVariables.getUserSession().getUniversalUser().getPersonEmailAddress() );
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward runJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob( request );
        
        checkJobAuthorization( job, "runJob" );
        
        String startStepStr = request.getParameter(START_STEP_PARAMETER);
        String endStepStr = request.getParameter(END_STEP_PARAMETER);
        String startTimeStr = request.getParameter(START_TIME_PARAMETER);
        String emailAddress = request.getParameter( EMAIL_PARAMETER );
        
        int startStep = Integer.parseInt(startStepStr);
        int endStep = Integer.parseInt(endStepStr);
        Date startTime = new Date();
        if ( !StringUtils.isBlank(startTimeStr) ) {
            startTime = new SimpleDateFormat( START_TIME_FORMAT ).parse(startTimeStr);
        }        
        
        job.runJob( startStep, endStep, startTime, emailAddress );
        
        // redirect to display form to prevent re-execution of the job by mistake
        return new ActionForward( "/batchModify.do?methodToCall=start&name=" + UrlFactory.encode( job.getName() ) + "&group=" + UrlFactory.encode( job.getGroup() ), true );
    }

    public ActionForward stopJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob( request );

        checkJobAuthorization( job, "stopJob" );

        job.interrupt();        
        
        return new ActionForward( "/batchModify.do?methodToCall=start&name=" + UrlFactory.encode( job.getName() ) + "&group=" + UrlFactory.encode( job.getGroup() ), true );
    }
    

    public ActionForward schedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob( request );
        
        checkJobAuthorization( job, "schedule" );

        job.schedule();
        
        return new ActionForward( "/batchModify.do?methodToCall=start&name=" + UrlFactory.encode( job.getName() ) + "&group=" + UrlFactory.encode( job.getGroup() ), true );
    }

    public ActionForward unschedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob( request );

        checkJobAuthorization( job, "unschedule" );
        
        job.unschedule();

        // move to the unscheduled job object since the scheduled one has been removed
        job = getSchedulerService().getJob( SchedulerService.UNSCHEDULED_GROUP, job.getName() );
        
        return new ActionForward( "/batchModify.do?methodToCall=start&name=" + UrlFactory.encode( job.getName() ) + "&group=" + UrlFactory.encode( job.getGroup() ), true );
    }
}

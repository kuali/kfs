/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.web.struts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class KualiBatchJobModifyAction extends KualiAction {

    private static final String JOB_NAME_PARAMETER = "name";
    private static final String JOB_GROUP_PARAMETER = "group";
    private static final String START_STEP_PARAMETER = "startStep";
    private static final String END_STEP_PARAMETER = "endStep";
    private static final String START_TIME_PARAMETER = "startTime";
    private static final String EMAIL_PARAMETER = "emailAddress";

    private static SchedulerService schedulerService;
    private static ParameterService parameterService;
    private static IdentityManagementService identityManagementService;
    private static DateTimeService dateTimeService;
    
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        if (form instanceof KualiBatchJobModifyForm) {
            if (!getIdentityManagementService().isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS, KRADUtils.getNamespaceAndComponentSimpleName(BatchJobStatus.class), new HashMap<String,String>(getRoleQualification(form, "use")))) {
                throw new AuthorizationException(GlobalVariables.getUserSession().getPrincipalName(), "view", "batch jobs");
            }
        }
        else {
            super.checkAuthorization(form, methodToCall);
        }
    }

    /**
     * Performs the actual authorization check for a given job and action against the current user. This method can be overridden by
     * sub-classes if more granular controls are desired.
     * 
     * @param job
     * @param actionType
     * @throws AuthorizationException
     */
    protected boolean canModifyJob(KualiBatchJobModifyForm form, String actionType) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, form.getJob().getNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.BEAN_NAME, form.getJob().getName());
        return getIdentityManagementService().isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KNS_NAMESPACE, KFSConstants.PermissionTemplate.MODIFY_BATCH_JOB.name, permissionDetails, new HashMap<String,String>(getRoleQualification(form, actionType)));
    }
    
    protected void checkJobAuthorization(KualiBatchJobModifyForm form, String actionType) throws AuthorizationException {
        if (!canModifyJob(form, actionType)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPrincipalName(), "actionType", form.getJob().getName());
        }
    }
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // load the given job and map into the form
        String jobName = request.getParameter(JOB_NAME_PARAMETER);
        String jobGroup = request.getParameter(JOB_GROUP_PARAMETER);
        if (form instanceof KualiBatchJobModifyForm) {
            ((KualiBatchJobModifyForm)form).setJob(getSchedulerService().getJob(jobGroup, jobName));
        }
        ActionForward forward = super.execute(mapping, form, request, response);
        return forward;
    }

    private IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    private SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService = SpringContext.getBean(SchedulerService.class);
        }
        return schedulerService;
    }

    public static ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm)form;

        request.setAttribute("job", batchModifyForm.getJob());
        request.setAttribute("canRunJob", canModifyJob(batchModifyForm, "runJob"));
        request.setAttribute("canSchedule", canModifyJob(batchModifyForm, "schedule"));
        request.setAttribute("canUnschedule", canModifyJob(batchModifyForm, "unschedule"));
        request.setAttribute("canStopJob", canModifyJob(batchModifyForm, "stopJob"));
        request.setAttribute("userEmailAddress", GlobalVariables.getUserSession().getPerson().getEmailAddressUnmasked());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward runJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm)form;

        checkJobAuthorization(batchModifyForm, "runJob");

        String startStepStr = request.getParameter(START_STEP_PARAMETER);
        String endStepStr = request.getParameter(END_STEP_PARAMETER);
        String startTimeStr = request.getParameter(START_TIME_PARAMETER);
        String emailAddress = request.getParameter(EMAIL_PARAMETER);

        int startStep = Integer.parseInt(startStepStr);
        int endStep = Integer.parseInt(endStepStr);
        Date startTime;
        if (!StringUtils.isBlank(startTimeStr)) {
            startTime = getDateTimeService().convertToDateTime(startTimeStr);
        } else {
            startTime = getDateTimeService().getCurrentDate();
        }

        batchModifyForm.getJob().runJob(startStep, endStep, startTime, emailAddress);

        // redirect to display form to prevent re-execution of the job by mistake
        return getForward(batchModifyForm.getJob());
    }

    public ActionForward stopJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm)form;

        checkJobAuthorization(batchModifyForm, "stopJob");

        batchModifyForm.getJob().interrupt();

        return getForward(batchModifyForm.getJob());
    }


    public ActionForward schedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm)form;

        checkJobAuthorization(batchModifyForm, "schedule");

        batchModifyForm.getJob().schedule();

        return getForward(batchModifyForm.getJob());
    }

    public ActionForward unschedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiBatchJobModifyForm batchModifyForm = (KualiBatchJobModifyForm)form;

        checkJobAuthorization(batchModifyForm, "unschedule");

        batchModifyForm.getJob().unschedule();

        // move to the unscheduled job object since the scheduled one has been removed
        batchModifyForm.setJob(getSchedulerService().getJob(SchedulerService.UNSCHEDULED_GROUP, batchModifyForm.getJob().getName()));

        return getForward(batchModifyForm.getJob());
    }

    private ActionForward getForward(BatchJobStatus job) {
        return new ActionForward(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY) + "/batchModify.do?methodToCall=start&name=" + UrlFactory.encode(job.getName()) + "&group=" + UrlFactory.encode(job.getGroup()), true);
    }

    public static DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }
}


/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.service.impl;

import org.kuali.module.kra.routingform.service.ProjectTypeService;
import org.kuali.module.kra.routingform.service.PurposeService;
import org.kuali.module.kra.routingform.service.RoutingFormMainPageService;
import org.kuali.module.kra.routingform.service.SubmissionTypeService;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormMainPageServiceImpl implements RoutingFormMainPageService {

    private ProjectTypeService projectTypeService;
    private SubmissionTypeService submissionTypeService;
    private PurposeService purposeService;
    
    public void initializeRoutingFormMainPage(RoutingForm routingForm) {
        routingForm.setProjectTypes(projectTypeService.getProjectTypes());
        routingForm.setSubmissionTypes(submissionTypeService.getSubmissionTypes());
        routingForm.setPurposes(purposeService.getPurposes());
    }

    public ProjectTypeService getProjectTypeService() {
        return projectTypeService;
    }

    public void setProjectTypeService(ProjectTypeService projectTypeService) {
        this.projectTypeService = projectTypeService;
    }

    public PurposeService getPurposeService() {
        return purposeService;
    }

    public void setPurposeService(PurposeService purposeService) {
        this.purposeService = purposeService;
    }

    public SubmissionTypeService getSubmissionTypeService() {
        return submissionTypeService;
    }

    public void setSubmissionTypeService(SubmissionTypeService submissionTypeService) {
        this.submissionTypeService = submissionTypeService;
    }
}

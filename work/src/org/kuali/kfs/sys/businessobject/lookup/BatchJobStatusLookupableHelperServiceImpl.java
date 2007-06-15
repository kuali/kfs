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
package org.kuali.kfs.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.BatchJobStatus;
import org.kuali.kfs.service.SchedulerService;

public class BatchJobStatusLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchJobStatusLookupableHelperServiceImpl.class);
    
    private SchedulerService schedulerService;
    private KualiConfigurationService configService;
    
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        super.setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        super.setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        List<BatchJobStatus> allJobs = schedulerService.getJobs();
        List<BatchJobStatus> jobs = new ArrayList<BatchJobStatus>();
        String nameValue = fieldValues.get( "name" );
        Pattern namePattern = null;
        if ( !StringUtils.isEmpty(nameValue) ) {
            namePattern = Pattern.compile(nameValue.replace("*", ".*"), Pattern.CASE_INSENSITIVE );
        }
        String schedulerGroup = fieldValues.get( "group" );
        String jobStatus = fieldValues.get( "status" );
        for ( BatchJobStatus job : allJobs ) {
            if ( namePattern != null && !namePattern.matcher(job.getName()).matches() ) {
                continue; // match failed, skip this entry
            }
            if ( !StringUtils.isEmpty(schedulerGroup) && !schedulerGroup.equalsIgnoreCase( job.getGroup() ) ) {
                continue;
            }
            if ( !StringUtils.isEmpty(jobStatus) && !jobStatus.equalsIgnoreCase( job.getStatus() ) ) {
                continue;
            }
            jobs.add(job);
        }
        
        return jobs;
    }

    @Override
    public String getActionUrls(BusinessObject businessObject) {
        if ( businessObject instanceof BatchJobStatus ) {
            BatchJobStatus job = (BatchJobStatus)businessObject;
            String linkText = "Modify";
            StringBuffer sb = new StringBuffer();
            if ( configService.hasApplicationParameter(KFSConstants.ParameterGroups.SYSTEM,KFSConstants.SystemGroupParameterNames.JOB_ADMIN_WORKGROUP) ) {            
                String adminWorkgroup = configService.getApplicationParameterValue(KFSConstants.ParameterGroups.SYSTEM, KFSConstants.SystemGroupParameterNames.JOB_ADMIN_WORKGROUP);
                if ( !GlobalVariables.getUserSession().getUniversalUser().isMember(adminWorkgroup) ) {
                    linkText = "View";
                }
            }
            sb.append( "<a href=\"" + configService.getPropertyString(KFSConstants.APPLICATION_URL_KEY) + "/batchModify.do?methodToCall=start&name=" ).append( UrlFactory.encode( job.getName() ) )
                    .append( "&group=" ).append( UrlFactory.encode( job.getGroup() ) ).append( "\">" ).append( linkText ).append( "</a>" );

            return sb.toString();
        }
        return "&nbsp;";
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

}

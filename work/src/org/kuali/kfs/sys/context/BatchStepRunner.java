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
package org.kuali.kfs.context;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.UserSession;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.BatchSpringContext;
import org.kuali.kfs.batch.Step;

public class BatchStepRunner {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchStepRunner.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: You must pass the name of the step to run on the command line.");
            System.exit(8);
        }
        try {
            Log4jConfigurer.configureLogging(false);
            SpringContext.initializeApplicationContext();
            String[] stepNames;
            if (args[0].indexOf(",") > 0) {
                stepNames = StringUtils.split(args[0], ",");
            }
            else {
                stepNames = new String[] {args[0]};
            }
            
            String jobName = KFSConstants.BATCH_STEP_RUNNER_JOB_NAME;
            if (args.length >= 2) {
                jobName = args[1];
            }
            for (int i = 0; i < stepNames.length; i++) {
                runStep(stepNames[i], jobName);
            }
            System.exit(0);
        }
        catch (Throwable t) {
            System.err.println("ERROR: Exception caught: ");
            t.printStackTrace(System.err);
            System.exit(8);
        }
    }
    
    private static void runStep(String stepName, String jobName) throws Exception {
        GlobalVariables.setErrorMap(new ErrorMap());
        GlobalVariables.setMessageList(new ArrayList());
        String stepUserParameter = "USER";
        KualiConfigurationService configService = SpringContext.getBean(KualiConfigurationService.class);
        LOG.debug("runStep() Retrieving step " + stepName);
        Step step = BatchSpringContext.getStep(stepName);
        try {
	        if (configService.parameterExists(step.getNamespace(), step.getComponentName(), stepUserParameter)) {
	            GlobalVariables.setUserSession(new UserSession(configService.getParameterValue(step.getNamespace(), step.getComponentName(), stepUserParameter)));
	        }
        } catch ( Exception ex ) {
        	// database may not be created yet, if performing the initial import - handle the database error which results
        	LOG.warn( "error checking application parameter", ex );
        }
        String stepRunIndicatorParameter = "RUN_IND";
        boolean skipStep = false;
        try {
        	skipStep = !configService.getIndicatorParameter(step.getNamespace(), step.getComponentName(), stepRunIndicatorParameter);
        } catch ( Exception ex ) {
        	// database may not be created yet, if performing the initial import - handle the database error which results
        	LOG.warn( "error checking application parameter", ex );
        }
        if ( skipStep ) {
            LOG.info("runStep() Skipping step " + stepName + " due to flag turned off");
        } else {
            LOG.info("runStep() Running step " + stepName);
            if (step.execute(jobName)) {
                LOG.info("runStep() Step successful - continue");
            } else {
                LOG.info("runStep() Step successful - stop job");
                System.exit(4);
            }
        }
    }
 }
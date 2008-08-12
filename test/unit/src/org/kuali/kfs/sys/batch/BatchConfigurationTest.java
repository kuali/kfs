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
package org.kuali.kfs.sys.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.KualiModule;
import org.kuali.core.service.KualiModuleService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.PreCommitSuite;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Tests the spring configuration for batch jobs.
 */
@ConfigureContext(initializeBatchSchedule=true)
@AnnotationTestSuite(PreCommitSuite.class)
public class BatchConfigurationTest extends KualiTestBase {
    private List<KualiModule> modules;
    private Collection<JobDescriptor> jobDescriptors;
    private Collection<TriggerDescriptor> triggerDescriptors;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        modules = SpringContext.getBean(KualiModuleService.class).getInstalledModules();
        jobDescriptors = SpringContext.getBeansOfType(JobDescriptor.class).values();
        triggerDescriptors = SpringContext.getBeansOfType(TriggerDescriptor.class).values();
    }

    /**
     * Verifies that registered job names correspond to JobDescriptor beans.
     */
    public final void testRegisteredJobsExist() throws Exception {
        List<String> nonExistentJobNames = new ArrayList();
        StringBuffer errorMessage = new StringBuffer("The following registered job names do not correspond to JobDescriptor beans:");
        for (KualiModule module : modules) {
            for (String jobName : module.getJobNames()) {
                try {
                    BatchSpringContext.getJobDescriptor(jobName);
                }
                catch (NoSuchBeanDefinitionException e) {
                    nonExistentJobNames.add(jobName);
                    errorMessage.append("\n\t").append(module.getModuleCode()).append(": ").append(jobName);
                }
            }
        }
        assertTrue(errorMessage.toString(), nonExistentJobNames.isEmpty());
    }

    /**
     * Verifies that registered trigger names correspond to TriggerDescriptor beans.
     */
    public final void testRegisteredTriggersExist() throws Exception {
        List<String> nonExistentTriggerNames = new ArrayList();
        StringBuffer errorMessage = new StringBuffer("The following registered trigger names do not correspond to TriggerDescriptor beans:");
        for (KualiModule module : modules) {
            for (String triggerName : module.getTriggerNames()) {
                try {
                    BatchSpringContext.getTriggerDescriptor(triggerName);
                }
                catch (NoSuchBeanDefinitionException e) {
                    nonExistentTriggerNames.add(triggerName);
                    errorMessage.append("\n\t").append(module.getModuleCode()).append(": ").append(triggerName);
                }
            }
        }
        assertTrue(errorMessage.toString(), nonExistentTriggerNames.isEmpty());
    }

    /**
     * Verifies that beans of type JobDescriptor are registered with KFS as a whole or with an individual module.
     */
    public final void testJobsRegistered() throws Exception {
        List<String> unregisteredJobNames = new ArrayList();
        StringBuffer errorMessage = new StringBuffer("The following JobDescriptor beans are not registered with KFS as a whole or any module:");
        for (JobDescriptor jobDescriptor : SpringContext.getBeansOfType(JobDescriptor.class).values()) {
            boolean isRegistered = false;
            for (KualiModule module : modules) {
                if (module.getJobNames().contains(jobDescriptor.getJobDetail().getName())) {
                    isRegistered = true;
                    break;
                }
            }
            if (!isRegistered) {
                unregisteredJobNames.add(jobDescriptor.getJobDetail().getName());
                errorMessage.append("\n\t").append(jobDescriptor.getJobDetail().getFullName());
            }
        }
        assertTrue(errorMessage.toString(), unregisteredJobNames.isEmpty());
    }

    /**
     * Verifies that beans of type TriggerDescriptor are registered with KFS as a whole or with an individual module.
     */
    public final void testTriggersRegistered() throws Exception {
        List<String> unregisteredTriggerNames = new ArrayList();
        StringBuffer errorMessage = new StringBuffer("The following TriggerDescriptor beans are not registered with KFS as a whole or any module:");
        for (TriggerDescriptor triggerDescriptor : SpringContext.getBeansOfType(TriggerDescriptor.class).values()) {
            boolean isRegistered = false;
            for (KualiModule module : modules) {
                if (module.getTriggerNames().contains(triggerDescriptor.getTrigger().getName())) {
                    isRegistered = true;
                    break;
                }
            }
            if (!isRegistered) {
                unregisteredTriggerNames.add(triggerDescriptor.getTrigger().getName());
                errorMessage.append("\n\t").append(triggerDescriptor.getTrigger().getFullName());
            }
        }
        assertTrue(errorMessage.toString(), unregisteredTriggerNames.isEmpty());
    }

    /**
     * Verifies that dependecies listed for a name defined JobDescriptor beans.
     */
    public final void testDependenciesExist() throws Exception {
        List<String> nonExistentDependencies = new ArrayList();
        StringBuffer errorMessage = new StringBuffer("The following dependencies do not refer to JobDescriptor beans:");
        for (JobDescriptor jobDescriptor : SpringContext.getBeansOfType(JobDescriptor.class).values()) {
            for (String dependencyJobName : jobDescriptor.getDependencies().keySet()) {
                try {
                    BatchSpringContext.getJobDescriptor(dependencyJobName);
                }
                catch (NoSuchBeanDefinitionException e) {
                    nonExistentDependencies.add(dependencyJobName);
                    errorMessage.append("\n\t").append(jobDescriptor.getJobDetail().getFullName()).append("depends on: ").append(dependencyJobName);
                }
            }
        }
        assertTrue(errorMessage.toString(), nonExistentDependencies.isEmpty());
    }
}

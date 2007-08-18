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
package org.kuali.kfs.batch;

import java.util.Map;

import org.kuali.kfs.context.SpringContext;

public class BatchSpringContext {
    public static Map getBatchComponents() {
        return SpringContext.getBeansOfType(Map.class).get("kfsBatchComponents");
    }

    public static Step getStep(String beanId) {
        return SpringContext.getBeansOfType(Step.class).get(beanId);
    }

    public static JobDescriptor getJobDescriptor(String beanId) {
        return SpringContext.getBeansOfType(JobDescriptor.class).get(beanId);
    }

    public static TriggerDescriptor getTriggerDescriptor(String beanId) {
        return SpringContext.getBeansOfType(TriggerDescriptor.class).get(beanId);
    }

    public static BatchInputFileType getBatchInputFileType(String beanId) {
        return SpringContext.getBeansOfType(BatchInputFileType.class).get(beanId);
    }

    public static BatchInputFileSetType getBatchInputFileSetType(String beanId) {
        return SpringContext.getBeansOfType(BatchInputFileSetType.class).get(beanId);
    }
}

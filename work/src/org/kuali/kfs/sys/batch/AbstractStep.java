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

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractStep implements Step, BeanNameAware {
    private String name;
    private KualiConfigurationService configurationService;
    private DateTimeService dateTimeService;
    private boolean interrupted = false;

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name) {
        this.name = name;
    }

    /**
     * Gets the name attribute.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the configurationService attribute.
     * 
     * @return Returns the configurationService.
     */
    protected KualiConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Sets the configurationService attribute value.
     * 
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
    
    public void interrupt() {
        this.interrupted = true;
    }
}

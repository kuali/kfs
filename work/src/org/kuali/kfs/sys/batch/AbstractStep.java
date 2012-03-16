/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.sys.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractStep extends InitiateDirectoryBase implements Step, BeanNameAware, InitializingBean, InitiateDirectory{

    private static final Logger LOG = Logger.getLogger(AbstractStep.class);

    protected String name;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected BatchInputFileType batchInputFileType = null;

    protected boolean interrupted = false;

    /**
     * Initialization  after bean properties are instantiate,
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //prepare the directories by using the required directory list
        prepareDirectories(getRequiredDirectoryNames());
    }

    /**
     * By default it should use batchInpeutFile (single file) directory path as the required directory name.
     *
     * Subclasses should override this function to provide any custom required directory list.
     *
     * @see org.kuali.kfs.sys.batch.service.InitiateDirectoryInterface#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        List<String> requiredDirectoryList = new ArrayList<String>();
        if (batchInputFileType != null){
            LOG.info(batchInputFileType.getClass().getName() + " ==> " + batchInputFileType.getDirectoryPath());
            requiredDirectoryList.add(batchInputFileType.getDirectoryPath());
        }
        return requiredDirectoryList;
    }

    /**
     * Sets the bean name
     *
     * @param name String that contains the bean name
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    /**
     * Gets the name attribute.
     *
     * @return Returns the name.
     */
    @Override
    public String getName() {
        return name;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
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

    /**
     * Returns the boolean value of the interrupted flag
     *
     * @return boolean
     * @see org.kuali.kfs.sys.batch.Step#isInterrupted()
     */
    @Override
    public boolean isInterrupted() {
        return interrupted;
    }

    public BatchInputFileType getBatchInputFileType() {
        return batchInputFileType;
    }

    public void setBatchInputFileType(BatchInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }


    /**
     * Sets the interruped flag
     *
     * @param interrupted
     * @see org.kuali.kfs.sys.batch.Step#setInterrupted(boolean)
     */
    @Override
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * Initializes the interrupted flag
     *
     * @see org.kuali.kfs.sys.batch.Step#interrupt()
     */
    @Override
    public void interrupt() {
        this.interrupted = true;
    }
}

/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.batch;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;


public abstract class AbstractWrappedBatchStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(AbstractWrappedBatchStep.class);
    private WrappedBatchExecutorService wrappedBatchExecutorService;
    private List<WrappingBatchService> wrappingBatchServices;

    public boolean execute(String jobName, Date jobRunDate) {
        return wrappedBatchExecutorService.execute(wrappingBatchServices, getCustomBatchExecutor());
    }

    protected abstract WrappedBatchExecutorService.CustomBatchExecutor getCustomBatchExecutor();

    public void setWrappedBatchExecutorService(WrappedBatchExecutorService wrappedBatchExecutorService) {
        this.wrappedBatchExecutorService = wrappedBatchExecutorService;
    }

    public void setWrappingBatchServices(List<WrappingBatchService> wrappingBatchServices) {
        this.wrappingBatchServices = wrappingBatchServices;
    }
}

/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class CustomerLoadStep extends AbstractStep {

    private CustomerLoadService batchService;

    /**
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return batchService.getRequiredDirectoryNames();
    }

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return batchService.loadFiles();
    }

    public void setBatchService(CustomerLoadService batchService) {
        this.batchService = batchService;
    }

}

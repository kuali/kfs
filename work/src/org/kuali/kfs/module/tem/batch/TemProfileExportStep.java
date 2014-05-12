/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch;



import java.util.Date;

import org.kuali.kfs.module.tem.batch.service.TemProfileExportService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class TemProfileExportStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TemProfileExportStep.class);
    protected TemProfileExportService temProfileExportService;

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
	    temProfileExportService.exportProfile();
		return false;
	}

    public void setTemProfileExportService(TemProfileExportService temProfileExportService) {
        this.temProfileExportService = temProfileExportService;
    }








}

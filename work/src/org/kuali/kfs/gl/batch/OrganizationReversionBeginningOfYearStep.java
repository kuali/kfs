/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.batch;

import org.kuali.core.batch.Step;
import org.kuali.module.gl.service.OrganizationReversionProcessService;

public class OrganizationReversionBeginningOfYearStep implements Step {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionEndOfYearStep.class);

    private OrganizationReversionProcessService organizationReversionProcessService;

    public boolean performStep() {
        LOG.debug("performStep() started");

        organizationReversionProcessService.organizationReversionProcessBeginningOfYear();
        return true;
    }

    public String getName() {
        return "Organization Reversion Process (Beginning of Year)";
    }

    public void setOrganizationReversionProcessService(OrganizationReversionProcessService organizationReversionProcessService) {
        this.organizationReversionProcessService = organizationReversionProcessService;
    }

}

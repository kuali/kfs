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
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.gl.service.OriginEntryGroupService;

public class ClearOldOriginEntryStep implements Step {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearOldOriginEntryStep.class);

    private OriginEntryGroupService originEntryGroupService;
    private KualiConfigurationService kualiConfigurationService;

    public boolean performStep() {
        LOG.debug("performStep() started");

        String daysStr = kualiConfigurationService.getApplicationParameterValue("glClearOldOriginEntryStep", "days");
        int days = Integer.parseInt(daysStr);

        originEntryGroupService.deleteOlderGroups(days);

        return true;
    }

    public String getName() {
        return "Clear old origin entry groups";
    }

    public void setOriginEntryGroupService(OriginEntryGroupService oes) {
        originEntryGroupService = oes;
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }
}

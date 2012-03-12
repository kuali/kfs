/*
 * Copyright 2007-2008 The Kuali Foundation
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


import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

@ConfigureContext
public class PurgeSessionDocumentsStepTest extends KualiTestBase {
    private Step purgeSessionDocumentsStep;
    private DateTimeService dateTimeService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        purgeSessionDocumentsStep = SpringContext.getBean(Step.class, "purgeSessionDocumentsStep");
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    public void testExecute() throws Exception {
        purgeSessionDocumentsStep.execute(PurgeSessionDocumentsStep.class.getName(), dateTimeService.getCurrentDate());
    }
}

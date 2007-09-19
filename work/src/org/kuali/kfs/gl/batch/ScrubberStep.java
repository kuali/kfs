/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.Date;

import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.service.ScrubberService;

public class ScrubberStep extends AbstractStep {
    private ScrubberService scrubberService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberStep.class);
    
    public boolean execute(String jobName) {
        long start = System.currentTimeMillis();
        scrubberService.scrubEntries();
        long end = System.currentTimeMillis();
        long total = end - start;
        LOG.fatal("Time elapsed " + (total/60000) + ":" + ((total % 60000) / 1000));
        LOG.fatal("After scrub commit" + System.currentTimeMillis());
        return true;
    }

    public void setScrubberService(ScrubberService ss) {
        scrubberService = ss;
    }
}

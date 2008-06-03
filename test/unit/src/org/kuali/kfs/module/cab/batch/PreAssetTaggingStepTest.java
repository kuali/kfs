/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.capitalAssetBuilder.batch;


import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;
import org.kuali.module.cams.bo.Pretag;
import org.kuali.module.capitalAssetBuilder.batch.PreAssetTaggingStep;

/**
 * Test the PreAssetTaggingSetp.
 */
@ConfigureContext
public class PreAssetTaggingStepTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreAssetTaggingStepTest.class);
    private PreAssetTaggingStep preAssetTaggingStep;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

//        preAssetTaggingStep = SpringContext.getBean(PreAssetTaggingStep.class);

    }

    /**
     * Tests the whole step completes successfully.
     */
    public void testExecute() throws Exception {
        
// This is time-consuming precess, which is only good to run locally
//     preAssetTaggingStep.execute(getClass().getName());
                
//     assertTrue("hold until figure out staging dir!", true);

    }
     
}

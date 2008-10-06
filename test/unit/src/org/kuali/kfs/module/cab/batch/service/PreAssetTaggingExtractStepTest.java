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
package org.kuali.kfs.module.cab.batch.service;

import java.util.Date;

import org.kuali.kfs.module.cab.batch.PreAssetTaggingExtractStep;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

public class PreAssetTaggingExtractStepTest extends BatchTestBase {
    private PreAssetTaggingExtractStep preAssetTaggingExtractStep;

    @Override
    @ConfigureContext(session = UserNameFixture.KHUNTLEY, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        preAssetTaggingExtractStep = SpringContext.getBean(PreAssetTaggingExtractStep.class);
    }

    public void testExecute() throws Exception {
        preAssetTaggingExtractStep.execute("testPreAssetTaggingExtractStep", new Date());
    }
}

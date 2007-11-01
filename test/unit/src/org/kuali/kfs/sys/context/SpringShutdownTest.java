/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.context;

import org.kuali.test.suite.TestSuiteBuilder;

/**
 * This class shuts down spring after unit testing
 */
@TestSuiteBuilder.Exclude
public class SpringShutdownTest extends KualiTestBase {
    // TODO this is a hack. sure there's a better way to keep from initializing and shutting down spring for each unit
    // test, but still shut spring down after they've all run
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        SpringContext.close();
    }

    public void testNothing() throws Exception {
    }
}
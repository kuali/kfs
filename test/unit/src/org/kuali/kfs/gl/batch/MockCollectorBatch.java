/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import org.kuali.kfs.sys.KualiTestConstants.TestConstants.Data1;

/**
 * Mock object for CollectorBatch. Used so the object does not need built from XML everytime we need it for a parameter.
 */
public class MockCollectorBatch extends CollectorBatch {

    /**
     * @see org.kuali.kfs.gl.batch.CollectorBatch#getChartOfAccountsCode()
     */
    @Override
    public String getChartOfAccountsCode() {
        return Data1.CHART_OF_ACCOUNTS_CODE;
    }

    /**
     * @see org.kuali.kfs.gl.batch.CollectorBatch#getOrganizationCode()
     */
    @Override
    public String getOrganizationCode() {
        return Data1.ORGANIZATION_CODE;
    }

}

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
package org.kuali.module.chart.service;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the Chart service.
 * 
 * 
 */
@ConfigureContext
public class ChartServiceTest extends KualiTestBase {

    public void testFindById() {
        Chart chart = SpringContext.getBean(ChartService.class).getByPrimaryId("UA");
        assertEquals("Chart Code should be UA", chart.getChartOfAccountsCode(), "UA");
    }
}

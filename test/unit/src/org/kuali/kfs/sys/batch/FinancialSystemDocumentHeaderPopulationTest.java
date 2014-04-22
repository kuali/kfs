/*
 * Copyright 2014 The Kuali Foundation.
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

import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.dataaccess.FinancialSystemDocumentHeaderPopulationDao;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeaderMissingFromWorkflow;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.PersistenceStructureService;

@ConfigureContext
public class FinancialSystemDocumentHeaderPopulationTest extends KualiTestBase {
    public void testMissingFromWorkflowExists() {
        final PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);
        final List<String> pkNames = persistenceStructureService.getPrimaryKeys(FinancialSystemDocumentHeaderMissingFromWorkflow.class);

        assertFalse(pkNames.isEmpty());
        assertEquals(1, pkNames.size());
        assertEquals("documentNumber", pkNames.get(0));
    }

    public void testCountFinancialSystemDocumentHeaders() {
        final FinancialSystemDocumentHeaderPopulationDao populationDao = SpringContext.getBean(FinancialSystemDocumentHeaderPopulationDao.class);
        final int size = populationDao.countTotalFinancialSystemDocumentHeadersToProcess();
        assertTrue(size > 0);
    }
}

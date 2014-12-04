/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

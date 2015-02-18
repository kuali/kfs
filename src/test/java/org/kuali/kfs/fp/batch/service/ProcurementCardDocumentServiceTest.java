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
package org.kuali.kfs.fp.batch.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the services used to create ProcurementCard documents.
 */
@ConfigureContext(session = kfs)
public class ProcurementCardDocumentServiceTest extends KualiTestBase {

    public void testCreatePCardDocuments() throws Exception {
        boolean documentsCreated = SpringContext.getBean(ProcurementCardCreateDocumentService.class).createProcurementCardDocuments();
        assertTrue("problem creating documents", documentsCreated);
    }

    public void testRoutePCardDocuments() throws Exception {
        boolean routeSuccessful = SpringContext.getBean(ProcurementCardCreateDocumentService.class).routeProcurementCardDocuments();
    }

}


/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document;


import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.cg.businessobject.AgencyAddressType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MaintenanceDocumentTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class is used to test the AgencyAddressType MaintenanceDocument
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class AgencyAddressTypeTest extends KualiTestBase {

    public MaintenanceDocument document;
    public DocumentService documentService;
    public static final String AGENCY_ADDRESS_TYPE_CODE = "A";
    public static final String AGENCY_ADDRESS_TYPE_DESCRIPTION = "Alternate";
    public static final boolean ACTIVE = true;
    public static final Class<MaintenanceDocument> DOCUMENT_CLASS = MaintenanceDocument.class;
    private AgencyAddressType agencyAddressType;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument("AATY");
        document.getDocumentHeader().setDocumentDescription("Test Document");
        documentService = SpringContext.getBean(DocumentService.class);
        agencyAddressType = new AgencyAddressType();
        agencyAddressType.setAgencyAddressTypeCode(AGENCY_ADDRESS_TYPE_CODE);
        agencyAddressType.setAgencyAddressTypeDescription(AGENCY_ADDRESS_TYPE_DESCRIPTION);
        agencyAddressType.setActive(ACTIVE);
        agencyAddressType.setVersionNumber(new Long(1));
        document.getNewMaintainableObject().setBusinessObject(agencyAddressType);
        document.getNewMaintainableObject().setBoClass(agencyAddressType.getClass());

    }


    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testSaveDocument() throws Exception {
        MaintenanceDocumentTestUtils.testSaveDocument(document, documentService);
    }

    public void testGetNewDocument() throws Exception {
        Document document = documentService.getNewDocument("AATY");
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }


}

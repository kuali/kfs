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
package org.kuali.kfs.module.ar.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.document.MaintenanceDocumentTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests DunningLetterTemplate Maintenance Document
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class DunningLetterTemplatesTest extends KualiTestBase {
    public MaintenanceDocument document;
    public DocumentService documentService;
    public static final String DUNNING_LETTER_TEMPLATE = "TEST";
    public static final String DUNNING_LETTER_DESCRIPTION = "Testing Description";
    public static final boolean ACCESS_RESTRICTED = true;
    public static final boolean ACTIVE = true;
    public static final Class<MaintenanceDocument> DOCUMENT_CLASS = MaintenanceDocument.class;
    private DunningLetterTemplate dunningLetterTemplate;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument("DLTS");
        document.getDocumentHeader().setDocumentDescription("Test Document");
        documentService = SpringContext.getBean(DocumentService.class);
        dunningLetterTemplate = new DunningLetterTemplate();
        
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        String billByChartOfAccountCode = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(currentUser, KFSConstants.ParameterNamespaces.CHART).getChartOfAccountsCode();
        String billedByOrganizationCode = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(currentUser, KFSConstants.ParameterNamespaces.CHART).getOrganizationCode();
        
        dunningLetterTemplate.setBillByChartOfAccountCode(billByChartOfAccountCode);
        dunningLetterTemplate.setBilledByOrganizationCode(billedByOrganizationCode);
        
        dunningLetterTemplate.setLetterTemplateCode(DUNNING_LETTER_TEMPLATE);
        dunningLetterTemplate.setLetterTemplateDescription(DUNNING_LETTER_DESCRIPTION);
        dunningLetterTemplate.setActive(ACTIVE);
        dunningLetterTemplate.setAccessRestricted(ACCESS_RESTRICTED);
        
        document.getNewMaintainableObject().setBusinessObject(dunningLetterTemplate);
        document.getNewMaintainableObject().setBoClass(dunningLetterTemplate.getClass());
    }

    /**
     * This method calls testSaveDocument method.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testSaveDocument() throws Exception {
        MaintenanceDocumentTestUtils.testSaveDocument(document, documentService);
    }

    /**
     * This method tests getNewDocument() method on Dunning Letter Templates maintenance document.
     * 
     * @throws Exception
     */
    public void testGetNewDocument() throws Exception {
        Document document = (Document) documentService.getNewDocument("DLTS");
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }
}

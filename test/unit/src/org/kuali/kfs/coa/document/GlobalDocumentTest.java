/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.globals;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UnknownDocumentTypeException;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountChangeDocument;
import org.kuali.module.chart.bo.DelegateChangeDocument;
import org.kuali.test.KualiTestBaseWithFixtures;

public class GlobalDocumentTest extends KualiTestBaseWithFixtures {
    
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiDelegateChangeDocument";
    private static final String GLOBAL_DELEGATE_TYPENAME = "KualiDelegateChangeDocument";
    private static final String GLOBAL_ACCOUNT_TYPENAME = "KualiAccountChangeDocument";
    
    private DocumentService docService;
    
    public GlobalDocumentTest() {
        super();
    }
    
    public void setUp() throws Exception {
        super.setUp();
        docService = SpringServiceLocator.getDocumentService();
    }
    
    public void testGlobalDelegateMaintenanceDocumentCreation_goodDocTypeName() throws Exception {
        MaintenanceDocument doc = (MaintenanceDocument) docService.getNewDocument(KNOWN_DOCUMENT_TYPENAME);
        assertNotNull(doc);
        assertNotNull(doc.getNewMaintainableObject());
        assertEquals("org.kuali.module.chart.bo.DelegateChangeDocument", doc.getNewMaintainableObject().getBoClass().getName());
    }

    public final void testGetNewDocument_globalDelegateMaintDoc() throws Exception {
        
        MaintenanceDocument document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        
        //  make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getFinancialDocumentNumber());
        
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());
        
        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeDocument.", DelegateChangeDocument.class, newMaintainable.getBoClass());

        BusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", DelegateChangeDocument.class, newBo.getClass());
        
    }
    
    public final void testGetNewDocument_globalAccountMaintDoc() throws Exception {
        
        boolean exceptionThrown = false;
        MaintenanceDocument document = null;
        try {
            document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_ACCOUNT_TYPENAME);
        }
        catch (UnknownDocumentTypeException e) {
            exceptionThrown = true;
        }
        
        //  make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getFinancialDocumentNumber());
        
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());
        
        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be AccountChangeDocument.", AccountChangeDocument.class, newMaintainable.getBoClass());

        BusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", AccountChangeDocument.class, newBo.getClass());
//        
    }
    
}

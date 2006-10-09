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
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.ProcurementCardTransaction;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the services used to create ProcurementCard documents.
 * 
 * @author Kuali Financial Transactions ()
 */
@WithTestSpringContext
public class ProcurementCardDocumentServiceTest extends KualiTestBase {
    private ProcurementCardCreateDocumentService procurementCardCreateDocumentService;
    private ProcurementCardLoadTransactionsService procurementCardLoadTransactionsService;
    private static String PCDO_USER_NAME = KualiUser.SYSTEM_USER;

    private static List documentsCreated;

    protected void setUp() throws Exception {
        super.setUp();
        // TODO fix this
        //changeCurrentUser(PCDO_USER_NAME);
        procurementCardCreateDocumentService = SpringServiceLocator.getProcurementCardCreateDocumentService();
        procurementCardLoadTransactionsService = SpringServiceLocator.getProcurementCardLoadTransactionsService();
    }

    /**
     * Tests that the service is parsing the kuali xml files and loading into the transaction table correctly.
     * 
     * @throws Exception
     */
    public void testLoadKualiPCardFiles() throws Exception {
        // todo: load test files with known contents?
        boolean loadSuccessful = procurementCardLoadTransactionsService.loadProcurementCardDataFile();

        // load transactions
        List loadedTransactions = (List) SpringServiceLocator.getBusinessObjectService().findAll(ProcurementCardTransaction.class);
        assertNotNull(loadedTransactions);
        assertEquals("Incorrect number of rows loaded ", 4, loadedTransactions.size());
    }

    public void testCreatePCardDocuments() throws Exception {
        boolean documentsCreated = procurementCardCreateDocumentService.createProcurementCardDocuments();
    }

    // public void testRoutePCardDocuments() throws Exception {
    // boolean routeSuccessful = procurementCardCreateDocumentService.routeProcurementCardDocuments();
    // }

}
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
package org.kuali.kfs.module.tem.document.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class AccountingDocumentRelationshipServiceTest extends KualiTestBase {

    private AccountingDocumentRelationshipService adrService;
    private static final String TEST_PREFIX = "test";
    private static final Logger LOG = Logger.getLogger(AccountingDocumentRelationshipServiceTest.class);

    /**
     * @see junit.framework.TestCase#setUp() Setup 5 doc relationships (test1-5).
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();

        adrService = SpringContext.getBean(AccountingDocumentRelationshipService.class);

        List<AccountingDocumentRelationship> adrList = new ArrayList<AccountingDocumentRelationship>();

        int relatedDocNumbers = 5;
        for (int i = 1; i < relatedDocNumbers; i++) {
            adrList.add(new AccountingDocumentRelationship(TEST_PREFIX + i, TEST_PREFIX + (i + 1)));
        }
        adrList.add(new AccountingDocumentRelationship(TEST_PREFIX + 1, TEST_PREFIX + 7));

        adrService.save(adrList);
    }

    @After
    public void tearDown() throws Exception {
        adrService = null;
        super.tearDown();
    }

    /**
     * This method tests {@link AccountingDocumentRelationshipService#getAllRelatedDocumentNumbers(String)}.
     */
    @Test
    public void testGetAllRelatedDocumentNumbers() {
        Set<String> docNumbers = adrService.getAllRelatedDocumentNumbers(TEST_PREFIX + 3);
        LOG.info("docNumbers.toString(): " + docNumbers.toString() + " (" + docNumbers.size() + ")");   
        assertTrue(docNumbers.contains(TEST_PREFIX + 1));
        assertTrue(docNumbers.contains(TEST_PREFIX + 2));
        assertFalse(docNumbers.contains(TEST_PREFIX + 3));
        assertTrue(docNumbers.contains(TEST_PREFIX + 4));
        assertTrue(docNumbers.contains(TEST_PREFIX + 5));
    }
    
    /**
     * This method tests {@link AccountingDocumentRelationshipService#getRelatedDocumentNumbers(String)}.
     */
    @Test
    public void testGetRelatedDocumentNumbers() {
        Set<String> docNumbers = adrService.getRelatedDocumentNumbers(TEST_PREFIX + 3);
        LOG.info("docNumbers.toString(): " + docNumbers.toString() + " (" + docNumbers.size() + ")");
        assertTrue(docNumbers.contains(TEST_PREFIX + 2));
        assertFalse(docNumbers.contains(TEST_PREFIX + 3));
        assertTrue(docNumbers.contains(TEST_PREFIX + 4));
    }    
    
    /**
     * This method tests {@link AccountingDocumentRelationshipService#getRootDocumentNumber(String)}.
     */
    @Test
    public void testGetRootDocumentNumber() {
        assertTrue((TEST_PREFIX + 1).equals(adrService.getRootDocumentNumber(TEST_PREFIX + 3)));
    }    
}

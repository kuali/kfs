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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in CollectionActivityDocumentRule
 */
@ConfigureContext(session = khuntley)
public class CollectionActivityDocumentRuleTest extends KualiTestBase {

    private DocumentService documentService;
    private CollectionActivityDocumentRule collectionActivityDocumentRule;
    private CollectionActivityDocument collectionActivityDocument;

    private final static String ACTIVITY_CODE = "LSTS";

    /**
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        collectionActivityDocumentRule = new CollectionActivityDocumentRule();
        collectionActivityDocument = createCollectionActivityDocument();
    }

    /**
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        documentService = null;
        collectionActivityDocumentRule = null;
        collectionActivityDocument = null;
    }

    /**
     * Creates the collection activity document.
     *
     * @return Returns new document of collection activity.
     * @throws WorkflowException
     */
    private CollectionActivityDocument createCollectionActivityDocument() throws WorkflowException {
        CollectionActivityDocument collectionActivityDocument = (CollectionActivityDocument) documentService.getNewDocument(CollectionActivityDocument.class);
        collectionActivityDocument.getDocumentHeader().setDocumentDescription("Testing document");

        return collectionActivityDocument;
    }

    /**
     * Tests the validateEvent() method of service and returns true when all rules passed.
     */
    public void testValidateEvent_True() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        boolean result = collectionActivityDocumentRule.validateCollectionEvent(event);
        assertTrue(result);
    }

    /**
     * Tests the validateEvent() method of service and returns false when any rule is not passed.
     */
    public void testValidateEvent_False() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));
        event.setFollowup(Boolean.TRUE);

        boolean result = collectionActivityDocumentRule.validateCollectionEvent(event);
        assertFalse(result);
    }

    /**
     * Tests the validateEvents() method of service and returns true when all rules passed.
     */
    public void testValidateEvents_True() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        collectionActivityDocument.getCollectionEvents().add(event);
        boolean result = collectionActivityDocumentRule.validateCollectionEvents(collectionActivityDocument);
        assertTrue(result);
    }

    /**
     * Tests the validateEvents() method of service and returns false when any rule is not passed.
     */
    public void testValidateEvents_False() {
        CollectionEvent event = new CollectionEvent();
        collectionActivityDocument.getCollectionEvents().add(event);
        boolean result = collectionActivityDocumentRule.validateCollectionEvents(collectionActivityDocument);
        assertFalse(result);
    }

    /**
     * Tests the processAddEventBusinessRules() method of service and returns true when all rules are passed.
     */
    public void testProcessAddEventBusinessRules_True() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        collectionActivityDocument.getCollectionEvents().add(event);
        boolean result = collectionActivityDocumentRule.processAddCollectionEventBusinessRules(collectionActivityDocument, event);
        assertTrue(result);
    }

    /**
     * Tests the processAddEventBusinessRules() method of service and returns false when any rule is not passed.
     */
    public void testProcessAddEventBusinessRules_False() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));
        event.setCompleted(Boolean.TRUE);

        collectionActivityDocument.getCollectionEvents().add(event);
        boolean result = collectionActivityDocumentRule.processAddCollectionEventBusinessRules(collectionActivityDocument, event);
        assertFalse(result);
    }
}

/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.Event;
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

    private final static String ACTIVITY_CODE = "LTSS";

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
        Event event = new Event();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        boolean result = collectionActivityDocumentRule.validateEvent(event);
        assertTrue(result);
    }

    /**
     * Tests the validateEvent() method of service and returns false when any rule is not passed.
     */
    public void testValidateEvent_False() {
        Event event = new Event();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));
        event.setFollowupInd(Boolean.TRUE);

        boolean result = collectionActivityDocumentRule.validateEvent(event);
        assertFalse(result);
    }

    /**
     * Tests the validateEvents() method of service and returns true when all rules passed.
     */
    public void testValidateEvents_True() {
        Event event = new Event();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        collectionActivityDocument.getEvents().add(event);
        boolean result = collectionActivityDocumentRule.validateEvents(collectionActivityDocument);
        assertTrue(result);
    }

    /**
     * Tests the validateEvents() method of service and returns false when any rule is not passed.
     */
    public void testValidateEvents_False() {
        Event event = new Event();
        collectionActivityDocument.getEvents().add(event);
        boolean result = collectionActivityDocumentRule.validateEvents(collectionActivityDocument);
        assertFalse(result);
    }

    /**
     * Tests the processAddEventBusinessRules() method of service and returns true when all rules are passed.
     */
    public void testProcessAddEventBusinessRules_True() {
        Event event = new Event();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        collectionActivityDocument.getEvents().add(event);
        boolean result = collectionActivityDocumentRule.processAddCollectionActivityDocumentEventBusinessRules(collectionActivityDocument, event);
        assertTrue(result);
    }

    /**
     * Tests the processAddEventBusinessRules() method of service and returns false when any rule is not passed.
     */
    public void testProcessAddEventBusinessRules_False() {
        Event event = new Event();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));
        event.setCompletedInd(Boolean.TRUE);

        collectionActivityDocument.getEvents().add(event);
        boolean result = collectionActivityDocumentRule.processAddCollectionActivityDocumentEventBusinessRules(collectionActivityDocument, event);
        assertFalse(result);
    }
}

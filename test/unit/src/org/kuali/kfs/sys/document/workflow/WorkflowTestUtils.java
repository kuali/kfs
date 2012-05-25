/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.document.workflow;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.monitor.ChangeMonitor;
import org.kuali.kfs.sys.monitor.DocumentWorkflowNodeMonitor;
import org.kuali.kfs.sys.monitor.DocumentWorkflowRequestMonitor;
import org.kuali.kfs.sys.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public class WorkflowTestUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowTestUtils.class);
    protected static final int INITIAL_PAUSE_SECONDS = 5;
    protected static final int MAX_WAIT_SECONDS = 60;

    public static boolean isAtNode(Document document, String nodeName) throws WorkflowException {
        return isAtNode(document.getDocumentHeader().getWorkflowDocument(), nodeName);
    }

    public static boolean isAtNode(WorkflowDocument workflowDocument, String nodeName) throws WorkflowException {
        Set<String> nodeNames = workflowDocument.getNodeNames();
        for (Iterator<String> iterator = nodeNames.iterator(); iterator.hasNext();) {
            String nodeNamesNode = iterator.next();
            if (nodeName.equals(nodeNamesNode)) {
                return true;
            }
        }
        return false;
    }

    public static void waitForNodeChange(String documentNumber, String desiredNodeName) throws Exception {
        LOG.info("Entering: waitForNodeChange(" + documentNumber + "," + desiredNodeName + ")");
        DocumentWorkflowNodeMonitor monitor = new DocumentWorkflowNodeMonitor(documentNumber, desiredNodeName);
        boolean success = ChangeMonitor.waitUntilChange(monitor, MAX_WAIT_SECONDS, INITIAL_PAUSE_SECONDS);
        if ( !success ) {
            WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(documentNumber, UserNameFixture.kfs.getPerson() );
            Assert.fail( "waitForNodeChange(" + documentNumber + "," + desiredNodeName + ") timed out. Document was at the " + document.getCurrentNodeNames() + " node.\n" + document.getRequestedActions() );
        }
    }

    public static void waitForNodeChange(WorkflowDocument document, String desiredNodeName) throws Exception {
        LOG.info("Entering: waitForNodeChange(" + document.getDocumentId() + "," + desiredNodeName + ")");
        DocumentWorkflowNodeMonitor monitor = new DocumentWorkflowNodeMonitor(document.getDocumentId(), desiredNodeName);
        boolean success = ChangeMonitor.waitUntilChange(monitor, MAX_WAIT_SECONDS, INITIAL_PAUSE_SECONDS);
        if ( !success ) {
            document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(document.getDocumentId(), UserNameFixture.kfs.getPerson() );
            Assert.fail( "waitForNodeChange(" + document.getDocumentId() + "," + desiredNodeName + ") timed out. Document was at the " + document.getCurrentNodeNames() + " node.\n" + document.getRequestedActions() );
        }
    }

    public static void waitForDocumentApproval( String documentNumber ) {
        waitForStatusChange(documentNumber, DocumentStatus.PROCESSED, DocumentStatus.FINAL );
    }

    public static void waitForStatusChange( String documentNumber, DocumentStatus... desiredStatuses ) {
        try {
            LOG.info("Entering: waitForStatusChange(" + MAX_WAIT_SECONDS + "," + documentNumber + "," + Arrays.toString(desiredStatuses) + ")");
            DocumentWorkflowStatusMonitor monitor = new DocumentWorkflowStatusMonitor(documentNumber, desiredStatuses);
            if ( !ChangeMonitor.waitUntilChange(monitor, MAX_WAIT_SECONDS, INITIAL_PAUSE_SECONDS) ) {
                WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(documentNumber, UserNameFixture.kfs.getPerson() );
                Assert.fail( "waitForStatusChange(" + documentNumber + "," + Arrays.toString(desiredStatuses) + ") timed out. Document was in " + document.getStatus() + " state.\n" + document.getCurrentNodeNames() + "\n" + document.getRequestedActions() );
            }
        } catch (Exception ex) {
            LOG.error("An exception was thrown while checking workflow status on document " + documentNumber + ", unable to continue.", ex );
            Assert.fail( "An exception was thrown while checking workflow status on document " + documentNumber + ", unable to continue." + ex.getClass() + " : " + ex.getMessage() );
        }
    }

    public static void waitForApproveRequest(String documentNumber, Person user) throws Exception {
        LOG.info("Entering: waitForApproveRequest(" + documentNumber + "," + user.getPrincipalName() + ")");
        DocumentWorkflowRequestMonitor monitor = new DocumentWorkflowRequestMonitor(documentNumber, user, ActionRequestType.APPROVE);
        boolean success = ChangeMonitor.waitUntilChange(monitor, MAX_WAIT_SECONDS, INITIAL_PAUSE_SECONDS);
        if ( !success ) {
            WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(documentNumber, UserNameFixture.kfs.getPerson() );
            Assert.fail( "waitForApproveRequest(" + documentNumber + "," + user.getPrincipalName() + ") timed out. Document was in " + document.getStatus() + " state.\n" + document.getCurrentNodeNames() + "\n" + document.getRequestedActions() );
        }
    }

}


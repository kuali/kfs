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

import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.monitor.ChangeMonitor;
import org.kuali.kfs.sys.monitor.DocumentWorkflowNodeMonitor;
import org.kuali.kfs.sys.monitor.DocumentWorkflowRequestMonitor;
import org.kuali.kfs.sys.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

public class WorkflowTestUtils {
    protected static final int INITIAL_PAUSE_SECONDS = 5;
    protected static final int MAX_WAIT_SECONDS = 30;
    private static final Log LOG = LogFactory.getLog(WorkflowTestUtils.class);

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

    public static void waitForNodeChange(WorkflowDocument document, String desiredNodeName) throws Exception {
        LOG.info("Entering: waitForNodeChange(" + document.getDocumentId() + "," + desiredNodeName + ")");
        DocumentWorkflowNodeMonitor monitor = new DocumentWorkflowNodeMonitor(document, desiredNodeName);
        Assert.assertTrue("waitForNodeChange(" + document.getDocumentId() + "," + desiredNodeName + ") timed out", ChangeMonitor.waitUntilChange(monitor, MAX_WAIT_SECONDS, INITIAL_PAUSE_SECONDS));
    }

    public static void waitForStatusChange(WorkflowDocument document, DocumentStatus desiredStatus) throws Exception {
        waitForStatusChange(MAX_WAIT_SECONDS, document, desiredStatus);
    }

    public static void waitForStatusChange(int numSeconds, WorkflowDocument document, DocumentStatus desiredStatus) throws Exception {
        LOG.info("Entering: waitForStatusChange(" + numSeconds + "," + document.getDocumentId() + "," + desiredStatus + ")");
        DocumentWorkflowStatusMonitor monitor = new DocumentWorkflowStatusMonitor(SpringContext.getBean(DocumentService.class), "" + document.getDocumentId(), desiredStatus);
        Assert.assertTrue("waitForStatusChange(" + numSeconds + "," + document.getDocumentId() + "," + desiredStatus + ") timed out", ChangeMonitor.waitUntilChange(monitor, numSeconds, INITIAL_PAUSE_SECONDS));
    }

    public static void waitForStatusChange(int numSeconds, WorkflowDocument document, DocumentStatus[] desiredStatuses) throws Exception {
        LOG.info("Entering: waitForStatusChange(" + numSeconds + "," + document.getDocumentId() + "," + desiredStatuses + ")");
        DocumentWorkflowStatusMonitor monitor = new DocumentWorkflowStatusMonitor(SpringContext.getBean(DocumentService.class), "" + document.getDocumentId(), desiredStatuses);
        Assert.assertTrue("waitForStatusChange(" + numSeconds + "," + document.getDocumentId() + "," + desiredStatuses + ") timed out", ChangeMonitor.waitUntilChange(monitor, numSeconds, INITIAL_PAUSE_SECONDS));
    }

    public static void waitForApproveRequest(String docHeaderId, Person user) throws Exception {
        LOG.info("Entering: waitForApproveRequest(" + docHeaderId + "," + user.getPrincipalName() + ")");
        DocumentWorkflowRequestMonitor monitor = new DocumentWorkflowRequestMonitor(docHeaderId, user, ActionRequestType.APPROVE);
        Assert.assertTrue("waitForApproveRequest(" + docHeaderId + "," + user.getPrincipalName() + ") timed out", ChangeMonitor.waitUntilChange(monitor, MAX_WAIT_SECONDS, INITIAL_PAUSE_SECONDS));
    }

}


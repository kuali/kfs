/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.workflow;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentWorkflowNodeMonitor;
import org.kuali.test.monitor.DocumentWorkflowRequestMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

public class WorkflowTestUtils {
    private static final Log LOG = LogFactory.getLog(WorkflowTestUtils.class);

    public static boolean isAtNode(Document document, String nodeName) throws WorkflowException {
        String[] nodeNames = document.getDocumentHeader().getWorkflowDocument().getNodeNames();
        for (int index = 0; index < nodeNames.length; index++) {
            if (nodeName.equals(nodeNames[index])) {
                return true;
            }
        }
        return false;
    }

    public static void waitForNodeChange(KualiWorkflowDocument document, String desiredNodeName) throws Exception {
        LOG.info("Entering: waitForNodeChange(" + document.getRouteHeaderId() + "," + desiredNodeName + ")");
        DocumentWorkflowNodeMonitor monitor = new DocumentWorkflowNodeMonitor(document, desiredNodeName);
        Assert.assertTrue("waitForNodeChange(" + document.getRouteHeaderId() + "," + desiredNodeName + ") timed out", ChangeMonitor.waitUntilChange(monitor, 240, 5));
    }

    public static void waitForStatusChange(KualiWorkflowDocument document, String desiredStatus) throws Exception {
        waitForStatusChange(240, document, desiredStatus);
    }

    public static void waitForStatusChange(int numSeconds, KualiWorkflowDocument document, String desiredStatus) throws Exception {
        LOG.info("Entering: waitForStatusChange(" + numSeconds + "," + document.getRouteHeaderId() + "," + desiredStatus + ")");
        DocumentWorkflowStatusMonitor monitor = new DocumentWorkflowStatusMonitor(SpringContext.getBean(DocumentService.class), "" + document.getRouteHeaderId(), desiredStatus);
        Assert.assertTrue("waitForStatusChange(" + numSeconds + "," + document.getRouteHeaderId() + "," + desiredStatus + ") timed out", ChangeMonitor.waitUntilChange(monitor, numSeconds, 5));
    }

    public static void waitForStatusChange(int numSeconds, KualiWorkflowDocument document, String[] desiredStatuses) throws Exception {
        LOG.info("Entering: waitForStatusChange(" + numSeconds + "," + document.getRouteHeaderId() + "," + desiredStatuses + ")");
        DocumentWorkflowStatusMonitor monitor = new DocumentWorkflowStatusMonitor(SpringContext.getBean(DocumentService.class), "" + document.getRouteHeaderId(), desiredStatuses);
        Assert.assertTrue("waitForStatusChange(" + numSeconds + "," + document.getRouteHeaderId() + "," + desiredStatuses + ") timed out", ChangeMonitor.waitUntilChange(monitor, numSeconds, 5));
    }

    public static void waitForApproveRequest(Long docHeaderId, UniversalUser user) throws Exception {
        LOG.info("Entering: waitForApproveRequest(" + docHeaderId + "," + user.getPersonUserIdentifier() + ")");
        DocumentWorkflowRequestMonitor monitor = new DocumentWorkflowRequestMonitor(docHeaderId, user, EdenConstants.ACTION_REQUEST_APPROVE_REQ);
        Assert.assertTrue("waitForApproveRequest(" + docHeaderId + "," + user.getPersonUserIdentifier() + ") timed out", ChangeMonitor.waitUntilChange(monitor, 240, 5));
    }

}

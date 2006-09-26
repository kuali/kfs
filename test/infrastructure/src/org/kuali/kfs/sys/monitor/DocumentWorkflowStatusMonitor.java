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
package org.kuali.test.monitor;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;

/**
 * DocumentWorkflowStatusMonitor
 * 
 * @author Kuali Nervous System Team ()
 */
public class DocumentWorkflowStatusMonitor extends ChangeMonitor {
    final DocumentService documentService;
    final private String docHeaderId;
    final private String[] desiredWorkflowStates;

    public DocumentWorkflowStatusMonitor(DocumentService documentService, String docHeaderId, String desiredWorkflowStatus) {
        this.documentService = documentService;
        this.docHeaderId = docHeaderId;
        this.desiredWorkflowStates = new String[] { desiredWorkflowStatus };
    }

    public DocumentWorkflowStatusMonitor(DocumentService documentService, String docHeaderId, String[] desiredWorkflowStates) {
        this.documentService = documentService;
        this.docHeaderId = docHeaderId;
        this.desiredWorkflowStates = desiredWorkflowStates;
    }

    public boolean valueChanged() throws Exception {
        Document d = documentService.getByDocumentHeaderId(docHeaderId.toString());

        String currentStatus = d.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();

        for (int i = 0; i < desiredWorkflowStates.length; i++) {
            if (StringUtils.equals(desiredWorkflowStates[i], currentStatus)) {
                return true;
            }
        }
        return false;
    }
}
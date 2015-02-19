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
package org.kuali.kfs.sys.monitor;

import java.util.Iterator;
import java.util.Set;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Watches the workflow document and indicates valueChanged when either the status or the current node changes.
 */
public class DocumentWorkflowNodeMonitor extends ChangeMonitor {

    protected String documentNumber;
    protected String desiredNodeName;

    public DocumentWorkflowNodeMonitor(String documentNumber, String desiredNodeName) throws WorkflowException {
        this.documentNumber = documentNumber;
        this.desiredNodeName = desiredNodeName;
    }

    public boolean valueChanged() throws Exception {
        WorkflowDocument document = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(documentNumber, UserNameFixture.kfs.getPerson());
        return WorkflowTestUtils.isAtNode(document, desiredNodeName);
    }

}


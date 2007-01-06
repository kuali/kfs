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
package org.kuali.module.purap.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.purap.document.AssignContractManagerDocument;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * This class is the form class for the assign a contract manager.
 * 
 */
public class AssignContractManagerForm extends KualiTransactionalDocumentFormBase {

    // TODO: the list does not appear to need to be here. Delete it and getter and setter when confirmed.
//    List unassignedRequisitions;
    
    /**
     * Constructs a AssignContractManagerForm instance 
     */
    public AssignContractManagerForm() {
        super();
        setDocument(new AssignContractManagerDocument());
//        unassignedRequisitions = new ArrayList();
    }

//    /**
//     * @return Returns the assignContractManagerDocument.
//     */
//    public AssignContractManagerDocument getAssignContractManagerDocument() {
//        return (AssignContractManagerDocument) getDocument();
//    }
//
//    /**
//     * @param assignContractManagerDocument The assignContractManagerDocument to set.
//     */
//    public void setAssignContractManagerDocument(AssignContractManagerDocument assignContractManagerDocument) {
//        setDocument(assignContractManagerDocument);
//    }

//    public List getUnassignedRequisitions() {
//        return unassignedRequisitions;
//    }
//
//    public void setUnassignedRequisitions(List unassignedRequisitions) {
//        this.unassignedRequisitions = unassignedRequisitions;
//    }

}
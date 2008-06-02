/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;

public class EquipmentLoanOrReturnForm extends KualiTransactionalDocumentFormBase {

    private EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument;

    /**
     * Constructs a EquipmentLoanOrReturnForm.java.
     */
    public EquipmentLoanOrReturnForm() {
        super();
        this.equipmentLoanOrReturnDocument = new EquipmentLoanOrReturnDocument();
        setDocument(this.equipmentLoanOrReturnDocument);
        // If this is not done, when document description error is there, message comes back with read-only mode
        Map<String, String> editModeMap = new HashMap<String, String>();
        editModeMap.put(AuthorizationConstants.EditMode.FULL_ENTRY, "TRUE");
        setEditingMode(editModeMap);
    }

    /**
     * This method gets the equipmentLoanOrReturn document
     * 
     * @return EquipmentLoanOrReturnDocument
     */
    public EquipmentLoanOrReturnDocument getEquipmentLoanOrReturnDocument() {
        return this.equipmentLoanOrReturnDocument;
    }

    /**
     * This method sets the equipmentLoanOrReturnDocument selected
     * 
     * @param equipmentLoanOrReturnDocument
     */
    public void setEquipmentLoanOrReturnDocument(EquipmentLoanOrReturnDocument document) {
        setDocument(document);
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getEquipmentLoanOrReturnDocument());
    }

}

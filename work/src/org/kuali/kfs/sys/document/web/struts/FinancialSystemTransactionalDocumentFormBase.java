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
package org.kuali.kfs.sys.document.web.struts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This class is a Financial System specific transactional document form base
 */
public class FinancialSystemTransactionalDocumentFormBase extends KualiTransactionalDocumentFormBase {

    /**
     * Constructs a FinancialSystemTransactionalDocumentFormBase.java.
     */
    public FinancialSystemTransactionalDocumentFormBase() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populateHeaderFields(org.kuali.rice.kns.workflow.service.KualiWorkflowDocument)
     */
    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (getDocument().getDocumentHeader() instanceof FinancialSystemDocumentHeader) {
            FinancialSystemDocumentHeader documentHeader = (FinancialSystemDocumentHeader)getDocument().getDocumentHeader();
            if (StringUtils.isNotBlank(documentHeader.getFinancialDocumentInErrorNumber())) {
                extendDocInfoToThreeColumns();
                int insertIndex = 2;
                getDocInfo().remove(insertIndex);
                getDocInfo().add(insertIndex, new HeaderField("DataDictionary.FinancialSystemDocumentHeader.attributes.financialDocumentInErrorNumber", documentHeader.getFinancialDocumentInErrorNumber()));
            }
            if (StringUtils.isNotBlank(documentHeader.getCorrectedByDocumentId())) {
                extendDocInfoToThreeColumns();
                int insertIndex = getNumColumns() + 2;
                getDocInfo().remove(insertIndex);
                getDocInfo().add(insertIndex, new HeaderField("DataDictionary.FinancialSystemDocumentHeader.attributes.correctedByDocumentId", documentHeader.getCorrectedByDocumentId()));
            }
        }
    }
    
    /**
     * Extends the DocInfo on the form to 3 columns if it currently has less than 3 columns.
     * If it has exactly 3 or more columns, no action will be taken.
     */
    protected void extendDocInfoToThreeColumns() {
        List<HeaderField> newDocInfo = new ArrayList<HeaderField>();
        int currentColumns = getNumColumns();
        int targetColumns = 3;
        if (getNumColumns() < targetColumns) {
            int column = 0;
            for (HeaderField headerField : getDocInfo()) {
                if (column + 1 > currentColumns) {
                    newDocInfo.add(HeaderField.EMPTY_FIELD);
                    column = (column + 1) % targetColumns;
                }
                newDocInfo.add(headerField);
                column = (column + 1) % targetColumns;
            }
            // fill out the final row with empty columns
            while (newDocInfo.size() % targetColumns != 0) {
                newDocInfo.add(HeaderField.EMPTY_FIELD);
            }
            setDocInfo(newDocInfo);
            setNumColumns(3);
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = super.getExtraButtons();
        DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        final Set<String> documentActionsFromPresentationController = documentHelperService.getDocumentPresentationController(getDocument())
                .getDocumentActions(getDocument());
        final Set<String> documentActionsFromAuthorizer = documentHelperService.getDocumentAuthorizer(getDocument())
                .getDocumentActions(getDocument(), GlobalVariables.getUserSession().getPerson(), documentActionsFromPresentationController);
        if (documentActionsFromAuthorizer.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) {
            buttons.add(generateErrorCorrectionButton());
        }
        return buttons;
    }
    
    /**
     * Generates an ExtraButton which represents the error correction button
     * @return an ExtraButton representing an ErrorCorrection button
     */
    protected ExtraButton generateErrorCorrectionButton() {
        ExtraButton button = new ExtraButton();
        button.setExtraButtonAltText("Create error correction document from current document");
        button.setExtraButtonProperty("methodToCall.correct");
        button.setExtraButtonSource(SpringContext.getBean(KualiConfigurationService.class).getPropertyString("kr.externalizable.images.url")+"buttonsmall_errcorr.gif");
        return button;
    }
}

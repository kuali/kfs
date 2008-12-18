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
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentTypeService;
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
        setDocumentActionFlags(new FinancialSystemTransactionalDocumentActionFlags());
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
        final FinancialSystemTransactionalDocumentEntry documentEntry = (FinancialSystemTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeNameByClass(getDocument().getClass()));
        final Set<String> documentActionsFromPresentationController = createPresentationControllerInstance(documentEntry).getDocumentActions(getDocument());
        final Set<String> documentActionsFromAuthorizer = createAuthorizerInstance(documentEntry).getDocumentActions(getDocument(), GlobalVariables.getUserSession().getPerson(), documentActionsFromPresentationController);
        if (documentActionsFromAuthorizer.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) {
            buttons.add(generateErrorCorrectionButton());
        }
        return buttons;
    }    

    /**
     * Creates a new instance of the presentation controller which fits this document
     * @param documentEntry the data dictionary entry for this document
     * @return a new instance of the presentation controller
     */
    protected FinancialSystemTransactionalDocumentPresentationController createPresentationControllerInstance(FinancialSystemTransactionalDocumentEntry documentEntry) {
        try {
            return (FinancialSystemTransactionalDocumentPresentationController)documentEntry.getDocumentPresentationControllerClass().newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Couldn't create new instance of presentation controller for class: "+this.getClass(),ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Couldn't create new instance of presentation controller for class: "+this.getClass(),iae);
        }
    }
    
    /**
     * Creates a new instance of the authorizer which fits this document
     * @param documentEntry the data dictionary entry for this document
     * @return a new instance of the authorizer
     */
    protected DocumentAuthorizer createAuthorizerInstance(FinancialSystemTransactionalDocumentEntry documentEntry) {
        try {
            return (DocumentAuthorizer)documentEntry.getDocumentAuthorizerClass().newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Couldn't create new instance of authorizer for class: "+this.getClass(),ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Couldn't create new instance of authorizer for class: "+this.getClass(),iae);
        }
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

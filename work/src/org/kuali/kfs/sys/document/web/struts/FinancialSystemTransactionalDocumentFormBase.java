/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;

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
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populateHeaderFields(org.kuali.rice.kew.api.WorkflowDocument)

     * KRAD Conversion: Customizing the header fields - Uses data dictionary
     */
    @Override
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (getDocument().getDocumentHeader() instanceof FinancialSystemDocumentHeader) {
            FinancialSystemDocumentHeader documentHeader = (FinancialSystemDocumentHeader)getDocument().getDocumentHeader();
            if (StringUtils.isNotBlank(documentHeader.getFinancialDocumentInErrorNumber())) {
                extendDocInfoToThreeColumns();
                int insertIndex = 2;
                getDocInfo().remove(insertIndex);
                getDocInfo().add(insertIndex, new HeaderField("DataDictionary.FinancialSystemDocumentHeader.attributes.financialDocumentInErrorNumber",
                        documentHeader.getFinancialDocumentInErrorNumber(), buildHtmlLink(getDocumentHandlerUrl(documentHeader.getFinancialDocumentInErrorNumber()), documentHeader.getFinancialDocumentInErrorNumber())));
            }
            if (StringUtils.isNotBlank(documentHeader.getCorrectedByDocumentId())) {
                extendDocInfoToThreeColumns();
                int insertIndex = getNumColumns() + 2;
                getDocInfo().remove(insertIndex);
                getDocInfo().add(insertIndex, new HeaderField("DataDictionary.FinancialSystemDocumentHeader.attributes.correctedByDocumentId",
                        documentHeader.getCorrectedByDocumentId(), buildHtmlLink(getDocumentHandlerUrl(documentHeader.getCorrectedByDocumentId()), documentHeader.getCorrectedByDocumentId())));


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
     *
     * KRAD Conversion: Customizing the addition of extra buttons
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = super.getExtraButtons();
        if (getDocumentActions().containsKey(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) {
            buttons.add(generateErrorCorrectionButton());
        }
        return buttons;
    }

    private ExtraButton errorCorrectionButton;

    /**
     * Generates an ExtraButton which represents the error correction button
     *
     * @return an ExtraButton representing an ErrorCorrection button
     *
     *  KRAD Conversion: Customizing the error correction button
     */
    protected ExtraButton generateErrorCorrectionButton() {
        if ( errorCorrectionButton == null ) {
            ExtraButton button = new ExtraButton();
            button.setExtraButtonAltText("Create error correction document from current document");
            button.setExtraButtonProperty("methodToCall.correct");
            button.setExtraButtonSource(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("kr.externalizable.images.url")+"buttonsmall_errcorr.gif");
            errorCorrectionButton = button;
        }
        return errorCorrectionButton;
    }


}

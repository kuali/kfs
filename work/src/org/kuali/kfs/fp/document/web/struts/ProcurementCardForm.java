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
package org.kuali.module.financial.web.struts.form;

import java.util.List;

import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.document.ProcurementCardDocument;
import org.kuali.module.financial.service.ProcurementCardCreateDocumentService;

/**
 * This class is the form class for the ProcurementCard document. This method extends the parent KualiTransactionalDocumentFormBase
 * class which contains all of the common form methods and form attributes needed by the Procurment Card document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;
    private List newTargetLines;

    /**
     * Constructs a ProcurmentCardForm instance and sets up the appropriately casted document. Also, the newSourceLine needs to be
     * the extended ProcurementCardSourceAccountingLine, for the additional trans line nbr.
     */
    public ProcurementCardForm() {
        super();
        setDocument(new ProcurementCardDocument());
        this.newTargetLines = new TypedArrayList(ProcurementCardTargetAccountingLine.class);
    }

    /**
     * @return The retreived APC string used for the dispute url.
     */
    public String getDisputeURL() {
        return SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                ProcurementCardCreateDocumentService.PCARD_DOCUMENT_PARAMETERS_SEC_GROUP,
                ProcurementCardCreateDocumentService.DISPUTE_URL_PARM_NM);
    }


    /**
     * @return Returns the newTargetLines.
     */
    public List getNewTargetLines() {
        return newTargetLines;
    }

    /**
     * @param newTargetLines The newTargetLines to set.
     */
    public void setNewTargetLines(List newTargetLines) {
        this.newTargetLines = newTargetLines;
    }


    /**
     * Override to return ProcurementCardTargetAccountingLine
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getBaselineSourceAccountingLine(int)
     */
    public TargetAccountingLine getBaselineTargetAccountingLine(int index) {
        while (getBaselineTargetAccountingLines().size() <= index) {
            getBaselineTargetAccountingLines().add(new ProcurementCardTargetAccountingLine());
        }
        return (ProcurementCardTargetAccountingLine) getBaselineTargetAccountingLines().get(index);
    }
}
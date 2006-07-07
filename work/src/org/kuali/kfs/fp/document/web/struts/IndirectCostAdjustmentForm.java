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

import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.document.IndirectCostAdjustmentDocument;
import org.kuali.module.financial.rules.IndirectCostAdjustmentDocumentRuleConstants;

/**
 * This class is the action form for Indirect Cost Adjustment Document
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */

public class IndirectCostAdjustmentForm extends KualiTransactionalDocumentFormBase {

    /**
     * Constructs a IndirectCostAdjustmentForm.java.
     */
    public IndirectCostAdjustmentForm() {
        super();
        setDocument(new IndirectCostAdjustmentDocument());
    }

    /**
     * @return Returns the Indirect Cost Adjustment Document
     */
    public IndirectCostAdjustmentDocument getIndirectCostAdjustmentDocument() {
        return (IndirectCostAdjustmentDocument) getDocument();
    }

    /**
     * @param indirectCostAdjustmentDocument the <code>IndirectCostAdjustmentDocument</code> to set
     */
    public void setIndirectCostAdjustmentDocument(IndirectCostAdjustmentDocument indirectCostAdjustmentDocument) {
        setDocument(indirectCostAdjustmentDocument);
    }

    /**
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#createNewSourceAccountingLine(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    public SourceAccountingLine createNewSourceAccountingLine(TransactionalDocument transactionalDocument) {
        SourceAccountingLine sourceAccountingLine = super.createNewSourceAccountingLine(transactionalDocument);
        String objectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(IndirectCostAdjustmentDocumentRuleConstants.INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, IndirectCostAdjustmentDocumentRuleConstants.GRANT_OBJECT_CODE);

        sourceAccountingLine.setFinancialObjectCode(objectCode);
        return sourceAccountingLine;
    }

    /**
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#createNewTargetAccountingLine(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    public TargetAccountingLine createNewTargetAccountingLine(TransactionalDocument transactionalDocument) {
        TargetAccountingLine targetAccountingLine = super.createNewTargetAccountingLine(transactionalDocument);
        String objectCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(IndirectCostAdjustmentDocumentRuleConstants.INDIRECT_COST_ADJUSTMENT_DOCUMENT_SECURITY_GROUPING, IndirectCostAdjustmentDocumentRuleConstants.RECEIPT_OBJECT_CODE);

        targetAccountingLine.setFinancialObjectCode(objectCode);
        return targetAccountingLine;
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    @Override
    public Map getForcedReadOnlyFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(PropertyConstants.FINANCIAL_OBJECT_CODE, Boolean.TRUE);
        return map;
    }

}

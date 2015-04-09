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
package org.kuali.kfs.fp.document.web.struts;

import java.util.Map;

import org.kuali.kfs.fp.document.IndirectCostAdjustmentDocument;
import org.kuali.kfs.fp.document.validation.impl.IndirectCostAdjustmentDocumentRuleConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class is the action form for Indirect Cost Adjustment Document
 */
public class IndirectCostAdjustmentForm extends KualiAccountingDocumentFormBase {

    /**
     * Constructs a IndirectCostAdjustmentForm.java.
     */
    public IndirectCostAdjustmentForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "ICA";
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
     * @see org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase#createNewSourceAccountingLine(org.kuali.module.financial.document.FinancialDocument)
     */
    @Override
    public SourceAccountingLine createNewSourceAccountingLine(AccountingDocument financialDocument) {
        SourceAccountingLine sourceAccountingLine = super.createNewSourceAccountingLine(financialDocument);
        String objectCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(IndirectCostAdjustmentDocument.class, IndirectCostAdjustmentDocumentRuleConstants.GRANT_OBJECT_CODE);

        sourceAccountingLine.setFinancialObjectCode(objectCode);
        return sourceAccountingLine;
    }

    /**
     * @see org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase#createNewTargetAccountingLine(org.kuali.module.financial.document.FinancialDocument)
     */
    @Override
    public TargetAccountingLine createNewTargetAccountingLine(AccountingDocument financialDocument) {
        TargetAccountingLine targetAccountingLine = super.createNewTargetAccountingLine(financialDocument);
        String objectCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(IndirectCostAdjustmentDocument.class, IndirectCostAdjustmentDocumentRuleConstants.RECEIPT_OBJECT_CODE);

        targetAccountingLine.setFinancialObjectCode(objectCode);
        return targetAccountingLine;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    @Override
    public Map getForcedReadOnlyFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, Boolean.TRUE);
        return map;
    }

}

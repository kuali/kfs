/*
 * Copyright 2005-2006 The Kuali Foundation
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

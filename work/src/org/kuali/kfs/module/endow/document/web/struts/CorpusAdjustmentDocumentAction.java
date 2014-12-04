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
package org.kuali.kfs.module.endow.document.web.struts;

import static org.kuali.kfs.module.endow.EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME;
import static org.kuali.kfs.module.endow.EndowConstants.NEW_TARGET_TRAN_LINE_PROPERTY_NAME;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.KualiRuleService;

public class CorpusAdjustmentDocumentAction extends EndowmentTransactionLinesDocumentActionBase {

    /**
     * This action executes an insert of an EndowmentTargetTransactionLine into a document only after validating the Transaction
     * line and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward insertSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentSourceTransactionLine transLine = (EndowmentSourceTransactionLine) documentForm.getNewSourceTransactionLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent(NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

        if (rulePassed) {
            // add accountingLine
            // SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            insertTransactionLine(true, documentForm, transLine);

            // clear the used newTargetLine
            documentForm.setNewSourceTransactionLine(new EndowmentSourceTransactionLine());
            documentForm.getNewSourceTransactionLine().setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action executes an insert of an EndowmentTargetTransactionLine into a document only after validating the Transaction
     * line and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward insertTargetTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EndowmentTransactionLinesDocumentFormBase documentForm = (EndowmentTransactionLinesDocumentFormBase) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentTargetTransactionLine transLine = (EndowmentTargetTransactionLine) documentForm.getNewTargetTransactionLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent(NEW_TARGET_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

        if (rulePassed) {
            // add accountingLine

            // SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            insertTransactionLine(false, documentForm, transLine);

            // clear the used newTargetLine
            documentForm.setNewTargetTransactionLine(new EndowmentTargetTransactionLine());
            documentForm.getNewTargetTransactionLine().setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}

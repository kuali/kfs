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
package org.kuali.module.financial.rules;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.rule.PreRulesCheck;
import org.kuali.core.rule.event.PreRulesCheckEvent;
import org.kuali.core.rules.RulesUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.Payee;

/**
 * Checks warnings and prompt conditions for payee document.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentPreRules implements PreRulesCheck, DisbursementVoucherConstants {
    private String buttonClicked;
    private String question;
    private KualiConfigurationService kualiConfiguration;

    /**
     * @see org.kuali.core.rule.PreRulesCheck#processPreRuleChecks(org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
     *      org.kuali.core.rule.event.PreRulesCheckEvent)
     */
    public boolean processPreRuleChecks(ActionForm form, HttpServletRequest request, PreRulesCheckEvent event) {

        kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();
        question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
        event.setQuestionContext(request.getParameter(Constants.QUESTION_CONTEXT));

//        dv warnings
//
//        if ($$curr_state != $$screate & $$curr_state != $$snew)
//            message/info "Please attach a note explaining why you are changing the payment method."
//            call fp_notes
//            if ($$note_option != "NEW")
//                return $$cancel
//            endif
//        endif
//
//        if (dv_pmt_mthd_cd != "C" & dv_pmt_mthd_cd != "P")
//          if (dv_pmt_mthd_cd = "W")
//            $$message = "You have selected a wire transfer payment method for which you will be charged a fee."
//            run "fp_g0055"
//          endif
//          call lp_fdwt
//          if ($status != $$success)
//            dv_pmt_mthd_cd = ""
//          endif
//        endif
//        if (dv_attch_ind.fp_dv_doc_t & (dv_pmt_mthd_cd = "W" | dv_pmt_mthd_cd = "A"))
//            askmess "You cannot send an attachment with this payment method.","OK"
//            dv_attch_ind.fp_dv_doc_t = $$false
//        endif

        return true;
    }


    private boolean performW9Check(HttpServletRequest request, PreRulesCheckEvent event) {
        MaintenanceDocument document = (MaintenanceDocument) event.getDocument();
        Payee oldPayee = (Payee) document.getOldMaintainableObject().getBusinessObject();
        Payee newPayee = (Payee) document.getNewMaintainableObject().getBusinessObject();
        
        Set W9_OWN_TYPS = RulesUtils.makeSet(new String[] { "M", "I", "P", "S" });

        /**
         * give warning about required tax forms
         */
        if (question != null && Constants.PAYEE_W9_QUESTION.equals(question)) {
            if (ConfirmationQuestion.NO.equals(buttonClicked)) {
                event.setActionForwardName(Constants.MAPPING_BASIC);
                return false;
            }
            else {
                return true;
            }
        }
        else if (!StringUtils.contains(event.getQuestionContext(), "w9CheckDone")) {
            event.setQuestionId(Constants.PAYEE_W9_QUESTION);
            event.setQuestionType(Constants.CONFIRMATION_QUESTION);
            event.setQuestionContext(event.getQuestionContext() + "w9CheckDone");
            if (!oldPayee.isPayeeW9CompleteCode() && newPayee.isPayeeW9CompleteCode()) {
                if (RulesUtils.permitted(W9_OWN_TYPS, newPayee.getPayeeOwnershipTyp())) {
                    if (newPayee.isAlienPaymentCode()) {
                        event.setQuestionText(kualiConfiguration.getPropertyString(KeyConstants.WARNING_DV_W9_ALIEN));
                    }
                    else {
                        event.setQuestionText(kualiConfiguration.getPropertyString(KeyConstants.WARNING_DV_W9_NONALIEN));
                    }
                    event.setPerformQuestion(true);
                }
            }
            else if (!oldPayee.isPayeeW9CompleteCode() && !newPayee.isPayeeW9CompleteCode()) {
                if (newPayee.isAlienPaymentCode()) {
                    event.setQuestionText(kualiConfiguration.getPropertyString(KeyConstants.WARNING_MISSING_DV_W9_ALIEN));
                }
                else {
                    event.setQuestionText(kualiConfiguration.getPropertyString(KeyConstants.WARNING_MISSING_DV_W9_NONALIEN));
                }
                event.setPerformQuestion(true);
            }
        }

        if (event.isPerformQuestion()) {
            return false;
        }
        else {
            return true;
        }
    }

}
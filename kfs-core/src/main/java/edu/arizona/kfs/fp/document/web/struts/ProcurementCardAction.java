package edu.arizona.kfs.fp.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.fp.batch.service.ProcurementCardCreateDocumentService;
import edu.arizona.kfs.fp.document.ProcurementCardDocument;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.document.DocumentRefreshQueue;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;
import org.kuali.rice.kew.api.action.DocumentActionParameters;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class ProcurementCardAction extends org.kuali.kfs.fp.document.web.struts.ProcurementCardAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAction.class);
    
    private static final String REASON = "reason";
    private static final String HAS_RECONCILER_NODE = "HasReconciler";
    private static final String ANNOTATION = " Approver Return to Reconciler";

    /**
     *  Provide a mechanism to route/return the pcard document back to the first approval node (reconciler).
     */
    public ActionForward returnToReconciler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering ProcurementCardAction returnToReconciler() ...");
        
        Object question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String disapprovalNoteText = KFSConstants.EMPTY_STRING;

        // Use the Disapproval mechanism of forcing a note to be supplied when returning document
        // back to the reconciler.
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, KRADConstants.DOCUMENT_DISAPPROVE_QUESTION, getKualiConfigurationService().getPropertyValueAsString(KFSKeyConstants.QUESTION_RETURN_DOCUMENT), KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_DISAPPROVE, "");
        }
        else {
            Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if ((KRADConstants.DOCUMENT_DISAPPROVE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return mapping.findForward(RiceConstants.MAPPING_BASIC);
            }
            else {
                // have to check length on value entered
                String introNoteMessage = getKualiConfigurationService().getPropertyValueAsString(KFSKeyConstants.MESSAGE_RETURN_NOTE_TEXT_INTRO) + KRADConstants.BLANK_SPACE;

                // build out full message
                disapprovalNoteText = introNoteMessage + reason;
                int disapprovalNoteTextLength = disapprovalNoteText.length();

                // get note text max length from DD
                int noteTextMaxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, KRADConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

                if (StringUtils.isBlank(reason) || (disapprovalNoteTextLength > noteTextMaxLength)) {
                    // figure out exact number of characters that the user can enter
                    int reasonLimit = noteTextMaxLength - disapprovalNoteTextLength;

                    if (reason == null) {
                        // prevent a NPE by setting the reason to a blank string
                        reason = KFSConstants.EMPTY_STRING;
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, KRADConstants.DOCUMENT_DISAPPROVE_QUESTION, getKualiConfigurationService().getPropertyValueAsString(KFSKeyConstants.QUESTION_RETURN_DOCUMENT), KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_DISAPPROVE, "", reason, KFSKeyConstants.ERROR_DOCUMENT_RETURN_REASON_REQUIRED, KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                }

                if (KRADUtils.containsSensitiveDataPatternMatch(disapprovalNoteText)) {
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response,
                            KRADConstants.DOCUMENT_DISAPPROVE_QUESTION, getKualiConfigurationService().getPropertyValueAsString(KFSKeyConstants.QUESTION_RETURN_DOCUMENT),
                            KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_DISAPPROVE, "", reason, RiceKeyConstants.ERROR_DOCUMENT_FIELD_CONTAINS_POSSIBLE_SENSITIVE_DATA,
                            KRADConstants.QUESTION_REASON_ATTRIBUTE_NAME, REASON);
                }
            }
        }
        
        Note returnNote = new Note();
        returnNote.setNoteText(disapprovalNoteText);
        NoteService noteService = SpringContext.getBean(NoteService.class);
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        
        String systemUserPrincipalId = getSystemUserPrincipalId();
        returnNote = noteService.createNote(returnNote, procurementCardForm.getDocument().getNoteTarget(), systemUserPrincipalId);
        returnNote.setNotePostedTimestampToCurrent();
        noteService.save(returnNote);
        
        List<RouteNodeInstance> routeNodeInstances = procurementCardForm.getProcurementCardDocument().getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeInstances();
        String node = routeNodeInstances.get(0).getName();
        SpringContext.getBean(ProcurementCardCreateDocumentService.class).requeueDocument((ProcurementCardDocument)procurementCardForm.getDocument(), node, HAS_RECONCILER_NODE, ANNOTATION);

        return returnToSender(request, mapping, procurementCardForm);
    }
    
    protected String getSystemUserPrincipalId() {
        String systemUserName = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KfsParameterConstants.ALL_COMPONENT, KFSConstants.SYSTEM_USER_NAME);
        Person systemUser = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(systemUserName);
        return systemUser.getPrincipalId();
    }
}

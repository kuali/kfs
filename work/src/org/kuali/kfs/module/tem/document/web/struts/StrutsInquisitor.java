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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.purap.SingleConfirmationQuestion.OK;
import static org.kuali.kfs.sys.KFSConstants.QUESTION_CLICKED_BUTTON;
import static org.kuali.rice.kns.question.ConfirmationQuestion.NO;
import static org.kuali.rice.krad.util.ObjectUtils.isNull;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public class StrutsInquisitor<Document,StrutsF,StrutsA> implements Inquisitive<Document,ActionForward> {
    private ActionMapping mapping;
    private StrutsF form;
    private StrutsA action;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Document document;

    public StrutsInquisitor(final ActionMapping mapping,
                            final StrutsF form,
                            final StrutsA action,
                            final HttpServletRequest request,
                            final HttpServletResponse response) {
        this.mapping = mapping;
        this.form = form;
        this.action = action;
        this.request = request;
        this.response = response;
    }

    /**
     * Gets the actionMapping attribute.
     *
     * @return Returns the actionMapping.
     */
    public ActionMapping getMapping() {
        return mapping;
    }


    /**
     * Sets the actionMapping attribute value.
     *
     * @param actionMapping The actionMapping to set.
     */
    public void setMapping(final ActionMapping actionMapping) {
        this.mapping = actionMapping;
    }

    /**
     * Gets the form attribute.
     *
     * @return Returns the form.
     */
    public StrutsF getForm() {
        return form;
    }


    /**
     * Sets the form attribute value.
     *
     * @param form The form to set.
     */
    public void setForm(final StrutsF form) {
        this.form = form;
    }

    /**
     * Gets the action attribute.
     *
     * @return Returns the action.
     */
    public StrutsA getAction() {
        return action;
    }


    /**
     * Sets the action attribute value.
     *
     * @param action The action to set.
     */
    public void setAction(final StrutsA action) {
        this.action = action;
    }

    @Override
    public boolean wasQuestionAsked() {
        return !isNull(getRequest().getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME));
    }

    /**
     * Gets the request attribute.
     *
     * @return Returns the request.
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Sets the request attribute value.
     *
     * @param request The request to set.
     */
    public void setRequest(final HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Gets the response attribute.
     *
     * @return Returns the response.
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Sets the response attribute value.
     *
     * @param response The response to set.
     */
    public void setResponse(final HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public Document getDocument() {
        if (document == null){
            document =(Document) ((TravelFormBase)this.getForm()).getDocument();
        }
        return document;
    }

    @Override
    public String getReason() {
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        return reason == null?"":reason;
    }

    @Override
    public Object getQuestion() {
        return request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
    }

    @Override
    public boolean denied(final String qid) {
        final Object buttonClicked = request.getParameter(QUESTION_CLICKED_BUTTON);
        return getQuestion().equals(qid) && buttonClicked.equals(NO);
    }

    @Override
    public boolean confirmed(final String qid) {
        final Object buttonClicked = request.getParameter(QUESTION_CLICKED_BUTTON);
        return getQuestion().equals(qid) && buttonClicked.equals(OK);
    }

    @Override
    public ActionForward finish() throws Exception {
        return back();
    }

    @Override
    public ActionForward back() throws Exception {
        return getMapping().findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward end() throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_PORTAL);
    }

    @SuppressWarnings("null")
    protected ActionForward performQuestion(final String questionId,
                                            final String questionText,
                                            final String questionType,
                                            final String caller,
                                            final String context,
                                            final boolean showReasonField,
                                            final String reason,
                                            final String errorKey,
                                            final String errorPropertyName,
                                            final String errorParameter) throws Exception {
        Method method = null;
        try {
            method = KualiAction.class.getDeclaredMethod("performQuestion", new Class[] { ActionMapping.class,
                                                                                          ActionForm.class,
                                                                                          HttpServletRequest.class,
                                                                                          HttpServletResponse.class,
                                                                                          String.class,
                                                                                          String.class,
                                                                                          String.class,
                                                                                          String.class,
                                                                                          String.class,
                                                                                          boolean.class,
                                                                                          String.class,
                                                                                          String.class,
                                                                                          String.class,
                                                                                          String.class});
            method.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return (ActionForward) method.invoke(getAction(), getMapping(), getForm(), getRequest(), getResponse(),
                                             questionId, questionText, questionType, caller, context, showReasonField, reason, errorKey, errorPropertyName, errorParameter);
    }

    @Override
    public ActionForward confirm(final String questionType, final String message, final boolean showReasonField, final String ... errorArgs) throws Exception {
        if (errorArgs.length > 0) {
            return performQuestion(questionType, message, TemConstants.QUESTION_CONFIRMATION, questionType, "", showReasonField, getReason(), errorArgs[0], errorArgs[1], errorArgs[2]);
        }
        return performQuestion(questionType, message, TemConstants.QUESTION_CONFIRMATION, questionType, "", showReasonField, "", "", "", "");
    }
}

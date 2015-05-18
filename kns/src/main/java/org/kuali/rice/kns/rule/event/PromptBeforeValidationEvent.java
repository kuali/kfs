/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.rule.event;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * Event for handling warnings/questions before rules are called.
 * 
 * 
 */
public class PromptBeforeValidationEvent extends KualiDocumentEventBase {
    private static final Logger LOG = Logger.getLogger(PromptBeforeValidationEvent.class);

    boolean performQuestion;
    String actionForwardName;
    String questionId;
    String questionText;
    String questionType;
    String questionCaller;
    String questionContext;


    /**
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public PromptBeforeValidationEvent(String description, String errorPathPrefix, Document document) {

        super(description, errorPathPrefix);
        this.document = document;

        LOG.debug(description);

        performQuestion = false;
    }


    /**
     * @return Returns the actionForwardName.
     */
    public String getActionForwardName() {
        return actionForwardName;
    }

    /**
     * @param actionForwardName The actionForwardName to set.
     */
    public void setActionForwardName(String actionForwardName) {
        this.actionForwardName = actionForwardName;
    }

    /**
     * @return Returns the performQuestion.
     */
    public boolean isPerformQuestion() {
        return performQuestion;
    }

    /**
     * @param performQuestion The performQuestion to set.
     */
    public void setPerformQuestion(boolean performQuestion) {
        this.performQuestion = performQuestion;
    }

    /**
     * @return Returns the questionCaller.
     */
    public String getQuestionCaller() {
        return questionCaller;
    }

    /**
     * @param questionCaller The questionCaller to set.
     */
    public void setQuestionCaller(String questionCaller) {
        this.questionCaller = questionCaller;
    }

    /**
     * @return Returns the questionContext.
     */
    public String getQuestionContext() {
        return questionContext;
    }

    /**
     * @param questionContext The questionContext to set.
     */
    public void setQuestionContext(String questionContext) {
        this.questionContext = questionContext;
    }

    /**
     * @return Returns the questionId.
     */
    public String getQuestionId() {
        return questionId;
    }

    /**
     * @param questionId The questionId to set.
     */
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    /**
     * @return Returns the questionText.
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * @param questionText The questionText to set.
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * @return Returns the questionType.
     */
    public String getQuestionType() {
        return questionType;
    }

    /**
     * @param questionType The questionType to set.
     */
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    /**
     * @see org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return PromptBeforeValidation.class;
    }


    /**
     * @see org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return true;
    }
}

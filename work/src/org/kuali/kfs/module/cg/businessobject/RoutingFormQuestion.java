/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class RoutingFormQuestion extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String questionTypeCode;
    private String yesNoIndicator;

    private QuestionType question;

    public RoutingFormQuestion() {
        super();
    }

    public RoutingFormQuestion(String documentNumber, QuestionType questionType) {
        super();
        this.documentNumber = documentNumber;
        this.questionTypeCode = questionType.getQuestionTypeCode();
        this.question = questionType;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the questionTypeCode attribute.
     * 
     * @return Returns the questionTypeCode.
     */
    public String getQuestionTypeCode() {
        return questionTypeCode;
    }

    /**
     * Sets the questionTypeCode attribute value.
     * 
     * @param questionTypeCode The questionTypeCode to set.
     */
    public void setQuestionTypeCode(String questionTypeCode) {
        this.questionTypeCode = questionTypeCode;
    }

    /**
     * Gets the yesNoIndicator attribute.
     * 
     * @return Returns the yesNoIndicator.
     */
    public String getYesNoIndicator() {
        return yesNoIndicator;
    }

    /**
     * Sets the yesNoIndicator attribute value.
     * 
     * @param yesNoIndicator The yesNoIndicator to set.
     */
    public void setYesNoIndicator(String yesNoIndicator) {
        this.yesNoIndicator = yesNoIndicator;
    }

    /**
     * Gets the question attribute.
     * 
     * @return Returns the question.
     */
    public QuestionType getQuestion() {
        return question;
    }

    /**
     * Sets the question attribute value.
     * 
     * @param question The question to set.
     */
    public void setQuestion(QuestionType question) {
        this.question = question;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("questionTypeCode", this.questionTypeCode);
        return m;
    }
}

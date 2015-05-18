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
package org.kuali.rice.kns.question;

import java.util.ArrayList;

/**
 * This class is a base class to implement questions types.
 * 
 * 
 *         "confirmation questions") rather than specific questions.
 */

public class QuestionBase implements Question {
    String question;
    ArrayList buttons;

    /**
     * default constructor
     * 
     * @param question the question to assign to this question prompt
     * @param buttons the buttons associated with it
     */
    public QuestionBase(String question, ArrayList buttons) {
        this.question = question;
        this.buttons = buttons;
    }

    /**
     * returns the index associated with a specified button
     * 
     * @param btnText the text of the button
     * @return the index of this button
     */
    public String getButtonIndex(String btnText) {
        return "" + buttons.indexOf(btnText);
    }

    /**
     * @return Returns the buttons.
     */
    public ArrayList getButtons() {
        return buttons;
    }

    /**
     * @param buttons The buttons to set.
     */
    public void setButtons(ArrayList buttons) {
        this.buttons = buttons;
    }

    /**
     * @return Returns the question.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question The question to set.
     */
    public void setQuestion(String question) {
        this.question = question;
    }
}

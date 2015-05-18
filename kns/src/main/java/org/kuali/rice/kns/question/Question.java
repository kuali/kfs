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
 * This interface defines methods that are required to support c Confirmation Question.
 * 
 * 
 */
public interface Question {
    /**
     * returns the index associated with a specified button
     * 
     * @param btnText the text of the button
     * @return the index of this button
     */
    public String getButtonIndex(String btnText);

    /**
     * @return Returns the buttons.
     */
    public abstract ArrayList getButtons();

    /**
     * @param buttons The buttons to set.
     */
    public abstract void setButtons(ArrayList buttons);

    /**
     * @return Returns the question.
     */
    public abstract String getQuestion();

    /**
     * @param question The question to set.
     */
    public abstract void setQuestion(String question);
}

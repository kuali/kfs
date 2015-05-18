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
 * Recall to Action List / Recall & Cancel question for Recall functionality.
 * Note: this could possibly be generalized to a generic MultipleChoiceQuestion in combination w/ special question context
 */
public class RecallQuestion extends QuestionBase {
    public static final String RECALL_TO_ACTIONLIST = "0";
    public static final String RECALL_AND_CANCEL = "1";

    public RecallQuestion() {
        // this should be set by question form
        super("Return document to Action List or Cancel document?", new ArrayList(2));
        this.buttons.add("recallactionlist");
        this.buttons.add("recallcancel");

    }
}
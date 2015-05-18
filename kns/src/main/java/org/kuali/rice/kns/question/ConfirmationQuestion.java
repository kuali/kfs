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
 * This class support the ConfirmationQuestion. For example: a Yes/No dialog window.
 */

public class ConfirmationQuestion extends QuestionBase {

    public static final String YES = "0";
    public static final String NO = "1";

    public ConfirmationQuestion() {
        // this should be set by question form
        super("Are you sure you want to cancel?", new ArrayList(2));
        this.buttons.add("Yes");
        this.buttons.add("No");

    }

}

/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.module.purap;

import java.util.ArrayList;

import org.kuali.rice.kns.question.QuestionBase;

/**
 * Support the SingleConfirmationQuestion that displays a window with an OK button.
 */
public class SingleConfirmationQuestion extends QuestionBase {

    public static final String OK = "0";

    public SingleConfirmationQuestion() {
        // this should be set by question form
        super("Confirmed", new ArrayList(1));
        this.getButtons().add("ok");
    }

}

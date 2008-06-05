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
package org.kuali.module.purap.util;

import org.kuali.module.purap.document.ReceivingDocument;

/**
 * Receiving Question Callback
 * Defines a callback method for post processing handling in the question interface.
 */
public interface ReceivingQuestionCallback {

    public boolean questionComplete = false;
    
    /**
     * Hooks for doing processing on the document after a question has been performed.
     * 
     * @param document - receiving document
     * @param noteText - user entered note
     */
    public ReceivingDocument doPostQuestion(ReceivingDocument document, String noteText) throws Exception;
    
    /**
     * Has the question been answered completely to move forward.
     * 
     * @return
     */
    public boolean isQuestionComplete();
    
    /**
     * Setter for question complete
     * 
     * @param questionComplete
     * @return
     */
    public void setQuestionComplete(boolean questionComplete);
}

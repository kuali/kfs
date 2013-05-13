/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

public interface EndowmentTransactionLinesDocumentService extends EndowmentTransactionDocumentService{
    
    /**
     * Get the value of CorpusIndicator
     * @param kemid
     * @param etranCode
     * @param ipIndicator
     * @return the value of CorpusIndicator -- true or false
     */
    public boolean getCorpusIndicatorValueforAnEndowmentTransactionLine (String kemid, String etranCode, String ipIndicator);

    /**
     * Check if a KEMID can have a principal activity
     * @param kemid
     * @param ipIndicator
     * @return true or false
     */
    public boolean canKEMIDHaveAPrincipalActivity (String kemid, String ipIndicator);
}

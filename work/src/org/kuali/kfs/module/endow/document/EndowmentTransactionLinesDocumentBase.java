/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;

public abstract class EndowmentTransactionLinesDocumentBase extends EndowmentTransactionalDocumentBase implements EndowmentTransactionLinesDocument {

    protected List<EndowmentTransactionLine> sourceTransactionLines;
    protected List<EndowmentTransactionLine> targetTransactionLines;

    /**
     * Constructs a EndowmentTransactionLinesDocumentBase.java.
     */
    public EndowmentTransactionLinesDocumentBase() {
        super();
        sourceTransactionLines = new ArrayList<EndowmentTransactionLine>();
        targetTransactionLines = new ArrayList<EndowmentTransactionLine>();
    }

    public List<EndowmentTransactionLine> getSourceTransactionLines() {
        return sourceTransactionLines;
    }

    public void setSourceTransactionLines(List<EndowmentTransactionLine> sourceTransactionLines) {
        this.sourceTransactionLines = sourceTransactionLines;
    }

    public List<EndowmentTransactionLine> getTargetTransactionLines() {
        return targetTransactionLines;
    }

    public void setTargetTransactionLines(List<EndowmentTransactionLine> targetTransactionLines) {
        this.targetTransactionLines = targetTransactionLines;
    }

}

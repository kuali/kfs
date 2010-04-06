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

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineParser;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

public abstract class EndowmentTransactionLinesDocumentBase extends EndowmentTransactionalDocumentBase implements EndowmentTransactionLinesDocument {

    protected Integer nextSourceLineNumber;
    protected Integer nextTargetLineNumber;
    protected List<EndowmentTransactionLine> sourceTransactionLines;
    protected List<EndowmentTransactionLine> targetTransactionLines;

    /**
     * Constructs a EndowmentTransactionLinesDocumentBase.java.
     */
    public EndowmentTransactionLinesDocumentBase() {
        super();
        this.nextSourceLineNumber = new Integer(1);
        this.nextTargetLineNumber = new Integer(1);
        sourceTransactionLines = new TypedArrayList(EndowmentTransactionLine.class);
        targetTransactionLines = new TypedArrayList(EndowmentTransactionLine.class);
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

    public String getSourceTransactionLinesSectionTitle() {
        return KFSConstants.SOURCE;

    }

    public String getTargetTransactionLinesSectionTitle() {
        return KFSConstants.TARGET;
    }

    public Integer getNextSourceLineNumber() {
        return nextSourceLineNumber;
    }

    public void setNextSourceLineNumber(Integer nextSourceLineNumber) {
        this.nextSourceLineNumber = nextSourceLineNumber;
    }

    public Integer getNextTargetLineNumber() {
        return nextTargetLineNumber;
    }

    public void setNextTargetLineNumber(Integer nextTargetLineNumber) {
        this.nextTargetLineNumber = nextTargetLineNumber;
    }


    public void addSourceTransactionLine(EndowmentSourceTransactionLine line) {
        // TODO Auto-generated method stub

    }


    public void addTargetTransactionLine(EndowmentTargetTransactionLine line) {
        // TODO Auto-generated method stub

    }


    public KualiDecimal getSourceIncomeTotal() {
        // TODO Auto-generated method stub
        return null;
    }


    public KualiDecimal getSourcePrincipalTotal() {
        // TODO Auto-generated method stub
        return null;
    }


    public EndowmentSourceTransactionLine getSourceTransactionLine(int index) {
        // TODO Auto-generated method stub
        return null;
    }


    public KualiDecimal getTargetIncomeTotal() {
        // TODO Auto-generated method stub
        return null;
    }


    public KualiDecimal getTargetPrincipalTotal() {
        // TODO Auto-generated method stub
        return null;
    }


    public EndowmentTargetTransactionLine getTargetTransactionLine(int index) {
        // TODO Auto-generated method stub
        return null;
    }


    public EndowmentTransactionLineParser getTransactionLineParser() {
        // TODO Auto-generated method stub
        return null;
    }

}

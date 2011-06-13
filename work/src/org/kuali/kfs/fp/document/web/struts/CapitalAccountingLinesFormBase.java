/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAccountingLine;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This class is the struts base form for financial form documents that need
 * capital accounting lines.
 */
public abstract class CapitalAccountingLinesFormBase extends KualiAccountingDocumentFormBase {
    //transient objects
    protected transient List<CapitalAccountingLines> capitalAccountingLines;
    protected transient CapitalAccountingLine capitalAccountingLine;
    protected transient boolean editCreateOrModify;
    
    /**
     * Constructs a AdvanceDepositForm.java.
     */
    public CapitalAccountingLinesFormBase() {
        super();
        
        capitalAccountingLines = new TypedArrayList(CapitalAccountingLines.class);
        capitalAccountingLine = new CapitalAccountingLine();
        editCreateOrModify = true;
    }

    /**
     * Gets the capitalAccountingLines attribute.
     * 
     * @return Returns the capitalAccountingLines
     */
    public List<CapitalAccountingLines> getCapitalAccountingLines() {
        return capitalAccountingLines;
    }

    /** 
     * Sets the capitalAccountingLines attribute.
     * 
     * @param capitalAccountingLines The capitalAccountingLines to set.
     */
    public void setCapitalAccountingLines(List<CapitalAccountingLines> capitalAccountingLines) {
        this.capitalAccountingLines = capitalAccountingLines;
    }
    
    /**
     * Gets the capitalAccountingLine attribute.
     * 
     * @return Returns the capitalAccountingLine
     */
    
    public CapitalAccountingLine getCapitalAccountingLine() {
        return capitalAccountingLine;
    }

    /** 
     * Sets the capitalAccountingLine attribute.
     * 
     * @param capitalAccountingLine The capitalAccountingLine to set.
     */
    public void setCapitalAccountingLine(CapitalAccountingLine capitalAccountingLine) {
        this.capitalAccountingLine = capitalAccountingLine;
    }
    
    /**
     * Gets the editCreateOrModify attribute.
     * 
     * @return Returns the editCreateOrModify
     */
    
    public boolean isEditCreateOrModify() {
        return editCreateOrModify;
    }

    /** 
     * Sets the editCreateOrModify attribute.
     * 
     * @param editCreateOrModify The editCreateOrModify to set.
     */
    public void setEditCreateOrModify(boolean editCreateOrModify) {
        this.editCreateOrModify = editCreateOrModify;
    }
}

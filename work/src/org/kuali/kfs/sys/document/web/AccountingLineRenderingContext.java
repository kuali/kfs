/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.web;

import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;

/**
 * A contract for classes which wish to provide information about an accounting line which is being rendered
 */
public interface AccountingLineRenderingContext {
    
    /**
     * @return the accounting line that would be rendered by the rendering of the given accounting line context
     */
    public abstract AccountingLine getAccountingLine();
    
    /**
     * @return the property path from the form to the accounting line returned by the getAccountingLine() method
     */
    public abstract String getAccountingLinePropertyPath();
    
    /**
     * @return a List of actions which can be performed on the given line
     */
    public abstract List<AccountingLineViewAction> getActionsForLine();
    
    /**
     * Tells callers if fields should render help or not
     * @return true if fields should render help, false otherwise
     */
    public abstract boolean fieldsShouldRenderHelp();
    
    /**
     * Tells callers if dynamic field labels should even be rendered
     * @return true if dynamic field labels can be labeled, false if they should not be labeled.
     */
    public abstract boolean fieldsCanRenderDynamicLabels();
    
    /**
     * Reports whether the tag to be rendered by this rendering context is "new" - ie, not added yet to the accounting group, but living on the form somewhere - or not
     * @return true if the line is new, false otherwise
     */
    public abstract boolean isNewLine();
    
    /**
     * Returns all the field names for the given accounting line, prefixed by the accounting line property path
     * @return a list of properly prefixed field names
     */
    public abstract List<String> getFieldNamesForAccountingLine();
}

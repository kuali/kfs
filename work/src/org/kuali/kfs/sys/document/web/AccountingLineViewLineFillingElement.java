/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web;

/**
 * A contract for accounting line view layout elements which join tables as part of an AccountingLineViewLines element
 */
public interface AccountingLineViewLineFillingElement extends TableJoining, ReadOnlyable {
    /**
     * Finds the number of table cells this line expects to take up
     * @return the number of displayed table cells this line expects to render as
     */
    public abstract int getDisplayingFieldWidth();
    
    /**
     * A way to ask the line filling element if it wants its cell to be stretched to fill the line
     * @return true if the line filling element should stretch its cell to fill the line; if false (or if the line contains more than a single cell), the line will be padded out with an empty cell
     */
    public abstract boolean shouldStretchToFillLine();
}

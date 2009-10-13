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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * Behaviors needed by all elements which represent elements that can be rendered as
 * part of an accounting line view
 */
public interface AccountingLineViewRenderableElementDefinition {
    /**
     * Returns the TableJoining element that this definition requires to be part of the accounting line view
     * @return a properly created TableJoining element
     */
    public abstract TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass);
}

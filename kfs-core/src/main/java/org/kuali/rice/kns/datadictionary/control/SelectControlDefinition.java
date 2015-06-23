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
package org.kuali.rice.kns.datadictionary.control;


/**
 The select element will render an HTML select control.
 The valuesFinderClass will have a getKeyValues() method
 that returns a list of KeyValue objects.

 An optional script attribute allows java script code to be
 entered.  This code will be run when the user selects a new
 value.
 */
@Deprecated
public class SelectControlDefinition extends MultivalueControlDefinitionBase {
    private static final long serialVersionUID = 7176759040882704084L;

    public SelectControlDefinition() {
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isSelect()
     */
    @Override
    public boolean isSelect() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SelectControlDefinition";
    }

}

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
                        The checkbox element is used to render an HTML checkbox
                        control.  It is used for boolean fields.
 */
@Deprecated
public class CheckboxControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -2658505826476098781L;

	public CheckboxControlDefinition()
    {
        super();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isCheckbox()
     */
    public boolean isCheckbox() {
        return true;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "CheckboxControlDefinition";
    }
}

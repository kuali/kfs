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
                        The radio element will render an HTML radio control.
                        The valuesFinderClass will have a getKeyValues() method
                        that returns a list of KeyValue objects.
 */
@Deprecated
public class RadioControlDefinition extends MultivalueControlDefinitionBase {
    private static final long serialVersionUID = -7578183583825935850L;

	public RadioControlDefinition() {
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isRadio()
     */
    @Override
	public boolean isRadio() {
        return true;
    }

    /**
     * @see Object#toString()
     */
    @Override
	public String toString() {
        return "RadioControlDefinition";
    }
}

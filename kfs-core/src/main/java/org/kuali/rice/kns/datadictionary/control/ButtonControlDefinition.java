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
 The button element is used to render an HTML button
 control.
 */
public class ButtonControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -4014804850405243606L;

    protected String imageSrc;
    protected String styleClass;

    public ButtonControlDefinition() {
        this.type = ControlDefinitionType.BUTTON;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isButton()
     */
    public boolean isButton() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.getClass().getName();
    }

    /**
     * @return the mageSrc
     */
    public String getImageSrc() {
        return this.imageSrc;
    }

    /**
     * @param mageSrc the mageSrc to set
     */
    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    /**
     * @return the styleClass
     */
    public String getStyleClass() {
        return this.styleClass;
    }

    /**
     * @param styleClass the styleClass to set
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }



}

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A single HTML text control.
 *
 *
 */
@Deprecated
public class FileControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 8778523411471162513L;

    // logger
    private static Log LOG = LogFactory.getLog(FileControlDefinition.class);

    public FileControlDefinition() {
        LOG.debug("creating new FileControlDefinition");
        this.type = ControlDefinitionType.FILE;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isText()
     */
    public boolean isFile() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "FileControlDefinition";
    }
}



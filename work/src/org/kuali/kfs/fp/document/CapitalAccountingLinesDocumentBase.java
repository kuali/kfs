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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;

/**
 * class which defines behavior common for capital accounting lines.
 */
 public class CapitalAccountingLinesDocumentBase extends CapitalAssetInformationDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAccountingLinesDocumentBase.class);

    //transient objects
    protected transient boolean capitalAccountingLinesExist = false;
    protected transient List<CapitalAccountingLines> capitalAccountingLines;

    /**
     * Constructs a CapitalAssetInformationDocumentBase
     */
    public CapitalAccountingLinesDocumentBase() {
        super();
        capitalAccountingLines = new ArrayList<CapitalAccountingLines>();
    }

    /**
     * Gets the capitalAccountingLines attribute.
     *
     * @return Returns the capitalAccountingLines
     */
    public List<CapitalAccountingLines> getCapitalAccountingLines() {
        if ( capitalAccountingLines == null ) {
            capitalAccountingLines = new ArrayList<CapitalAccountingLines>();
        }
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
     * Gets the capitalAccountingLinesExist attribute.
     *
     * @return
     */
    public boolean isCapitalAccountingLinesExist() {
        return capitalAccountingLinesExist;
    }

    /**
     * Sets the capitalAccountingLinesExist attribute.
     *
     * @param capitalAccountingLinesExist
     */
    public void setCapitalAccountingLinesExist(boolean capitalAccountingLinesExist) {
        this.capitalAccountingLinesExist = capitalAccountingLinesExist;
    }
}

/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

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
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;

/**
 * This class represents a pair of origin entries to carry forward encumbrance.
 */
public class OriginEntryOffsetPair {
    private OriginEntryFull entry;
    private OriginEntryFull offset;
    private boolean fatalErrorFlag;

    /**
     * @return Returns the fatalErrorFlag.
     */
    public boolean isFatalErrorFlag() {
        return fatalErrorFlag;
    }

    /**
     * @param fatalErrorFlag The fatalErrorFlag to set.
     */
    public void setFatalErrorFlag(boolean fatalErrorFlag) {
        this.fatalErrorFlag = fatalErrorFlag;
    }

    /**
     * @return Returns the entry.
     */
    public OriginEntryFull getEntry() {
        return entry;
    }

    /**
     * @param entry The entry to set.
     */
    public void setEntry(OriginEntryFull entry) {
        this.entry = entry;
    }

    /**
     * @return Returns the offset.
     */
    public OriginEntryFull getOffset() {
        return offset;
    }

    /**
     * @param offset The offset to set.
     */
    public void setOffset(OriginEntryFull offset) {
        this.offset = offset;
    }


}

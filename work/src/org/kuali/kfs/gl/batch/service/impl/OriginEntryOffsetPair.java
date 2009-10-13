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

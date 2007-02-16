/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.lookup.keyvalues;

import java.util.Comparator;

import org.kuali.module.gl.bo.OriginEntryGroup;

public class OEGIdComparator implements Comparator {

    public OEGIdComparator() {
    }

    public int compare(Object c1, Object c2) {

        OriginEntryGroup oeg1 = (OriginEntryGroup) c1;
        OriginEntryGroup oeg2 = (OriginEntryGroup) c2;

        return oeg2.getId().compareTo(oeg1.getId());
    }

}

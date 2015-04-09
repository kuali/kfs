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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;

public class OriginEntryGroupForTesting extends OriginEntryGroup {

    @Override
    public boolean equals(Object otherOriginEntryGroup) {
        return ObjectUtil.equals(this, otherOriginEntryGroup, this.getKeyValueList());
    }

    public Map getKeyValueMap() {
        return ObjectUtil.buildPropertyMap(this, this.getKeyValueList());
    }

    public List<String> getKeyValueList() {
        List<String> keyValueList = new ArrayList<String>();
        keyValueList.add(KFSPropertyConstants.SOURCE_CODE);
        keyValueList.add(KFSPropertyConstants.PROCESS);
        keyValueList.add(KFSPropertyConstants.VALID);
        keyValueList.add(KFSPropertyConstants.SCRUB);
        return keyValueList;
    }
}

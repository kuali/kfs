/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service;

import java.util.Collection;
import java.util.Iterator;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.labor.bo.LaborOriginEntry;

public interface LaborOriginEntryService {

    Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group);

    Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> postingGroups);

    Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup validGroup, boolean isConsolidated);
}
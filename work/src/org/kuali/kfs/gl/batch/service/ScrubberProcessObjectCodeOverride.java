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
package org.kuali.module.gl.service;

/**
 * Provides capability for institutions to override the originEntryObjectCode.
 */
public interface ScrubberProcessObjectCodeOverride {

    /**
     * Potentially overrides the object code for origin entries the scrubber generates
     * 
     * @param originEntryObjectLevelCode the current level code of the object code that could potentially be overriden
     * @param originEntryObjectCode current originEntryObjectCode
     * @return the overriden object code, or the same object code if overriding wasn't necessary
     */
    public String getOriginEntryObjectCode(String originEntryObjectLevelCode, String originEntryObjectCode);

}

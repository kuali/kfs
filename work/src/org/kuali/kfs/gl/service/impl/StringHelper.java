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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.gl.ObjectHelper;

/**
 * Provides helper methods related to evaluating Strings 
 */
public class StringHelper extends ObjectHelper {

    /**
     * This method returns true if String object is empty and NOT null 
     * 
     * @param s evaluated String
     * @return true if String object is empty and NOT null
     */
    static public boolean isEmpty(String s) {
        return (s != null) && "".equals(s);
    }

    /**
     * This method returns true if String object is null or empty
     * @param s evaluated String
     * @return true if String object is null or empty
     */
    static public boolean isNullOrEmpty(String s) {
        return (s == null) || isEmpty(s);
    }
}

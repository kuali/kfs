/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject;

/**
 * Interface for BO instances that may be designated as "primary", e.g., within a collection.
 */
public interface Primaryable {

    /**
     * @return whether this BO instance is the primary one within a collection. This is normally exclusive of any other BOs in the
     *         same collection.
     */
    public boolean isPrimary();
}

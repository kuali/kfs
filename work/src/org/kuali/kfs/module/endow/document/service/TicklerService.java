/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.Tickler;

/**
 * Provides Tickler related methods like retrieving active ticklers for a given Security.
 */
public interface TicklerService {

    /**
     * Gets the active {@link Tickler}s for a given {@link org.kuali.kfs.module.endow.businessobject.Security}.
     * 
     * @param securityId the id of the Security for which we want to retrieve the {@link Tickler}s
     * @return the active ticklers for the given {@link org.kuali.kfs.module.endow.businessobject.Security}
     */
    List<Tickler> getSecurityActiveTicklers(String securityId);

}

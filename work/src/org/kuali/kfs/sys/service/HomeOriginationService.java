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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.HomeOrigination;

/**
 * This interface defines methods that a HomeOrigination service implementation must provide.
 */
public interface HomeOriginationService {

    /**
     * Retrieves a HomeOrigination object. Currently, there is only a single, unique HomeOriginationCode record in the database.
     * 
     * @return A HomeOrigination object.
     * @throws Exception
     */
    public HomeOrigination getHomeOrigination();

}

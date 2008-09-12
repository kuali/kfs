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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.PostalCode;

public interface PostalCodeService {

    /**
     * get the postal zip code object based on the given zip code and default country code. The default country code is set up in
     * the system.
     * 
     * @param postalZipCode the given zip code
     * @return the postal zip code object with the given zip code and default country code.
     */
    PostalCode getByPrimaryId(String postalZipCode);

    /**
     * get the postal zip code object based on the given zip code and country code
     * 
     * @param postalCountryCode the given country code
     * @param postalZipCode the given zip code
     * @return the postal zip code object with the given zip code and country code.
     */
    PostalCode getByPrimaryId(String postalCountryCode, String postalZipCode);
}

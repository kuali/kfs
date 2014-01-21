/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.dataaccess;

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;

/**
 * This is the data access interface for Travelers.
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public interface TravelerDao {

    /**
     * Try to find {@link TravelerDetail} instances of employees. Employees have a <code>travelerTypeCode</code>
     * for employees. This means they also have valid employment information.
     *
     * @return {@link Collection} of {@linK TravelerDetail} instances
     */
    Collection<AccountsReceivableCustomer> findCustomersBy(final Map<String, String> criteria);

}

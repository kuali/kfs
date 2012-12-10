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
package org.kuali.kfs.fp.document.dataaccess;

import java.sql.Date;
import java.util.Collection;

import org.kuali.kfs.fp.businessobject.TravelMileageRate;


/**
 * The data access interface retrieving mileage rate objects.
 */
public interface TravelMileageRateDao {

    /**
     * Retrieves a list of TravelMileageRate objects whose effective dates are before or equal to the given effectiveDate and the
     * greatest of effective dates before the date.
     *
     * @param effectiveDate
     * @return
     */
    public Collection<TravelMileageRate> retrieveMostEffectiveMileageRates(Date effectiveDate);
}

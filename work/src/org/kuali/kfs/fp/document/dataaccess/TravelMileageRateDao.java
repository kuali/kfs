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

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
package org.kuali.kfs.sys.service;

import java.util.Date;

import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * This is a timeDateService that allows tests to specify the date/time they need to run. Set the currentDate property in this class
 * before running your code under test and dateTimeService.getCurrentDate() will return the one you specify instead of the current
 * date.
 */
public interface ConfigurableDateService extends DateTimeService {
    public void setCurrentDate(Date currentDate);
}

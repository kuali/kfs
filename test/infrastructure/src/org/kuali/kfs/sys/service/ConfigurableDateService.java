/*
 * Copyright 2007-2008 The Kuali Foundation
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

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
package org.kuali.module.chart.dao;


// this provides an interface for implementing a callback
// an "action" object for each class involved in 
// fiscal year makers can be created at run time and placed
// on the copy order list dictated by referential integrity
public interface FiscalYearMakersCopyAction {

    public void copyMethod(Integer BaseYear, boolean replaceMode);
}

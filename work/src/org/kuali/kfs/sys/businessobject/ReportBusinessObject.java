/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;

/**
 * Interface to identify business objects which are used as reporting data objects and sets
 * the necessary codes without the full object.  There codes could potentially be dropped
 * when the Business Object was refresh (ex: Access Security)
 */
public interface ReportBusinessObject  {

     /**
     * Refresh the non updatebale for the business object; but additionally perform 
     * logic to ensure if specific attributes were not removed due to it stored as non-persistable
     * object (but yet used in the BO as POJO without the primary keys)
     */
    void refreshNonUpdateableForReport();
}

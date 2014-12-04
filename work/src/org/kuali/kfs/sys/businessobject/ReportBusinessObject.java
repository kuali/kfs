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

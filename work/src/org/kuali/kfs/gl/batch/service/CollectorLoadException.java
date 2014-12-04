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
package org.kuali.kfs.gl.batch.service;

import java.util.List;

/**
 * An exception that occurs during the loading and parsing of a Collector file
 */
public class CollectorLoadException extends RuntimeException {
    private List errors;

    /**
     * Constructs a CollectorLoadException instance
     * @param errors a List of errors encountered while loading and parsing the file
     */
    public CollectorLoadException(List errors) {
        this.errors = errors;
    }

    /**
     * Returns the specific load/parse errors encountered that caused this exception 
     * 
     * @return a List of errors
     */
    public List getErrors() {
        return errors;
    }
}

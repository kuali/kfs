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
package org.kuali.kfs.gl.batch.service.impl.exception;

/**
 * Represents an exception, often occurring in batch jobs, that should be reported
 * but which shouldn't end the process
 */
public class NonFatalErrorException extends Exception {
    /**
     * Constructs a NonFatalErrorException instance
     */
    public NonFatalErrorException() {
        super();
    }

    /**
     * Constructs a NonFatalErrorException instance
     * @param message the message this exception should use to report itself in the logs
     * @param cause the original problem
     */
    public NonFatalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a NonFatalErrorException instance
     * @param message the message this exception should use to report itself in the logs
     */
    public NonFatalErrorException(String message) {
        super(message);
    }

    /**
     * Constructs a NonFatalErrorException instance
     * @param cause the original problem
     */
    public NonFatalErrorException(Throwable cause) {
        super(cause);
    }
}

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
package org.kuali.kfs.module.tem.exception;

/**
 * Exception that is thrown when a piece of the <code>UploadParser</code> fails.
 * 
 * @see org.kuali.kfs.module.tem.util.UploadParser
 */
public class UploadParserException extends RuntimeException {

    private String errorKey;
    private String[] errorParameters;

    /**
     * Constructs an UploadParserException instance.
     * 
     * @param message error message
     */
    public UploadParserException(String message) {
        super(message);
    }

    /**
     * Constructs an UploadParserException instance.
     * 
     * @param message error message
     * @param errorKey key to an error message
     * @param errorParameters error message parameters
     */
    public UploadParserException(String message, String errorKey, String... errorParameters) {
        super(message);
        this.errorKey = errorKey;
        this.errorParameters = errorParameters;
    }

    /**
     * Gets the errorKey attribute.
     * 
     * @return Returns the errorKey.
     */
    public String getErrorKey() {
        return errorKey;
    }

    /**
     * Gets the errorParameters attribute.
     * 
     * @return Returns the errorParameters.
     */
    public String[] getErrorParameters() {
        return errorParameters;
    }
    
}

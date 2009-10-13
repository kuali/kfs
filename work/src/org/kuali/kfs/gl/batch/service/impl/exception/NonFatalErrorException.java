/*
 * Copyright 2007 The Kuali Foundation
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

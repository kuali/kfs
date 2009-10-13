/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject;

/**
 * A class that holds an error message that would have been encountered during a GL batch job
 */
public class ScrubberMessage {
    /**
     * To mark Fatal errors, ones that should abend a batch process
     */
    public static int TYPE_FATAL = 1;
    /**
     * To mark warning errors, ones that should simply be logged but not abend the process
     */
    public static int TYPE_WARNING = 0;

    private String message;
    private int type;

    /**
     * Constructs a ScrubberMessage instance
     * @param m the message
     * @param t the type of message
     */
    public ScrubberMessage(String m, int t) {
        message = m;
        type = t;
    }

    /**
     * Returns the message
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return message;
    }

    /**
     * Returns the error message of this object
     * 
     * @return the error message held by this object
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message for this object
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the error type for this object 
     * 
     * @return the error type of this object
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the error type for this object
     * 
     * @param type an error type to set
     */
    public void setType(int type) {
        this.type = type;
    }


}

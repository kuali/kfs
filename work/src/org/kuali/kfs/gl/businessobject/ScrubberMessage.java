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

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
package org.kuali.kfs.sys;


/**
 * This class represents an error message (severity type and actual message).
 */
public class Message {
    public static final int TYPE_FATAL = 1;
    public static final int TYPE_WARNING = 0;

    private String message;
    private int type;

    public Message(String m, int t, Object... args) {
        message = String.format(m, args);
        type = t;
    }
    
    public Message(String m, int t) {
        message = m;
        type = t;
    }

    public String toString() {
        return message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

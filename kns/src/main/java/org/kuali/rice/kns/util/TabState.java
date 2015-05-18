/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import java.io.Serializable;

/**
 * This class is used to keep tab state whether it is open or closed.
 * 
 * 
 */

public class TabState implements Serializable {

    private static final long serialVersionUID = -628061041191967501L;
    private boolean open;

    /**
     * simple boolean getter
     * 
     * @return
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * boolean setter
     * 
     * @param open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

}

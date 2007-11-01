/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.pdp.xml.impl;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

public class TagStack {
    private Stack tags = new Stack();

    public TagStack() {
        super();
    }

    public void push(String name) {
        tags.add(name);
    }

    public void pop() throws EmptyStackException {
        tags.pop();
    }

    public String peek() throws EmptyStackException {
        return (String) tags.peek();
    }

    public boolean empty() {
        return (tags.size() == 0);
    }

    public String getTagPath() {
        StringBuffer name = new StringBuffer();
        Iterator i = tags.iterator();
        while (i.hasNext()) {
            if (name.length() > 0) {
                name.append("/");
            }
            name.append(i.next());
        }
        return name.toString();
    }
}

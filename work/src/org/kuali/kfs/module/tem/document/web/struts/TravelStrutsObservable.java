/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;

/**
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelStrutsObservable extends Observable {
    public Map<String, List<Observer>> observers;
 
    /**
     * deprecating this since the best practice is to use Spring
     */
    @Override
    @Deprecated
    public void addObserver(final Observer observer) {      
        super.addObserver(observer);
    }

    @Override
    public void notifyObservers(final Object arg) {
        TravelMvcWrapperBean wrapper = null;
        if (arg instanceof TravelMvcWrapperBean) {
            wrapper = (TravelMvcWrapperBean) arg;
        } 
        else if (arg instanceof Object[]) {
            final Object[] args = (Object[]) arg;
            if (args != null && args.length > 0 
                && args[0] instanceof TravelMvcWrapperBean) {
                wrapper = (TravelMvcWrapperBean) args[0];
            }
        }

        final String eventName = wrapper.getMethodToCall();
        for (final Observer observer : getObservers().get(eventName)) {
            observer.update(this, arg);
        }
        clearChanged();
    }

    /**
     * Gets the observers attribute.
     * 
     * @return Returns the observers.
     */
    public Map<String, List<Observer>> getObservers() {
        return observers;
    }

    /**
     * Sets the observers attribute value.
     * 
     * @param observers The observers to set.
     */
    public void setObservers(final Map<String,List<Observer>> observers) {
        this.observers = observers;
    }
}
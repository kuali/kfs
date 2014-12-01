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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;

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

    @SuppressWarnings("null")
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

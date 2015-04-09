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
package org.kuali.kfs.sys.service;

import java.util.Map;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.web.struts.ElectronicFundTransferForm;

/**
 * This represents one "controller" action performed by the ElectronicFundTransfer Action 
 */
public interface ElectronicFundTransferActionHelper {
    /**
     * Performs a web controller action
     * @param form the form that the action is to perform on
     * @param mapping the action mappings to return to
     * @param paramMap the map of parameters from the request
     * @param basePath the basePath of the request
     * @return the ActionForward that represents where the controller should next redirect to
     */
    public abstract ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map parameterMap, String basePath);
}

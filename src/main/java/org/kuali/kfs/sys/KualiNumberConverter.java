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
package org.kuali.kfs.sys;

import org.directwebremoting.ConversionException;
import org.directwebremoting.extend.Converter;
import org.directwebremoting.extend.ConverterManager;
import org.directwebremoting.extend.InboundVariable;
import org.directwebremoting.extend.OutboundContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

import uk.ltd.getahead.dwr.compat.BaseV10Converter;

/**
 * Converter for all Kuali Numbers (KualiDecimal & KualiInteger)
 * 
 * @see org.kuali.rice.core.api.util.type.KualiDecimal
 * @see org.kuali.rice.core.api.util.type.KualiInteger
 */
public class KualiNumberConverter extends BaseV10Converter implements Converter {
    /**
     * @see uk.ltd.getahead.dwr.Converter#init(uk.ltd.getahead.dwr.DefaultConfiguration)
     */
    public void setConverterManager(ConverterManager config) {
    }

    /**
     * @see uk.ltd.getahead.dwr.Converter#convertInbound(java.lang.Class, java.util.List, uk.ltd.getahead.dwr.InboundVariable,
     *      uk.ltd.getahead.dwr.InboundContext)
     */
    public Object convertInbound(Class paramType, InboundVariable iv) throws ConversionException {
        String value = iv.getValue();
        try {
            if (paramType == KualiDecimal.class) {
                return new KualiDecimal(value.trim());
            }

            if (paramType == KualiInteger.class) {
                return new KualiInteger(value.trim());
            }
            String message = MessageBuilder.buildMessage("BigNumberConverter.NonPrimitive", paramType.getName()).getMessage();
            throw new ConversionException(paramType,message);
        }
        catch (NumberFormatException ex) {
            String message = MessageBuilder.buildMessage("BigNumberConverter.FormatError", paramType.getName()).getMessage();
            throw new ConversionException(paramType, message, ex);
         }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ltd.getahead.dwr.Converter#convertOutbound(java.lang.Object, java.lang.String, uk.ltd.getahead.dwr.OutboundContext)
     */
    public String convertOutbound(Object object, String varname, OutboundContext outctx) {
        return "var " + varname + "=" + object.toString() + ';'; //$NON-NLS-1$ //$NON-NLS-2$
    }
}

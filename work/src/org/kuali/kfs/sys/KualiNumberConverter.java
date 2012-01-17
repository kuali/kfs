/*
 * Copyright 2008 The Kuali Foundation
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

/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.web.dwr.converter;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;

import uk.ltd.getahead.dwr.ConversionException;
import uk.ltd.getahead.dwr.Converter;
import uk.ltd.getahead.dwr.ConverterManager;
import uk.ltd.getahead.dwr.InboundContext;
import uk.ltd.getahead.dwr.InboundVariable;
import uk.ltd.getahead.dwr.Messages;
import uk.ltd.getahead.dwr.OutboundContext;
import uk.ltd.getahead.dwr.compat.BaseV10Converter;

/**
 * Converter for all Kuali Numbers (KualiDecimal & KualiInteger)
 * 
 * @see org.kuali.core.util.KualiDecimal
 * @see org.kuali.core.util.KualiInteger
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
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws ConversionException {
        String value = iv.getValue();
        try {
            if (paramType == KualiDecimal.class) {
                return new KualiDecimal(value.trim());
            }

            if (paramType == KualiInteger.class) {
                return new KualiInteger(value.trim());
            }

            throw new ConversionException(Messages.getString("BigNumberConverter.NonPrimitive", paramType.getName())); //$NON-NLS-1$
        }
        catch (NumberFormatException ex) {
            throw new ConversionException(Messages.getString("BigNumberConverter.FormatError", value, paramType.getName()), ex); //$NON-NLS-1$
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

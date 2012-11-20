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
package org.kuali.kfs.module.tem.report;

import net.sf.jasperreports.engine.design.*;

/**
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class RString {

    private String str;
    private RTextStyle style;

    public RString(final String str, final RTextStyle style) {
        this.str = str;
        this.style = style;
    }

    /**
     *
     */
    protected void setupTextElement(final JRDesignTextElement textElement) {
        textElement.setFontName(style.getFontName());
        textElement.setFontSize(style.getFontSize());
        textElement.setBold(style.isBold());
        textElement.setItalic(style.isItalic());
    }


    /**
     *
     */
    public JRDesignTextField toTextField() {
        return toTextField(String.class);
    }

    /**
     *
     */
    public JRDesignTextField toTextField(final Class valueClass) {
        final JRDesignTextField retval = new JRDesignTextField();
        setupTextElement(retval);

        final JRDesignExpression expression = new JRDesignExpression();
        expression.setText(str);
        expression.setValueClass(valueClass);
        retval.setExpression(expression);
        
        return retval;
    }

    /**
     *
     */
    public JRDesignStaticText toStaticText() {
        final JRDesignStaticText retval = new JRDesignStaticText();
        setupTextElement(retval);
        retval.setText(str);
        return retval;
    }
}
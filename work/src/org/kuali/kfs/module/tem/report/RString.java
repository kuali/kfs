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
package org.kuali.kfs.module.tem.report;

import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;

public class RString {
    private String str;
    private RTextStyle style;

    public RString(final String str, final RTextStyle style) {
        this.str = str;
        this.style = style;
    }

    protected void setupTextElement(final JRDesignTextElement textElement) {
        textElement.setFontName(style.getFontName());
        textElement.setFontSize(style.getFontSize());
        textElement.setBold(style.isBold());
        textElement.setItalic(style.isItalic());
    }

    public JRDesignTextField toTextField() {
        return toTextField(String.class);
    }

    public JRDesignTextField toTextField(final Class valueClass) {
        final JRDesignTextField retval = new JRDesignTextField();
        setupTextElement(retval);

        final JRDesignExpression expression = new JRDesignExpression();
        expression.setText(str);
        expression.setValueClass(valueClass);
        retval.setExpression(expression);

        return retval;
    }

    public JRDesignStaticText toStaticText() {
        final JRDesignStaticText retval = new JRDesignStaticText();
        setupTextElement(retval);
        retval.setText(str);
        return retval;
    }
}

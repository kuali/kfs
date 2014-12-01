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
package org.kuali.kfs.module.tem.report.util;

import java.awt.image.BufferedImage;

import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class BarcodeHelper {
    // Bar codes
    public static final String BARCODE_39 = "39";
    public static final String BARCODE_128 = "128";
    public static final int DPI = 150;

    private static final String MIME = "image/gif";
    private static final String BARCODE_GIF = "barcode.gif";

    public BufferedImage generateBarcodeImage(String documentNumber){
        String barcodeStyle = getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, TemConstants.TravelReimbursementParameters.BARCODE_STYLE);
        BufferedImage image=null;
        if (barcodeStyle.equals(BARCODE_39)) {
            Code39Bean bean = new Code39Bean();
            bean.setModuleWidth(UnitConv.in2mm(1.0f / DPI));
            bean.setWideFactor(3);
            bean.doQuietZone(false);
            image= getBufferedBarcodeImage(documentNumber, bean);

        }else if (barcodeStyle.equals(BARCODE_128)) {
            Code128Bean bean = new Code128Bean();
            bean.setModuleWidth(UnitConv.in2mm(1.0f / DPI));
            bean.doQuietZone(false);
            image= getBufferedBarcodeImage(documentNumber, bean);
        }
        return image;
    }

    private synchronized BufferedImage getBufferedBarcodeImage(String documentNumber, BarcodeGenerator bean){
        try {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(DPI, BufferedImage.TYPE_BYTE_BINARY,false ,0 );
            bean.generateBarcode(canvas, documentNumber);
            BufferedImage image=canvas.getBufferedImage();
            canvas.finish();
            return image;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    public static ConfigurationService getConfigurationService() {
        return SpringContext.getBean(ConfigurationService.class);
    }
}

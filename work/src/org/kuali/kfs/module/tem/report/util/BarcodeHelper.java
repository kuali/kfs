/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.report.util;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import java.awt.image.BufferedImage;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;

public class BarcodeHelper {    
    // Bar codes
    public static final String BARCODE_39 = "39";
    public static final String BARCODE_128 = "128";
    public static final int DPI = 150;
    
    private static final String MIME = "image/gif";
    private static final String BARCODE_GIF = "barcode.gif";
   
    public BufferedImage generateBarcodeImage(String documentNumber){
        String barcodeStyle = getParameterService().getParameterValue(PARAM_NAMESPACE, TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE, TemConstants.TravelParameters.TEM_BARCODE_STYLE);
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

    public static KualiConfigurationService getKualiConfigurationService() {
        return SpringContext.getBean(KualiConfigurationService.class);
    }
}

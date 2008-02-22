package org.kuali.kfs.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestModularization extends KualiTestBase {
    private static final String BASE_SPRING_FILESET = "SpringBeans.xml,SpringDataSourceBeans.xml,SpringRiceBeans.xml,org/kuali/kfs/KualiSpringBeansKfs.xml,org/kuali/module/chart/KualiSpringBeansChart.xml,org/kuali/module/financial/KualiSpringBeansFinancial.xml,org/kuali/module/gl/KualiSpringBeansGl.xml,org/kuali/module/vendor/KualiSpringBeansVendor.xml,org/kuali/module/pdp/KualiSpringBeansPdp.xml";
    private static final Map<String,String> OPTIONAL_MODULE_TEST_FILESETS = new HashMap<String,String>();
    static {
        OPTIONAL_MODULE_TEST_FILESETS.put("AR", BASE_SPRING_FILESET + ",org/kuali/module/ar/KualiSpringBeansAr.xml");
        OPTIONAL_MODULE_TEST_FILESETS.put("BC", BASE_SPRING_FILESET + ",org/kuali/module/budget/KualiSpringBeansBudget.xml");
        OPTIONAL_MODULE_TEST_FILESETS.put("CM", BASE_SPRING_FILESET + ",org/kuali/module/cams/KualiSpringBeansCams.xml");
        OPTIONAL_MODULE_TEST_FILESETS.put("CG", BASE_SPRING_FILESET + ",org/kuali/module/cg/KualiSpringBeansCg.xml");
        OPTIONAL_MODULE_TEST_FILESETS.put("ER", BASE_SPRING_FILESET + ",org/kuali/module/effort/KualiSpringBeansEffort.xml");
        OPTIONAL_MODULE_TEST_FILESETS.put("RA", BASE_SPRING_FILESET + ",org/kuali/module/kra/KualiSpringBeansKra.xml");
        OPTIONAL_MODULE_TEST_FILESETS.put("LD", BASE_SPRING_FILESET + ",org/kuali/module/labor/KualiSpringBeansLabor.xml");
        OPTIONAL_MODULE_TEST_FILESETS.put("PA", BASE_SPRING_FILESET + ",org/kuali/module/purap/KualiSpringBeansPurap.xml");
    }    
    
    public void testSpring() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in Spring configuration:\n");
        for (String moduleName : OPTIONAL_MODULE_TEST_FILESETS.keySet()) {
            testSucceeded = testSucceeded & testOptionalModuleSpringConfiguration(moduleName, OPTIONAL_MODULE_TEST_FILESETS.get(moduleName), errorMessage);
        }
        assertTrue(errorMessage.toString(), testSucceeded);
        // TODO will we lose things like testing the batch config and other from not using the initialize method on SpringContext
    }
    
    public void testOjb() throws Exception {
        // TODO will the testSpring method take care of this?
        // if we do have to do this, i think it will mean loading one file and looking for refs to the other.
        // e.g. load OJB-repository-ar.xml, and search for the following strings: org.kuali.module.budget, org.kuali.module.cams, ...
        // report an effort for that module if you find those - not necessary to report specifics, but if that's easy great
    }
    
    private boolean testOptionalModuleSpringConfiguration(String moduleName, String fileset, StringBuffer errorMessage) {
        try {
            new ClassPathXmlApplicationContext(fileset.split(","));
            return true;
        }
        catch (Exception e) {
            errorMessage.append(moduleName).append(": ").append(e.getMessage());
            return false;
        }
    }
}

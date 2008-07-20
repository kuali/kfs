package org.kuali.kfs.sys.context;

import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.kuali.core.authorization.KualiModuleAuthorizerBase;
import org.kuali.core.service.KualiModuleService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.resourceloader.ContextClassLoaderBinder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ModularizationTest extends KualiTestBase {
    private static final String BASE_SPRING_FILESET = "SpringBeans.xml,SpringDataSourceBeans.xml,SpringRiceBeans.xml,org/kuali/kfs/sys/KualiSpringBeansKfs.xml,org/kuali/kfs/integration/SpringBeansModules.xml,org/kuali/kfs/coa/KualiSpringBeansChart.xml,org/kuali/kfs/fp/KualiSpringBeansFinancial.xml,org/kuali/kfs/gl/KualiSpringBeansGl.xml,org/kuali/kfs/pdp/KualiSpringBeansPdp.xml,org/kuali/kfs/vnd/KualiSpringBeansVendor.xml";
    private static final Set<String> OPTIONAL_MODULE_IDS = new HashSet<String>();
    static {
        OPTIONAL_MODULE_IDS.add("ar");
        OPTIONAL_MODULE_IDS.add("bc");
        OPTIONAL_MODULE_IDS.add("cab");
        OPTIONAL_MODULE_IDS.add("cam");
        OPTIONAL_MODULE_IDS.add("cg");
        OPTIONAL_MODULE_IDS.add("ec");
        OPTIONAL_MODULE_IDS.add("ld");
        OPTIONAL_MODULE_IDS.add("purap");
    }
    private static final Set<String> SYSTEM_MODULE_IDS = new HashSet<String>();
    static {
        SYSTEM_MODULE_IDS.add("coa");
        SYSTEM_MODULE_IDS.add("fp");
        SYSTEM_MODULE_IDS.add("gl");
        SYSTEM_MODULE_IDS.add("pdp");
        SYSTEM_MODULE_IDS.add("sys");
        SYSTEM_MODULE_IDS.add("vnd");
    }
    private KualiModuleService moduleService;

//    public void testSpring() throws Exception {
//        // switch to a different context classloader context so that we don't blow away our existing configuration
//        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
//        binder.bind(new URLClassLoader(new URL[0]));
//        try {
//            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(BASE_SPRING_FILESET.split(","));
//            moduleService = (KualiModuleService) context.getBean("kualiModuleService");
//            context.close();
//            boolean testSucceeded = true;
//            StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in Spring configuration:");
//            for (String moduleId : OPTIONAL_MODULE_IDS) {
//                testSucceeded = testSucceeded & testOptionalModuleSpringConfiguration(moduleId, errorMessage);
//            }
//            System.out.print(errorMessage.toString());
//            assertTrue(errorMessage.toString(), testSucceeded);
//        }
//        finally {
//            binder.unbind();
//        }
//    }

    private boolean testOptionalModuleSpringConfiguration(String moduleId, StringBuffer errorMessage) {
        ClassPathXmlApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext(new StringBuffer(BASE_SPRING_FILESET).append(",org/kuali/kfs/module/").append(moduleId).append("/spring-").append(moduleId).append(".xml").toString().split(","));
            return true;
        }
        catch (Exception e) {
            errorMessage.append("\n").append(moduleId).append("\n\t").append(e.getMessage());
            return false;
        }
        finally {
            try {
                if (context != null) {
                    context.close();
                }
            }
            catch (Exception e) {
            }
        }
    }

    @ConfigureContext
    public void testOjb() throws Exception {
        moduleService = SpringContext.getBean(KualiModuleService.class);
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in OJB configuration:");
        HashSet<String> allModuleIds = new HashSet();
        for (String moduleId : SYSTEM_MODULE_IDS) {
            allModuleIds.add(moduleId);
        }
        for (String moduleId : OPTIONAL_MODULE_IDS.keySet()) {
            allModuleIds.add(moduleId);
        }
        for (String moduleId : allModuleIds) {
            testSucceeded = testSucceeded & testOptionalModuleOjbConfiguration(moduleId, errorMessage);
        }
        assertTrue(errorMessage.toString(), testSucceeded);
    }

    private boolean testOptionalModuleOjbConfiguration(String moduleId, StringBuffer errorMessage) throws FileNotFoundException {
        boolean testSucceeded = true;
        for (String referencedModuleId : OPTIONAL_MODULE_IDS.keySet()) {
            if (!moduleId.equals(referencedModuleId)) {
                Scanner scanner = null;
                scanner = new Scanner("work/src/" + moduleService.getModule(referencedModuleId).getDatabaseRepositoryFilePaths().iterator().next());
                scanner.useDelimiter(" ");
                int count = 0;
                while (scanner.hasNext()) {
                    if (scanner.next().contains(((KualiModuleAuthorizerBase) moduleService.getModule(referencedModuleId).getModuleAuthorizer()).getPackagePrefixes().iterator().next())) {
                        count++;
                    }
                }
                if (count > 0) {
                    if (testSucceeded) {
                        testSucceeded = false;
                        errorMessage.append("\n").append(moduleId).append(": ");
                    }
                    else {
                        errorMessage.append(", ");
                    }
                    errorMessage.append(count).append(" references to ").append(referencedModuleId);
                }
            }
        }
        return testSucceeded;
    }
}

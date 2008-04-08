package org.kuali.kfs.context;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.kuali.core.authorization.KualiModuleAuthorizerBase;
import org.kuali.core.service.KualiModuleService;
import org.kuali.module.ar.bo.ArUser;
import org.kuali.module.budget.bo.BudgetUser;
import org.kuali.module.cams.bo.CamsUser;
import org.kuali.module.cg.bo.CgUser;
import org.kuali.module.effort.bo.EffortUser;
import org.kuali.module.kra.budget.bo.KraUser;
import org.kuali.module.labor.bo.LaborUser;
import org.kuali.module.purap.bo.PurapUser;
import org.kuali.test.ConfigureContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestModularization extends KualiTestBase {
    private static final String BASE_SPRING_FILESET = "SpringBeans.xml,SpringDataSourceBeans.xml,SpringRiceBeans.xml,org/kuali/kfs/KualiSpringBeansKfs.xml,org/kuali/module/integration/SpringBeansModules.xml,org/kuali/module/chart/KualiSpringBeansChart.xml,org/kuali/module/financial/KualiSpringBeansFinancial.xml,org/kuali/module/gl/KualiSpringBeansGl.xml,org/kuali/module/pdp/KualiSpringBeansPdp.xml,org/kuali/module/vendor/KualiSpringBeansVendor.xml";
    private static final Set<String> OPTIONAL_MODULE_IDS = new HashSet<String>();
    static {
        OPTIONAL_MODULE_IDS.add(ArUser.MODULE_ID);
        OPTIONAL_MODULE_IDS.add(BudgetUser.MODULE_ID);
        OPTIONAL_MODULE_IDS.add(CamsUser.MODULE_ID);
        OPTIONAL_MODULE_IDS.add(CgUser.MODULE_ID);
        OPTIONAL_MODULE_IDS.add(EffortUser.MODULE_ID);
        OPTIONAL_MODULE_IDS.add(KraUser.MODULE_ID);
        OPTIONAL_MODULE_IDS.add(LaborUser.MODULE_ID);
        OPTIONAL_MODULE_IDS.add(PurapUser.MODULE_ID);
    }
    private KualiModuleService moduleService;

    public void testSpring() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(BASE_SPRING_FILESET.split(","));
        moduleService = SpringContext.getBean(KualiModuleService.class);
        context.close();
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in Spring configuration:");
        for (String moduleId : OPTIONAL_MODULE_IDS) {
            System.out.println("CHECK");
            testSucceeded = testSucceeded & testOptionalModuleSpringConfiguration(moduleId, errorMessage);
        }
        System.out.print(errorMessage.toString());
        assertTrue(errorMessage.toString(), testSucceeded);
    }
    
    private boolean testOptionalModuleSpringConfiguration(String moduleId, StringBuffer errorMessage) {
        ClassPathXmlApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext(new StringBuffer(BASE_SPRING_FILESET).append(",org/kuali/module/").append(moduleId).append("/KualiSpringBeans").append(moduleId.substring(0, 1).toUpperCase()).append(moduleId.substring(1)).append(".xml").toString().split(","));
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
        for (String moduleId : OPTIONAL_MODULE_IDS) {
            testSucceeded = testSucceeded & testOptionalModuleOjbConfiguration(moduleId, errorMessage);
        }
        assertTrue(errorMessage.toString(), testSucceeded);
    }

    private boolean testOptionalModuleOjbConfiguration(String moduleId, StringBuffer errorMessage) throws FileNotFoundException {
        boolean testSucceeded = true;
        for (String referencedModuleId : OPTIONAL_MODULE_IDS) {
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
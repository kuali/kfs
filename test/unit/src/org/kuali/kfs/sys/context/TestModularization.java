        package org.kuali.kfs.context;
        
        import java.util.HashMap;
        import java.util.Map;
        import java.util.ArrayList;
        import java.util.Scanner;
        import java.io.File;
        
        import org.kuali.test.ConfigureContext;
        import org.springframework.context.support.ClassPathXmlApplicationContext;
        
        public class TestModularization extends KualiTestBase {
            private static final String BASE_SPRING_FILESET = "SpringBeans.xml,SpringDataSourceBeans.xml,SpringRiceBeans.xml,org/kuali/kfs/KualiSpringBeansKfs.xml,org/kuali/module/integration/SpringBeansModules.xml,org/kuali/module/chart/KualiSpringBeansChart.xml,org/kuali/module/financial/KualiSpringBeansFinancial.xml,org/kuali/module/gl/KualiSpringBeansGl.xml,org/kuali/module/pdp/KualiSpringBeansPdp.xml,org/kuali/module/vendor/KualiSpringBeansVendor.xml";
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
            
            private static final Map<String,File> OPTIONAL_MODULE_OJB_FILESET = new HashMap<String,File>();
            static {
                OPTIONAL_MODULE_OJB_FILESET.put("ar", new File("work/src/org/kuali/module/ar/OJB-repository-ar.xml"));
                OPTIONAL_MODULE_OJB_FILESET.put("budget", new File("work/src/org/kuali/module/budget/OJB-repository-budget.xml"));
                OPTIONAL_MODULE_OJB_FILESET.put("cams", new File("work/src/org/kuali/module/cams/OJB-repository-cams.xml"));
                OPTIONAL_MODULE_OJB_FILESET.put("cg", new File("work/src/org/kuali/module/cg/OJB-repository-cg.xml"));
                OPTIONAL_MODULE_OJB_FILESET.put("effort", new File("work/src/org/kuali/module/effort/OJB-repository-effort.xml"));
                OPTIONAL_MODULE_OJB_FILESET.put("kra", new File("work/src/org/kuali/module/kra/OJB-repository-kra.xml"));
                OPTIONAL_MODULE_OJB_FILESET.put("labor", new File("work/src/org/kuali/module/labor/OJB-repository-labor.xml"));
                OPTIONAL_MODULE_OJB_FILESET.put("purap", new File("work/src/org/kuali/module/purap/OJB-repository-purap.xml"));
            }

            private static final Map<String,String> OPTIONAL_MODULE_OJB_PACKAGE = new HashMap<String,String>();
            static {
                OPTIONAL_MODULE_OJB_PACKAGE.put("ar","org.kuali.module.ar");
                OPTIONAL_MODULE_OJB_PACKAGE.put("budget","org.kuali.module.budget");
                OPTIONAL_MODULE_OJB_PACKAGE.put("cams","org.kuali.module.cams");
                OPTIONAL_MODULE_OJB_PACKAGE.put("cg","org.kuali.module.cg");
                OPTIONAL_MODULE_OJB_PACKAGE.put("effort","org.kuali.module.effort");
                OPTIONAL_MODULE_OJB_PACKAGE.put("kra","org.kuali.module.kra");
                OPTIONAL_MODULE_OJB_PACKAGE.put("labor","org.kuali.module.labor");
                OPTIONAL_MODULE_OJB_PACKAGE.put("purap","org.kuali.module.purap");
            }

            public void testSpring() throws Exception {
                boolean testSucceeded = true;
                StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in Spring configuration: \n");
                for (String moduleName : OPTIONAL_MODULE_TEST_FILESETS.keySet()) {
                    testSucceeded = testSucceeded & testOptionalModuleSpringConfiguration(moduleName, OPTIONAL_MODULE_TEST_FILESETS.get(moduleName), errorMessage);
                }
                System.out.print(errorMessage.toString());
                assertTrue(errorMessage.toString(), testSucceeded);
                // TODO will we lose things like testing the batch config and other from not using the initialize method on SpringContext
            }
            
            public void testOjb() throws Exception {
                System.out.println("Listing of OJB module dependencies:");
                for(String moduleFile : OPTIONAL_MODULE_OJB_FILESET.keySet()) {
                    System.out.println("Module: " + moduleFile);
                    for(String modulePackage : OPTIONAL_MODULE_OJB_PACKAGE.keySet()) {
                        
                        if(moduleFile != modulePackage) {
                            Scanner scanner = new Scanner(OPTIONAL_MODULE_OJB_FILESET.get(moduleFile));
                            scanner.useDelimiter(" ");
                            int count = 0;
                            while(scanner.hasNext()) {
                                if(scanner.next().contains(OPTIONAL_MODULE_OJB_PACKAGE.get(modulePackage))) {
                                    count++;
                                }
                            }
                            if(count > 0) {
                                System.out.println(count + "- references found to " + modulePackage);
                            }
                        }
                        
                        
                    }
                }
            }
            
            private boolean testOptionalModuleSpringConfiguration(String moduleName, String fileset, StringBuffer errorMessage) {
                ClassPathXmlApplicationContext context = null;
                try {
                    context = new ClassPathXmlApplicationContext(fileset.split(","));
                    return true;
                }
                catch (Exception e) {
                    errorMessage.append("\n").append(moduleName).append("\n\t").append(e.getMessage());
                    return false;
                }
                finally {   
                    try {
                        if (context != null) {
                            context.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
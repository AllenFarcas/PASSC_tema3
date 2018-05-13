import javax.tools.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ClientProxyGenerator {
    private String className;
    private String interfaceName;
    private String name;

    public ClientProxyGenerator(String className, String interfaceName, String name) {
        this.className = className;
        this.interfaceName = interfaceName;
        this.name=name;
    }

    public void generateAndCompile() {
        try {
            Class<?> reflectClass = Class.forName(interfaceName);
            CharSequence str = "String";
            File file= new File ("/Users/allenpianoman/Desktop/PASSC/Tema3/Tema3PASSC/src/"+className+".java");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("import MessageMarshaller.*;\nimport Commons.Address;\nimport Registry.Entry;\nimport RequestReply.*;\n");

            //==================PROXY CLIENT=====================
            printWriter.println("class "+interfaceName+"ClientProxy implements "+interfaceName+",ClientProxy {");
            printWriter.println("\tprivate Requestor req = new Requestor(\""+interfaceName+"ClientProxy\");");
            printWriter.println("\tprivate Marshaller m = new Marshaller();");
            printWriter.println("\tprivate int portNumber;\n\n\tpublic "+interfaceName+"ClientProxy(int portNumber) {");
            printWriter.println("\t\tthis.portNumber = portNumber;\n\t}\n");
            //// Get the objects methods, return type and parameter type
            Method[] classMethods = reflectClass.getDeclaredMethods();
            for(Method method : classMethods) {
                // Get the method name
                String methodName = method.getName();
                // Get the methods return type
                String methodReturnType = method.getReturnType().getTypeName();
                printWriter.print("\tpublic " + methodReturnType + " " + methodName + "(");
                Class<?>[] parameterType = method.getParameterTypes();
                // List parameters for a method
                String methodParameterType;
                String methodArgs="";
                if (parameterType.length == 0) {
                    methodParameterType = "";
                    methodArgs = "";
                } else {
                    int parCount = 0;
                    for (Class<?> parameter : parameterType) {
                        methodParameterType = parameter.getName();
                        if (parCount == 0) {
                            methodArgs += methodParameterType + " $param_" + parCount;
                        } else if (parCount >= 1) {
                            methodArgs += ", " + methodParameterType + " $param_" + parCount;
                        }
                        parCount++;
                    }
                }
                printWriter.println(methodArgs+") {");
                if (parameterType.length == 0) {
                    printWriter.println("\t\tString msgData = \" :" + methodName + "\";");
                } else {
                    int parCount = 0;
                    printWriter.print("\t\tString msgData = ");
                    for (Class<?> parameter : parameterType) {
                        methodParameterType = parameter.getName();
                        if (methodParameterType.contains(str)) {
                            if (parCount == 0) {
                                printWriter.print("$param_" + parCount);
                            } else {
                                printWriter.print("+ \" \" + $param_" + parCount);
                            }
                        } else {
                            if (parCount == 0) {
                                printWriter.print("String.valueOf($param_" + parCount + ")");
                            } else {
                                printWriter.print("+ \" \" + String.valueOf($param_" + parCount + ")");
                            }
                        }
                        parCount++;
                    }
                    printWriter.print("+\":" + methodName + "\";");
                }
                printWriter.println("\n\t\t//scrie in mesaj toti parametri");
                printWriter.println("\t\tMessage msg = new Message(\"" + interfaceName + "ClientProxy\",msgData);");
                printWriter.println("\t\tbyte[] bytes = m.marshal(msg);\n\t\tAddress dest = new Entry(\"127.0.0.1\",portNumber);");
                printWriter.println("\t\t//asteapta rezutatul\n\t\tbytes = req.deliver_and_wait_feedback(dest,bytes);");
                printWriter.println("\t\t//despacheteaza rezultatul\n\t\tMessage answer = m.unmarshal(bytes);");
                printWriter.println("\t\t//rezultatul este convertiti la tipul care este returnat de metoda curenta");
                if (methodReturnType.contains(str)) {
                    printWriter.println("\t\t" + methodReturnType + " $result = answer.data;");
                } else {
                    if (method.getReturnType().isPrimitive()) {
                        if (methodReturnType.equals("int")) {
                            printWriter.println("\t\t" + methodReturnType + " $result = Integer.parseInt(answer.data);");
                        } else if (methodReturnType.equals("float")) {
                            printWriter.println("\t\t" + methodReturnType + " $result = Float.parseFloat(answer.data);");
                        } else if (methodReturnType.equals("double")) {
                            printWriter.println("\t\t" + methodReturnType + " $result = Double.parseDouble(answer.data);");
                        } else if (methodReturnType.equals("boolean")) {
                            printWriter.println("\t\t" + methodReturnType + " $result = Boolean.parseBoolean(answer.data);");
                        } else if (methodReturnType.equals("char")) {
                            printWriter.println("\t\tchar[] charArray = answer.data.toCharArray()");
                            printWriter.println("\t\t" + methodReturnType + " $result = char[0];");
                        } else if (methodReturnType.equals("long")) {
                            printWriter.println("\t\t" + methodReturnType + " $result = Long.parseLong(answer.data);");
                        } else if (methodReturnType.equals("short")) {
                            printWriter.println("\t\t" + methodReturnType + " $result = Short.parseShort(answer.data);");
                        } else if (methodReturnType.equals("byte")) {
                            printWriter.println("\t\t" + methodReturnType + " $result = Byte.parseByte(answer.data);");
                        }
                    }
                }
                printWriter.println("\t\treturn $result;\n\t}\n");
            }
            printWriter.println("}");
            printWriter.close();
            //==================Compiling the file =====================
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            File parentDirectory = file.getParentFile();
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(parentDirectory));
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
            compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
            fileManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}

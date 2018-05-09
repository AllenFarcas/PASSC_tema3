import javax.tools.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ServerProxyGenerator {
    private String className;
    private String interfaceName;

    public ServerProxyGenerator(String className, String interfaceName) {
        this.className = className;
        this.interfaceName = interfaceName;
    }

    public void generateAndCompile(){
        try {
            Class<?> reflectClass = Class.forName(interfaceName);
            System.out.println("Numele interfetei este: "+interfaceName);
            CharSequence str = "String";
            File file= new File ("/Users/allenpianoman/Desktop/PASSC/Tema3/Tema3PASSC/src/"+className+".java");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println("import MessageMarshaller.Message;\nimport Commons.Address;\nimport Registry.Entry;\n" +
                    "import RequestReply.ByteStreamTransformer;\nimport RequestReply.Replyer;\n");


            //==================MESSAGE SERVER=====================
            printWriter.println("class "+interfaceName+"MessageServer extends MessageServer {");
            printWriter.println("\tprivate "+interfaceName+"Impl "+ interfaceName.toLowerCase()+";");
            printWriter.println("\tpublic "+interfaceName+"MessageServer() {}");
            printWriter.println("\tpublic "+interfaceName+"MessageServer("+interfaceName+"Impl "+interfaceName.toLowerCase()+"){");
            printWriter.println("\t\tthis."+interfaceName.toLowerCase()+" = "+interfaceName.toLowerCase()+";\n\t}");
            printWriter.println("\tpublic Message get_answer(Message msg) throws Exception {");
            printWriter.println("\t\tif(msg.sender.equals(\""+interfaceName+"ClientProxy\")) {");
            printWriter.println("\t\t\tSystem.out.println(\""+interfaceName+"ServerProxy analyzing data\");");
            printWriter.println("\t\t\tString [] arrOfStr = msg.data.split(\":\", 5);\n\t\t\tString parameters = arrOfStr[0];");
            printWriter.println("\t\t\tString opcode = arrOfStr[1];\n\t\t\tint opnum = Integer.parseInt(opcode);");
            printWriter.println("\t\t\tString [] arrParam = parameters.split(\" \", 5);\n\t\t\tswitch (opnum){");
            //// Get the objects methods, return type and parameter type
            Method[] classMethods = reflectClass.getDeclaredMethods();
            int i=0;
            //System.out.println("\n===========================\n");
            for(Method method : classMethods) {
                // Get the method name
                String methodName = method.getName();
                System.out.println("Metoda numarul "+i+" este: "+methodName);
                printWriter.println("\t\t\t\t//"+methodName+" "+i+"\n\t\t\t\tcase "+i+": {");
                printWriter.println("\t\t\t\t\tSystem.out.println(\""+interfaceName+"ServerProxy: "+methodName+" method\");");
                // Get the methods return type
                //System.out.println("Method return type: " + method.getReturnType());
                String methodReturnType = method.getReturnType().getTypeName();
                printWriter.println("\t\t\t\t\t"+methodReturnType+" $result;");
                Class<?>[] parameterType = method.getParameterTypes();
                // List parameters for a method
                String methodParameterType;
                String methodArgs="";
                if( parameterType.length==0) {
                    methodParameterType = "";
                    methodArgs="";
                } else {
                    int parCount=0;
                    for(Class<?> parameter : parameterType) {
                        methodParameterType = parameter.getName();
                        if(methodParameterType.contains(str)) {
                            printWriter.println("\t\t\t\t\t"+methodParameterType+" $param_"+parCount+" = arrParam["+parCount+"];");
                        } else {
                            if(parameter.isPrimitive()) {
                                String name="Error";
                                if(parameter.getTypeName().equals("int")){
                                    name = "Integer";
                                } else if (parameter.getTypeName().equals("float")){
                                    name = "Float";
                                } else if (parameter.getTypeName().equals("double")){
                                    name = "Double";
                                } else if (parameter.getTypeName().equals("boolean")){
                                    name = "Boolean";
                                } else if (parameter.getTypeName().equals("char")){
                                    name = "Character";
                                } else if (parameter.getTypeName().equals("long")){
                                    name = "Long";
                                } else if (parameter.getTypeName().equals("short")){
                                    name = "Short";
                                } else if (parameter.getTypeName().equals("byte")){
                                    name = "Byte";
                                }
                                printWriter.println("\t\t\t\t\t"+methodParameterType+" $param_"+parCount+" = "+name+".valueOf(arrParam["+parCount+"]);");
                            }
                        }
                        if(parCount==0) {
                            methodArgs += " $param_" + parCount;
                        } else if (parCount>=1){
                            methodArgs += ", $param_" + parCount;
                        }
                        parCount++;
                    }
                }
                printWriter.println("\t\t\t\t\t$result = "+interfaceName.toLowerCase()+"."+methodName+"("+methodArgs+");");
                if(methodReturnType.contains(str)){
                    printWriter.println("\t\t\t\t\tSystem.out.println(\""+interfaceName+"ServerProxy: result is \" + $result);");
                    printWriter.println("\t\t\t\t\tMessage answer = new Message(\""+interfaceName+"ServerProxy\", $result);");
                } else {
                    printWriter.println("\t\t\t\t\tString dataResult = String.valueOf($result);");
                    printWriter.println("\t\t\t\t\tSystem.out.println(\""+interfaceName+"ServerProxy: result is \" + dataResult);");
                    printWriter.println("\t\t\t\t\tMessage answer = new Message(\""+interfaceName+"ServerProxy\", dataResult);");
                }
                printWriter.println("\t\t\t\t\treturn answer;\n\t\t\t\t}");
                i++;
            }
            printWriter.println("\t\t\t}\n\t\t} else {");
            printWriter.println("\t\t\tSystem.out.println(\""+interfaceName+"ServerProxy Error: Somebody else is trying " +
                    "to communicate with me! ( \"+msg.sender+\" )\");");
            printWriter.println("\t\t\tthrow new Exception(\"Error: Somebody else is trying to communicate with me! ( \"+msg.sender+\" )\");");
            printWriter.println("\t\t}\n\t\tMessage answer = new Message(\""+interfaceName+"Server\",\"Error\");\n\t\treturn answer;\n\t}\n}\n\n");

            //==================PROXY SERVER=====================
            printWriter.println("class "+interfaceName+"ServerProxy implements ServerProxy {");
            printWriter.println("\tprivate int portNumber;\n\n\tpublic "+interfaceName+"ServerProxy(int portNumber) {");
            printWriter.println("\t\tthis.portNumber = portNumber;\n\t}\n");
            printWriter.println("\tpublic void dispatch() {\n\t\tAddress myAddr = new Entry(\"127.0.0.1\",portNumber);");
            printWriter.println("\t\tReplyer rep = new Replyer(\""+interfaceName+"ServerProxy\", myAddr);");
            printWriter.println("\t\t"+interfaceName+"Impl "+interfaceName.toLowerCase()+" = new "+interfaceName+"Impl();");
            printWriter.println("\t\tByteStreamTransformer transformer = new ServerTransformer(new "+interfaceName+
                    "MessageServer("+interfaceName.toLowerCase()+"));");
            printWriter.println("\t\twhile(true) {\n\t\t\ttry {\n\t\t\t\trep.receive_transform_and_send_feedback(transformer);");
            printWriter.println("\t\t\t} catch (Exception e) {\n\t\t\t\tSystem.out.println(e.getMessage());\n\t\t\t}\n\t\t}\n\t}\n}");
            printWriter.close();

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


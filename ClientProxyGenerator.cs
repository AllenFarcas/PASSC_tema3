using System;
using System.CodeDom.Compiler;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using Microsoft.CSharp;

namespace CevaTema
{
    public static class ClientProxyGenerator
    {
        public static object GenerateAndCompile(string className, string interfaceName, string name, int portNumber)
        {
            var location = "/Users/allenpianoman/RiderProjects/CevaTema/CevaTema/"+className+".cs";
            var text = "";
            using (var file = new StreamWriter(location, false))
            {
                text += "using CevaTema.Commons;\nusing CevaTema.Entries;\nusing CevaTema.MessageMarshaller;\n";
                text += "using CevaTema.RequestReply;\n\nnamespace CevaTema {\n\tpublic class ";
                text += interfaceName + "ClientProxy : I" + interfaceName + ", IClientProxy {\n";
                text += "\t\tprivate readonly Requestor _req = new Requestor(\""+interfaceName+"ClientProxy\");\n";
                text += "\t\tprivate readonly int _portNumber;\n\n";
                text += "\t\tpublic "+interfaceName+"ClientProxy(int portNumber) {\n";
                text += "\t\t\t_portNumber = portNumber;\n\t\t}\n";
                const string str = "String";
                
                var assembly = Assembly.GetExecutingAssembly();
                var types = assembly.GetTypes();
                foreach (var type in types)
                {
                    if (!type.Name.Equals("I" + interfaceName)) continue;
                    var methods = type.GetMethods();
                    foreach (var method in methods)
                    {
                        var methodName = method.Name;
                        text += "\t\tpublic " + method.ReturnType + " " + methodName +"(" ;
                        var parameters = method.GetParameters();
                        var methodArgs = "";
                        if (parameters.Length == 0)
                        {
                            methodArgs = "";
                        }
                        else
                        {
                            var counter = 0;
                            foreach (var parameter in parameters)
                            {
                                if (counter == 0)
                                {
                                    methodArgs += parameter.ParameterType + " " + "param" + counter;
                                }
                                else if (counter >= 1)
                                {
                                    methodArgs += ", " + parameter.ParameterType + " " + "param" + counter;
                                }
                                counter++;
                            }
                        }
                        text += methodArgs+ ") {\n";
                        if (parameters.Length == 0)
                        {
                            text += "\t\t\tvar msgData = \" :" + methodName + "\";\n";
                        }
                        else
                        {
                            var counter = 0;
                            text += "\t\t\tvar msgData = ";
                            foreach (var parameter in parameters)
                            {
                                if (counter == 0)
                                {
                                    text += "param" + counter;
                                }
                                else if (counter >= 1)
                                {
                                    methodArgs += "+ \" \" + param" + counter;
                                }
                                counter++;
                            }
                            text += "+\":" + methodName + "\";\n";
                        }
                        text += "\t\t\t//scrie in mesaj toti parametri\n";
                        text += "\t\t\tvar msg = new Message(\""+interfaceName+"ClientProxy\",msgData);\n";
                        text += "\t\t\tvar bytes = Marshaller.Marshal(msg);\n";
                        text += "\t\t\tIAddress dest = new Entry(\"127.0.0.1\",_portNumber);\n";
                        text += "\t\t\t//asteapta rezutatul\n\t\t\tbytes = _req.deliver_and_wait_feedback(dest,bytes);\n";
                        text += "\t\t\t//despacheteaza rezultatul\n\t\t\tvar answer = Marshaller.Unmarshal(bytes);\n";
                        text += "\t\t\t//rezultatul este convertiti la tipul care este returnat de metoda curenta\n";
                        if (method.ReturnType.Name.Contains(str))
                        {
                            text += "\t\t\tvar result = answer.Data;\n";
                        }
                        else
                        {
                            if (method.ReturnType == typeof(int))
                            {
                                text += "\t\t\tvar result = int.Parse(answer.Data);\n";
                            } else if (method.ReturnType == typeof(float))
                            {
                                text += "\t\t\tvar result = float.Parse(answer.Data);\n";
                            } else if (method.ReturnType == typeof(double))
                            {
                                text += "\t\t\tvar result = double.Parse(answer.Data);\n";
                            } else if (method.ReturnType == typeof(bool))
                            {
                                text += "\t\t\tvar result = bool.Parse(answer.Data);\n";
                            } else if (method.ReturnType == typeof(char))
                            {
                                text += "\t\t\tvar result = char.Parse(answer.Data);\n";
                            } else if (method.ReturnType == typeof(short))
                            {
                                text += "\t\t\tvar result = short.Parse(answer.Data);\n";
                            } else if (method.ReturnType == typeof(byte))
                            {
                                text += "\t\t\tvar result = byte.Parse(answer.Data);\n";
                            } 
                        }
                        text += "\t\t\treturn result;\n\t\t}\n";
                    }
                    text += "\t}\n}";
                }
                file.WriteLine(text);
            }
            
            //Compiling
            CSharpCodeProvider provider = new CSharpCodeProvider();
            string exePath = Assembly.GetExecutingAssembly().Location;
            string exeDir = Path.GetDirectoryName(exePath);
            AssemblyName[] assemRefs = Assembly.GetExecutingAssembly().GetReferencedAssemblies();
            List<string> references = new List<string>();
            foreach (AssemblyName assemblyName in assemRefs)
            {
                references.Add(assemblyName.Name + ".dll");
            }
            for (int i = 0; i < references.Count; i++)
            {
                string localName = Path.Combine(exeDir, references[i]);
                if (File.Exists(localName))
                    references[i] = localName;
            }
            references.Add(exePath);
            CompilerParameters compiler_parameters = new CompilerParameters(references.ToArray());
            // True - memory generation, false - external file generation
            compiler_parameters.GenerateInMemory = false;
            // True - exe file generation, false - dll file generation
            CompilerResults results = provider.CompileAssemblyFromSource(compiler_parameters, text);
            if (results.Errors.HasErrors)
            {
                StringBuilder sb = new StringBuilder();
                foreach (CompilerError error in results.Errors)
                {
                    sb.AppendLine(String.Format("Error ({0}): {1}", error.ErrorNumber, error.ErrorText));
                }

                throw new InvalidOperationException(sb.ToString());
            }
            Assembly asse = results.CompiledAssembly;
            Type program = asse.GetType("CevaTema."+interfaceName+"ClientProxy");
            if (program == null)
            {
                Console.WriteLine("Program is null");
            }
            object classInstance = Activator.CreateInstance(program,new object[] {portNumber});
            return classInstance;
        }
    }
}
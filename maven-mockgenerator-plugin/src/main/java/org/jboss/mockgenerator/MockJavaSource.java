/*
 * $Id$
 *
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.mockgenerator;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Formattable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;



/**
 * <p class="changed_added_4_0"></p>
 * @author asmirnov@exadel.com
 *
 */
public class MockJavaSource extends JavaSource {
    
    private final class ParametersFormatter implements Formattable {
        private final Class<?>[] parameterTypes;
        
        private String format = ",%1$s.class";
        
        private boolean varargs=false;

        /**
         * <p class="changed_added_4_0"></p>
         * @param varargs the varargs to set
         */
        public void setVarargs(boolean varargs) {
            this.varargs = varargs;
        }

        private ParametersFormatter(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        public void formatTo(Formatter formatter, int flags, int width,
                int precision) {
            for (int i = 0; i < this.parameterTypes.length; i++) {
                Class<?> parameter = this.parameterTypes[i];
                formatter.format(getFormat(), classToString(parameter, varargs && i==this.parameterTypes.length-1),i,0==i?"":",");
            }
        }

        /**
         * <p class="changed_added_4_0"></p>
         * @param format the format to set
         */
        public void setFormat(String format) {
            this.format = format;
        }

        /**
         * <p class="changed_added_4_0"></p>
         * @return the format
         */
        public String getFormat() {
            return format;
        }
    }

    private final String className;

    private static final String fileHeader = "/*\n" + 
    		" * GENERATED FILE - DO NOT EDIT\n" + 
    		" */\n" + 
    		"\n" + 
    		"package %1$s;\n" + 
    		"\n" + 
            "import static %4$s.findMethod;\n" + 
            "import static %4$s.invokeMethod;\n\n" + 
    		"import java.lang.reflect.Method;\n" + 
    		"import org.easymock.IMocksControl;\n" + 
            "import %4$s;\n" + 
            "import %4$s.MockObject;\n\n" + 
    		"public class %2$s extends %3$s implements MockObject {\n" + 
    		"\n" + 
    		"    private final IMocksControl control;\n" + 
    		"\n" + 
    		"    /**\n" + 
    		"     * @param control\n" + 
    		"     */\n" + 
    		"    public %2$s(IMocksControl control) {\n" + 
    		"        super();\n" + 
    		"        this.control = control;\n" + 
            "        %5$s" + 
    		"    }\n" + 
    		"\n" + 
    		"    public IMocksControl getControl() {\n" + 
    		"        return control;\n" + 
    		"    }\n" + 
    		"";
    
    private static final String fileFooter = "\n}\n";

    private final String mockController; 
    

    public MockJavaSource(File directory, String mockClassName, String className, String mockController) {
        super(directory, mockClassName);
        this.className = className;
        this.mockController = mockController;
    }

    public void printFileHeader(String postConstruct) {
        sprintf(fileHeader,mockPackage,mockClass,className,mockController,notNullString(postConstruct));
    }

    private String notNullString(String postConstruct) {
        return null==postConstruct?"":postConstruct;
    }
    
    private static final String methodConstant="    private static final Method %2$sMethod%4$d = findMethod(%1$s.class, \"%2$s\"%3$s);\n";
    
    private static final String voidMethod="//    @Override\n" + 
    		"    public void %2$s(%3$s) {\n" + 
    		"            invokeMethod(this, %2$sMethod%5$d %4$s);\n" + 
    		"    }\n" + 
    		"";
    private static final String valueMethod="//    @Override\n" + 
    "    public %1$s %2$s(%3$s) {\n" + 
    "            return (%6$s)invokeMethod(this, %2$sMethod%5$d %4$s);\n" + 
    "    }\n" + 
    "";
    
    /**
      * <p class="changed_added_4_0">This map holds counters of methods with same name. That counter value is appended
      * to method constant name to avoid conflicts.</p>
      */
    private Map<String, Integer> methods = new HashMap<String, Integer>();

    /**
     * <p class="changed_added_4_0"></p>
     * TODO - analyze type arguments.
     * @param method
     */
    public void printMethod(Method method){
        String name = method.getName();
        Integer methodNumber = methods.get(name);
        if(null == methodNumber){
            methodNumber = new Integer(0);
        } else {
            methodNumber = methodNumber+1;
        }
        methods.put(name, methodNumber);
        final Class<?>[] parameterTypes = method.getParameterTypes();
        ParametersFormatter parameterTypesFormat = new ParametersFormatter(parameterTypes);
        parameterTypesFormat.setFormat(",%1$s.class");
        sprintf(methodConstant, className,name,parameterTypesFormat,methodNumber);
        parameterTypesFormat.setFormat("%3$s%1$s arg%2$d");
        parameterTypesFormat.setVarargs(method.isVarArgs());
        ParametersFormatter argumentsFormat = new ParametersFormatter(parameterTypes);
        argumentsFormat.setFormat(",arg%2$d");
        Class<?> returnType = method.getReturnType();
        if(void.class.equals(returnType)){
            sprintf(voidMethod, "void",name,parameterTypesFormat,argumentsFormat,methodNumber);
        } else {
            sprintf(valueMethod, classToString(returnType, false),name,parameterTypesFormat,argumentsFormat,methodNumber,boxingClassName(returnType));            
        }
    }

    public void printFileFooter(String codeSegment){
        if(null != codeSegment){
            write(codeSegment);
        }
        write(fileFooter);
    }
}

package org.jboss.mockgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Formatter;

public class JavaSource {

    protected final File directory;
    protected final String mockPackage;
    protected String mockClass;
    protected final StringWriter output;

    public JavaSource(File directory, String mockClassName) {
        this.directory = directory;
        // calculate mock class name
        int indexOfDot = mockClassName.lastIndexOf('.');
        if(indexOfDot>=0){
            this.mockClass=mockClassName.substring(indexOfDot+1);
            this.mockPackage = mockClassName.substring(0, indexOfDot);
        } else {
            this.mockClass=mockClassName;            
            this.mockPackage = "";
        }
        output = new StringWriter(2048);

    }

    public void writeClassFile() throws IOException {
        File packageDirectory = new File(directory,mockPackage.replace('.', File.separatorChar));
        packageDirectory.mkdirs();
        File sourceFile = new File(packageDirectory,mockClass+".java");
        if(sourceFile.exists()){
            // It has would better to check last modification time
            sourceFile.delete();
        }
        sourceFile.createNewFile();
        Writer writer = new BufferedWriter(new FileWriter(sourceFile));
        writer.write(output.toString());
        writer.flush();
        writer.close();
    }

    static String classToString(Class<?> returnType) {
        return classToString(returnType, false);
    }

    static String classToString(Class<?> returnType, boolean varargs) {
        if(returnType.isArray()){
            return classToString(returnType.getComponentType())+(varargs?"...":"[]");
        }
        return returnType.getName().replace('$', '.');
    }
    
    static String boxingClassName(Class<?> clazz){
        if(clazz.isPrimitive()){
            if(boolean.class.equals(clazz)){
                return Boolean.class.getSimpleName();
            } else if(int.class.equals(clazz)){
                return Integer.class.getSimpleName();
            } else if(char.class.equals(clazz)){
                return Character.class.getSimpleName();
            } else if(short.class.equals(clazz)){
                return Short.class.getSimpleName();
            } else if(byte.class.equals(clazz)){
                return Byte.class.getSimpleName();
            } else if(long.class.equals(clazz)){
                return Long.class.getSimpleName();
            } else if(float.class.equals(clazz)){
                return Float.class.getSimpleName();
            } else if(double.class.equals(clazz)){
                return Double.class.getSimpleName();
            }
        }
        return classToString(clazz);
    }

    static Class<?>[] getMethodParemeters(Method method){
        Type[] parameterTypes = method.getGenericParameterTypes();
        Class<?>[] parameters = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Type type = parameterTypes[i];
        }
        return parameters;
    }
    
    public void sprintf(String format, Object...args) {
        Formatter formatter = new Formatter(output);
        formatter.format(format, args);
        formatter.flush();
    }
    
    public void write(String str){
        output.write(str);
    }

    @Override
    public String toString() {
        return output.toString();
    }

}
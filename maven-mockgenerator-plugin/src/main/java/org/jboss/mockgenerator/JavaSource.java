package org.jboss.mockgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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

    protected String classToString(Class<?> returnType) {
        if(returnType.isArray()){
            return classToString(returnType.getComponentType())+"[]";
        }
        return returnType.getName().replace('$', '.');
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
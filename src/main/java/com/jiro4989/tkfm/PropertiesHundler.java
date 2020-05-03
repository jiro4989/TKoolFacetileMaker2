package com.jiro4989.tkfm;

import java.io.*;
import java.util.Properties;

public class PropertiesHundler {
    private Properties prop;
    private static final String INITIAL_IO_DIR_PATH;
    private final String ioDirPath;
    private final String fileName;
    private final String filePath;
    private final String initialKeys[];
    private final String initialValues[];

    static 
    {
        INITIAL_IO_DIR_PATH = (new StringBuilder(".")).append(File.separator).append("properties").toString();
    }

    public PropertiesHundler(String aFileName, String keys[], String values[])
    {
        this(aFileName, INITIAL_IO_DIR_PATH, keys, values);
    }

    public PropertiesHundler(String aFileName, String anIoDirPath, String keys[], String values[])
    {
        prop = new Properties();
        fileName = (new StringBuilder(String.valueOf(aFileName))).append(".properties").toString();
        ioDirPath = anIoDirPath;
        filePath = (new StringBuilder(String.valueOf(ioDirPath))).append(File.separator).append(fileName).toString();
        initialKeys = keys;
        initialValues = values;
    }

    public void load()
    {
        File file;
        (new File(ioDirPath)).mkdirs();
        file = new File(filePath);
        if(!file.exists())
            break MISSING_BLOCK_LABEL_120;
        Exception exception;
        exception = null;
        Object obj = null;
        InputStream is = new FileInputStream(file);
        prop.load(new InputStreamReader(is, "UTF-8"));
        if(is != null)
            is.close();
        break MISSING_BLOCK_LABEL_119;
        exception;
        if(is != null)
            is.close();
        throw exception;
        Exception exception1;
        exception1;
        if(exception == null)
            exception = exception1;
        else
        if(exception != exception1)
            exception.addSuppressed(exception1);
        throw exception;
        IOException e;
        e;
        e.printStackTrace();
        return;
        for(int i = 0; i < initialKeys.length; i++)
            prop.setProperty(initialKeys[i], initialValues[i]);

        write();
        load();
        return;
    }

    public void write()
    {
        File file;
        (new File(ioDirPath)).mkdirs();
        file = new File(filePath);
        Exception exception;
        exception = null;
        Object obj = null;
        FileOutputStream fos = new FileOutputStream(file);
        prop.store(new OutputStreamWriter(fos, "UTF-8"), null);
        if(fos != null)
            fos.close();
        break MISSING_BLOCK_LABEL_121;
        exception;
        if(fos != null)
            fos.close();
        throw exception;
        Exception exception1;
        exception1;
        if(exception == null)
            exception = exception1;
        else
        if(exception != exception1)
            exception.addSuppressed(exception1);
        throw exception;
        FileNotFoundException e;
        e;
        e.printStackTrace();
        break MISSING_BLOCK_LABEL_121;
        e;
        e.printStackTrace();
    }

    public void setValue(String key, String value)
    {
        prop.setProperty(key, value);
    }

    public String getValue(String key)
    {
        return prop.getProperty(key);
    }

    public boolean exists()
    {
        File file = new File(filePath);
        return file.exists();
    }

}

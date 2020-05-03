package com.jiro4989.tkfm;

import java.io.*;
import java.util.Properties;

public class PropertiesHandler {
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
        File file = new File(filePath);
        (new File(ioDirPath)).mkdirs();

        if(!file.exists()) {
          for(int i = 0; i < initialKeys.length; i++) {
            prop.setProperty(initialKeys[i], initialValues[i]);
          }
          write();
        }

        file = new File(filePath);

        try (InputStream is = new FileInputStream(file)) {
          prop.load(new InputStreamReader(is, "UTF-8"));
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    public void write()
    {
        File file = new File(filePath);
        (new File(ioDirPath)).mkdirs();

        try (FileOutputStream fos = new FileOutputStream(file)) {
          prop.store(new OutputStreamWriter(fos, "UTF-8"), null);
        } catch (IOException e) {
          e.printStackTrace();
        }
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

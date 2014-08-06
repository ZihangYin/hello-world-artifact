package com.pepsi.rest.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

public class PropertiesParser {

    private final Properties properties = new Properties();

    public PropertiesParser(@Nonnull String propertyFile) throws IOException {
        InputStream propertiesInputStream = getClass().getClassLoader().getResourceAsStream(propertyFile);        
        if (propertiesInputStream == null) {
            throw new FileNotFoundException("Property file '" + propertyFile + "' does not exist.");
        }        
        this.properties.load(propertiesInputStream);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}

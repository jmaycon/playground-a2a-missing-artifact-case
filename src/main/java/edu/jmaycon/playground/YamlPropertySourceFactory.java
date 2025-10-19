package edu.jmaycon.playground;

import lombok.NonNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public @NonNull org.springframework.core.env.PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        var factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties props = factory.getObject();
        return new PropertiesPropertySource(name != null ? name : Objects.requireNonNull(resource.getResource().getFilename()), Objects.requireNonNull(props));
    }
}

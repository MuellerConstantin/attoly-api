package de.mueller_constantin.attoly.api.config;

import de.mueller_constantin.attoly.api.domain.payment.SubscriptionPlanProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class SubscriptionPlanConfig {
    @Bean
    public SubscriptionPlanProperties subscriptionPlanProperties() throws IOException {
        Resource resource = new ClassPathResource("subscription-plans.yml");

        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        PropertySource<?> propertySource =
                loader.load("plans-static", resource).get(0);

        Binder binder = new Binder(ConfigurationPropertySources.from(propertySource));
        return binder.bind("attoly", SubscriptionPlanProperties.class)
                .orElseThrow(() ->
                        new IllegalStateException("subscription-plans.yml could not be loaded")
                );
    }
}

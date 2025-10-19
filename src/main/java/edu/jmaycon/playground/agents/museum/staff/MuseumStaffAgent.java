package edu.jmaycon.playground.agents.museum.staff;

import lombok.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Profile("museum-staff")
@SpringBootApplication
public class MuseumStaffAgent {

    public static void main(String[] args) {
        SpringApplication.run(MuseumStaffAgent.class, args);
    }

    @Bean
    Advisor rotatingMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().maxMessages(20).build())
                .build();
    }

    @Bean
    List<ToolCallback> toolCallbacks(MuseumStaff tools) {
        return List.of(ToolCallbacks.from(tools));
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder chatBuilder) {
        return chatBuilder.defaultAdvisors(rotatingMemoryAdvisor()).build();
    }

}

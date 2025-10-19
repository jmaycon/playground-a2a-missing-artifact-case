package edu.jmaycon.playground.agents.security;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("security")
@SpringBootApplication
public class SecuritySystemAgent {

    public static void main(String[] args) {
        SpringApplication.run(SecuritySystemAgent.class, args);
    }

    @Bean
    Advisor rotatingMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().maxMessages(20).build())
                .build();
    }

    @Bean
    List<ToolCallback> toolCallbacks(SecurityGuard securityGuard) {
        return List.of(ToolCallbacks.from(securityGuard));
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder chatBuilder) {
        return chatBuilder.defaultAdvisors(rotatingMemoryAdvisor()).build();
    }
}

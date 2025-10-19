package edu.jmaycon.playground.agents.investigator;

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
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Profile("investigator")
@SpringBootApplication
public class InvestigatorAgent {

    public static void main(String[] args) {
        SpringApplication.run(InvestigatorAgent.class, args);
    }

    @Bean
    Advisor rotatingMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().maxMessages(20).build())
                .build();
    }

    @Bean
    List<ToolCallback> toolCallbacks(InvestigatorDrWatson sherlockHolmes) {
        return List.of(ToolCallbacks.from(sherlockHolmes));
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder chatBuilder) {
        return chatBuilder.defaultAdvisors(rotatingMemoryAdvisor()).build();
    }
}

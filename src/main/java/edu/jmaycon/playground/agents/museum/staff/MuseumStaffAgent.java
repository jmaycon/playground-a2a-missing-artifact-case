package edu.jmaycon.playground.agents.museum.staff;

import java.util.List;
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

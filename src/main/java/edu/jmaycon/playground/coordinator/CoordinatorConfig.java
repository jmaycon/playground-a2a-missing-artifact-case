package edu.jmaycon.playground.coordinator;

import edu.jmaycon.playground.ConsolePrinterConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@Import(ConsolePrinterConfig.class)
class CoordinatorConfig {

    @Bean
    Advisor rotatingMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().maxMessages(100).build())
                .build();
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder,
                          ObjectProvider<ToolCallback> locals,
                          AsyncMcpToolCallbackProvider mcp) {
        var all = new ArrayList<>(locals.orderedStream().toList());
        if (mcp != null) all.addAll(Arrays.asList(mcp.getToolCallbacks()));
        return builder.defaultAdvisors(rotatingMemoryAdvisor()).defaultToolCallbacks(all).build();
    }
}

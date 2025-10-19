package edu.jmaycon.playground.agents.detective;

import edu.jmaycon.playground.agents.museum.staff.MuseumStaff;
import io.modelcontextprotocol.server.McpAsyncServer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.mcp.server.autoconfigure.McpServerAutoConfiguration;
import org.springframework.ai.mcp.server.autoconfigure.McpWebFluxServerAutoConfiguration;
import org.springframework.ai.mcp.server.autoconfigure.McpWebMvcServerAutoConfiguration;
import org.springframework.ai.model.ollama.autoconfigure.OllamaChatAutoConfiguration;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Profile("detective")
@SpringBootApplication
@AutoConfiguration(
        after = {
                OllamaChatAutoConfiguration.class // example
        },
        before = {
                McpWebMvcServerAutoConfiguration.class,
                McpWebFluxServerAutoConfiguration.class
        }

)

public class DetectiveAgent {

    public static void main(String[] args) {
        SpringApplication.run(DetectiveAgent.class, args);
    }

    @Bean
    Advisor rotatingMemoryAdvisor() {
        return MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().maxMessages(20).build())
                .build();
    }


    @Bean
    List<ToolCallback> toolCallbacks(DetectiveSherlockHolmes tools) {
        return List.of(ToolCallbacks.from(tools));
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder chatBuilder, AsyncMcpToolCallbackProvider mcpTools, ObjectProvider<ToolCallback> local) {
        var all = new ArrayList<ToolCallback>(local.stream().toList()); // Register mcp tools to be used by this agent
        if (mcpTools != null) Collections.addAll(all, mcpTools.getToolCallbacks());
        return chatBuilder
                .defaultToolCallbacks(all)
                .defaultAdvisors(rotatingMemoryAdvisor()).build();
    }


}

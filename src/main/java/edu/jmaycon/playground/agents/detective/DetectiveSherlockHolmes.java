package edu.jmaycon.playground.agents.detective;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class DetectiveSherlockHolmes {

    @Value("museum-missing-artifact-case/sherlock-profile.md")
    private final ClassPathResource persona;

    // When the MCP server starts it exposes Tools that will depend on the ChatClient hence having creating cyclic
    // dependency.
    private final ObjectProvider<ChatClient> chat;

    @Tool(
            name = "detective_sherlock_holmes",
            description =
                    "Correlates findings from all sources, including Dr. Watson, and provides the final deduction.")
    public String answer(
            @ToolParam(description = "investigation summary, collected evidence, or new lead") String question,
            @ToolParam(description = "conversation id") String conversationId) {

        log.info("question={}, conversation-id={}", question, conversationId);
        return chat.getObject()
                .prompt()
                .system(persona)
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}

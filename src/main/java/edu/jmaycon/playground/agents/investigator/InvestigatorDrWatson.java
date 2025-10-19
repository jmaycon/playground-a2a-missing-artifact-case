package edu.jmaycon.playground.agents.investigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class InvestigatorDrWatson {

    // When the MCP server starts it exposes Tools that will depend on the ChatClient hence having creating cyclic
    // dependency.
    private final ObjectProvider<ChatClient> chat;

    @Value("museum-missing-artifact-case/watson-profile.md")
    private final ClassPathResource persona;

    @Tool(
            name = "investigator_dr_watson",
            description =
                    "Watson analyzes interrogatory records and proposes possibilities (not conclusions). Streams text.")
    public String answer(
            @ToolParam(description = "question or request") String question,
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

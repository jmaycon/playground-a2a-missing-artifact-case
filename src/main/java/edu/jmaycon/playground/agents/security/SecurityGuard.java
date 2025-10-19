package edu.jmaycon.playground.agents.security;

import edu.jmaycon.playground.YamlPropertySourceFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource(
        value = "classpath:museum-missing-artifact-case/narrative.yml",
        factory = YamlPropertySourceFactory.class)
class SecurityGuard {

    private final ApplicationContext context;

    @Value("museum-missing-artifact-case/security-guard-profile.md")
    private final ClassPathResource persona;

    @Value("${security-system}")
    private final String securityAccessRecords;

    private final Set<String> conversationHistory = ConcurrentHashMap.newKeySet();

    @Tool(
            name = "security_system_guard",
            description = "Guard answers only from the Security System Access Record. Streams text.")
    public String answer(
            @ToolParam(description = "question") String question,
            @ToolParam(description = "conversation id") String conversationId)
            throws IOException {

        log.info("Question: {}/{}", question, conversationId);
        boolean firstTurn = conversationHistory.add(conversationId);

        var userMsg = firstTurn
                ? PromptTemplate.builder()
                        .template(
                                """
                # Security System Access Record
                {access_record}

                # Inquiry
                {question}
                """)
                        .variables(Map.of("access_record", securityAccessRecords, "question", question))
                        .build()
                        .render()
                : question;

        String answer = context.getBean(ChatClient.class)
                .prompt()
                .system(persona)
                .user(userMsg)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        log.info("Answer: {}", answer);
        return answer;
    }
}

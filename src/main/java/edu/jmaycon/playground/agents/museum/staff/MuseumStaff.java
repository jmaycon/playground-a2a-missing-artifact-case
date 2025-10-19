package edu.jmaycon.playground.agents.museum.staff;

import edu.jmaycon.playground.YamlPropertySourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
@PropertySource(
        value = "classpath:museum-missing-artifact-case/narrative.yml",
        factory = YamlPropertySourceFactory.class)
@Slf4j
public class MuseumStaff {

    // When the MCP server starts it exposes Tools that will depend on the ChatClient hence having creating cyclic
    // dependency.
    private final ObjectProvider<ChatClient> chat;

    @Value("${personas.marco}")
    private final String marcoPersona;

    @Value("${personas.lena}")
    private final String lenaPersona;

    @Value("${personas.dr-voss}")
    private final String vossPersona;

    @Tool(
            name = "museum_staff_marco",
            description = "Marco museum staff answers to interrogatory questions, Operations Manager.")
    public String marcoAnswer(
            @ToolParam(description = "question or prompt") String question,
            @ToolParam(description = "conversation id") String conversationId) {
        log.info("Question: {}/{}", question, conversationId);
        String answer = chat.getObject()
                .prompt()
                .system(marcoPersona)
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId + ".marco"))
                .call()
                .content();
        log.info("Answer: {}", answer);
        return answer;
    }

    @Tool(
            name = "museum_staff_lena",
            description = "Lena museum staff answers to interrogatory questions, Front Hall Supervisor.")
    public Flux<String> lenaAnswer(
            @ToolParam(description = "question or prompt") String question,
            @ToolParam(description = "conversation id") String conversationId) {
        return chat
                .getObject()
                .prompt()
                .system(lenaPersona)
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId + ".lena"))
                .stream()
                .content();
    }

    @Tool(
            name = "museum_staff_dr_voss",
            description =
                    "Dr. Heinrich Voss museum staff answers to interrogatory questions, Conservator of Antiquities..")
    public Flux<String> vossAnswer(
            @ToolParam(description = "question or prompt") String question,
            @ToolParam(description = "conversation id") String conversationId) {
        return chat
                .getObject()
                .prompt()
                .system(vossPersona)
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId + ".voss"))
                .stream()
                .content();
    }
}

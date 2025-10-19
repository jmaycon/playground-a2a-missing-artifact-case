package edu.jmaycon.playground.coordinator;

import edu.jmaycon.playground.model.AiMessageEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
class ChatCli implements CommandLineRunner {

    private static final String CLOSED = "CASE_CLOSED";
    private static final String UNSOLVED = "CASE_UNSOLVED";

    private final ChatClient chat;
    private final ApplicationEventPublisher events;
    private final ObjectProvider<ToolCallback> locals;

    @Override
    public void run(String... args) throws Exception {
        Map<String, String> collect = locals.stream()
                .collect(Collectors.toMap(k -> k.getToolDefinition().name(), v -> v.getToolDefinition()
                        .description()));
        var chat = this.chat.mutate().defaultTools(new Output()).build();
        try (var in = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("CLI Chat. Type 'exit' to quit.");
            while (true) {
                System.out.print("\n> ");
                var userPrompt = in.readLine();
                if (userPrompt == null || "exit".equalsIgnoreCase(userPrompt.trim())) break;

                final String sys =
                        """
                # Objective
                You are a specialized autonomous agent capable of performing any assigned task through a transparent
                Conduct all reasoning and communication in a natural, human-readable way.

                # Tools you must use
                %s

                # Instructions
                1. ALWAYS Announce your **initial hypothesis or plan** using `output_tool`.
                3. Report intermediate results and their meaning using `output_tool`.
                4. Update your plan based on new evidence using `output_tool`.
                5. Iterate autonomously until you reach a final conclusion OR 3 minutes has passed.
                6. Summarize findings and outcome in human-readable form before finalizing.

                # Output Format
                All communication must use output_tool: <message>

                # Completion Rules
                - If solved: %s
                - If unsolved: %s
              """
                                .formatted(collect, CLOSED, UNSOLVED);

                var start = Instant.now();
                var deadline = start.plus(Duration.ofMinutes(3));

                var buffer = new StringBuilder();
                var finished = new AtomicBoolean(false);
                var firstTurn = true;

                do {
                    var turnDone = new AtomicBoolean(false);
                    var remaining = Duration.between(Instant.now(), deadline);
                    if (!remaining.isNegative() && !remaining.isZero()) {
                        Flux<String> stream = (firstTurn
                                        ? chat.prompt().system(sys).user(userPrompt)
                                        : chat.prompt().user("continue"))
                                .stream()
                                        .content()
                                        .doOnNext(chunk -> {
                                            // stream live progress naturally (no JSON, no prefixes)
                                            buffer.append(chunk);
                                            events.publishEvent(new AiMessageEvent(this, chunk, false));

                                            var s = buffer.toString();
                                            if (s.contains(CLOSED) || s.contains(UNSOLVED)) {
                                                turnDone.set(true);
                                                finished.set(true);
                                            }
                                        })
                                        .takeUntil(__ -> turnDone.get())
                                        .take(remaining)
                                        .doOnComplete(() -> events.publishEvent(new AiMessageEvent(this, "", true)))
                                        .onErrorResume(ex -> {
                                            events.publishEvent(
                                                    new AiMessageEvent(this, "[error] " + ex.getMessage(), true));
                                            return Flux.empty();
                                        });

                        stream.blockLast();
                        firstTurn = false;
                    } else {
                        break;
                    }
                } while (!finished.get() && Instant.now().isBefore(deadline));

                System.out.println("---------------------");
            }
        }
    }

    static class Output {

        @Tool(
                name = "output_tool",
                description =
                        "Use this tool to communicate feedback or intermediate steps or responses you have obtained.")
        public void marcoAnswer(@ToolParam(description = "Text human readable") String text) {
            System.out.println(text);
        }
    }
}

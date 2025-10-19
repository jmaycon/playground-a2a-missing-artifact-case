package edu.jmaycon.playground;

import edu.jmaycon.playground.model.AiMessageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class ConsolePrinterConfig {

    @Bean
    ConsolePrinter consolePrinter() {
        return new ConsolePrinter();
    }

    static class ConsolePrinter {
        @EventListener
        void onAi(AiMessageEvent e) {
            System.out.print(e.text());
            if (e.done()) System.out.println();
        }
    }
}

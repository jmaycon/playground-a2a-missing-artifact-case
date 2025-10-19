package edu.jmaycon.playground.agents.detective;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class ToolRegistry {
    private final List<ToolCallback> callbacks;

    Map<String, ToolCallback> byName() {
        return callbacks.stream()
                .collect(Collectors.toMap(c -> c.getToolDefinition().name(), Function.identity()));
    }

    List<ToolDefinition> specs() {
        return callbacks.stream().map(ToolCallback::getToolDefinition).toList();
    }
}

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
class McpToolController {

    private final ToolRegistry registry;

    @GetMapping("/tools")
    @Operation(summary = "List MCP tools")
    public List<ToolDefinition> listTools() {
        return registry.specs();
    }


}

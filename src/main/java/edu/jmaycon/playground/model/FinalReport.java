package edu.jmaycon.playground.model;

import java.util.List;
import lombok.Builder;

@Builder
public record FinalReport(String context, List<String> findings, String conclusion, double confidence) {}

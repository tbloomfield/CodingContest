package org.tbloomfield.codingcontest.executor.server;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Question {
    private String id;
    private String title;
    private String description;
    private String constraints;
    
    private List<Example> examples;
}

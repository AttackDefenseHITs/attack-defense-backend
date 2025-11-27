package ru.hits.attackdefenceplatform.business.checker.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.hits.attackdefenceplatform.business.checker.enums.CheckerResult;

import java.util.List;

@Data
@AllArgsConstructor
public class ScriptExecutionResult {
    private CheckerResult checkerResult;
    private List<String> outputLines;
}


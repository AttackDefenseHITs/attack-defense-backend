package ru.hits.attackdefenceplatform.core.checker;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class CheckerValidator {
    private static final List<String> REQUIRED_FUNCTIONS = List.of("check", "put", "get", "get_flag");

    public boolean validateSyntax(File scriptFile) {
        try {
            var process = new ProcessBuilder("python3", "-m", "py_compile", scriptFile.getAbsolutePath())
                    .start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateFunctions(File scriptFile) {
        try {
            var processBuilder = new ProcessBuilder(
                    "python3",
                    "-c",
                    String.format(
                            "import importlib.util; spec = importlib.util.spec_from_file_location('checker', '%s'); " +
                                    "checker = importlib.util.module_from_spec(spec); spec.loader.exec_module(checker); " +
                                    "assert all(hasattr(checker, func) for func in %s)",
                            scriptFile.getAbsolutePath(),
                            REQUIRED_FUNCTIONS.toString()
                    )
            );
            var process = processBuilder.start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}

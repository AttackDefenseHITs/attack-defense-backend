package ru.hits.attackdefenceplatform.public_interface.checker;

import java.util.List;

public record StartCheckerRequest(
        List<String> commands
) {
}

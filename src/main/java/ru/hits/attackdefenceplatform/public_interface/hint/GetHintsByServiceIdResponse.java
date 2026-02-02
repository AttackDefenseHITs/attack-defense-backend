package ru.hits.attackdefenceplatform.public_interface.hint;

import java.util.List;

public record GetHintsByServiceIdResponse(
        List<ServiceHintViewDto> hints
) {
}

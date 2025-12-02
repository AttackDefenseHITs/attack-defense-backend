package ru.hits.attackdefenceplatform.core.attack_bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttackBotRunner {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile long lastRunTs = 0;

    public boolean isRunning() {
        return running.get();
    }

    @Async
    public void runRoundAsync(AttackBotContext ctx) {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        try {
            //runRound(ctx);
        } finally {
            running.set(false);
            lastRunTs = System.currentTimeMillis();
        }
    }
}

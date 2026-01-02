package ru.hits.attackdefenceplatform.core.attack_bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.exploit.executor.ExploitExecutor;
import ru.hits.attackdefenceplatform.core.attack_bot.metric.AttackBotStateStore;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class AttackBotRunner {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile long lastRunTs = 0;

    private final AttackBotContextBuilder contextBuilder;
    private final TargetTeamSelector targetTeamSelector;
    private final ExploitExecutor exploitExecutor;
    private final AttackBotStateStore stateStore;

    public AttackBotRunner(
            AttackBotContextBuilder contextBuilder,
            TargetTeamSelector targetTeamSelector,
            @Qualifier("fakeExploitExecutor") ExploitExecutor exploitExecutor,
            AttackBotStateStore stateStore
    ) {
        this.contextBuilder = contextBuilder;
        this.targetTeamSelector = targetTeamSelector;
        this.exploitExecutor = exploitExecutor;
        this.stateStore = stateStore;
    }

    public boolean isRunning() {
        return running.get();
    }

    @Async("taskExecutor")
    public void runRoundAsync(long roundNumber) {
        if (!running.compareAndSet(false, true)) {
            log.warn("Атак-бот уже выполняется, пропускаем запуск для раунда {}", roundNumber);
            return;
        }

        try {
            AttackBotContext ctx = contextBuilder.build(roundNumber);
            runRound(ctx);
        } catch (Exception e) {
            log.error("Ошибка при выполнении атак-бота для раунда {}", roundNumber, e);
        } finally {
            running.set(false);
            lastRunTs = System.currentTimeMillis();
        }
    }

    private void runRound(AttackBotContext ctx) {
        var settings = ctx.getSettings();

        if (!settings.isEnabled()) {
            log.info("Атак-бот выключен в настройках – выходим");
            return;
        }

        // 1) решаем, будут ли атаки в этом раунде
        double r = Math.random();
        if (r > settings.getAttackProbability()) {
            log.info("В этом раунде атаки не выполняются (r={} > p={})", r, settings.getAttackProbability());
            return;
        }

        // 2) выбираем цели
        var targets = targetTeamSelector.selectTargets(ctx);
        if (targets.isEmpty()) {
            log.info("Нет команд-целей для атаки в этом раунде");
            return;
        }

        log.info("Выбрано {} команд-целей: {}", targets.size(),
                targets.stream().map(TeamInfo::getName).toList());

        // 3) запускаем эксплойты
        exploitExecutor.execute(targets);

        // 4) обновляем cooldown
        long currentRound = ctx.getRoundTimestamp();
        for (TeamInfo t : targets) {
            stateStore.updateLastAttackRound(t.getId(), currentRound);
        }

        log.info("Атаки завершены, cooldown обновлён для {} команд", targets.size());
    }
}

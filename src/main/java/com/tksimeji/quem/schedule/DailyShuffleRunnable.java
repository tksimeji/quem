package com.tksimeji.quem.schedule;

import com.tksimeji.quem.QuestType;
import com.tksimeji.quem.element.Category;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DailyShuffleRunnable extends BukkitRunnable {
    private @Nullable LocalDate execute;

    @Override
    public void run() {
        LocalDate today = LocalDate.now();

        if (execute != null && execute.equals(today)) {
            return;
        }

        execute = today;

        List<QuestType> dailyQuests = new ArrayList<>(QuestType.getInstances().stream()
                .filter(type -> type.getCategory() == Category.DAILY)
                .peek(QuestType::disable)
                .toList());

        Collections.shuffle(dailyQuests);

        for (int i = 12; 0 <= i; i --) {
            dailyQuests.get(i).enable();
        }
    }
}

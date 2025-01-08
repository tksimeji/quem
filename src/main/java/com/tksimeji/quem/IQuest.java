package com.tksimeji.quem;

import org.jetbrains.annotations.NotNull;

public interface IQuest {
    int getPhase(@NotNull IQuestType.Requirement requirement);

    void setPhase(@NotNull IQuestType.Requirement requirement, int phase);

    void onEnd(@NotNull EndReason reason);

    void onComplete();

    void onIncomplete();

    void cancel();

    void tick();

    enum EndReason {
        COMPLETE,
        CANCEL,
        OTHER
    }
}

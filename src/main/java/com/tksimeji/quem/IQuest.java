package com.tksimeji.quem;

import org.jetbrains.annotations.NotNull;

public interface IQuest {
    int getPhase();

    void setPhase(int phase);

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

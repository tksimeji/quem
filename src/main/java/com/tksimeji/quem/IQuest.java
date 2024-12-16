package com.tksimeji.quem;

import com.tksimeji.quem.element.CommandScript;
import org.jetbrains.annotations.NotNull;

public interface IQuest {
    int getPhase();

    void setPhase(int phase);

    void onEnd(@NotNull EndReason reason);

    void onComplete();

    void onIncomplete();

    void cancel();

    void call(@NotNull CommandScript.Trigger trigger);

    void tick();

    enum EndReason {
        COMPLETE,
        CANCEL,
        OTHER
    }
}

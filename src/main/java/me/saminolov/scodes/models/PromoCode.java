package me.saminolov.scodes.models;

import java.util.ArrayList;
import java.util.List;

public class PromoCode {

    private final String name;
    private int playerLimit;
    private int totalLimit;
    private int usedTotal;
    private List<String> rewards;
    private String messageSuccess;
    private String messagePlayerLimit;
    private String messageTotalLimit;

    public PromoCode(String name, int playerLimit, int totalLimit, List<String> rewards) {
        this.name = name;
        this.playerLimit = playerLimit;
        this.totalLimit = totalLimit;
        this.usedTotal = 0;
        this.rewards = new ArrayList<>(rewards);
        this.messageSuccess = "";
        this.messagePlayerLimit = "";
        this.messageTotalLimit = "";
    }

    public String getName() { return name; }

    public int getPlayerLimit() { return playerLimit; }
    public void setPlayerLimit(int playerLimit) { this.playerLimit = playerLimit; }

    public int getTotalLimit() { return totalLimit; }
    public void setTotalLimit(int totalLimit) { this.totalLimit = totalLimit; }

    public int getUsedTotal() { return usedTotal; }
    public void setUsedTotal(int usedTotal) { this.usedTotal = usedTotal; }
    public void incrementUsedTotal() { this.usedTotal++; }

    public List<String> getRewards() { return rewards; }
    public void setRewards(List<String> rewards) { this.rewards = new ArrayList<>(rewards); }

    public String getMessageSuccess() { return messageSuccess; }
    public void setMessageSuccess(String msg) { this.messageSuccess = msg; }

    public String getMessagePlayerLimit() { return messagePlayerLimit; }
    public void setMessagePlayerLimit(String msg) { this.messagePlayerLimit = msg; }

    public String getMessageTotalLimit() { return messageTotalLimit; }
    public void setMessageTotalLimit(String msg) { this.messageTotalLimit = msg; }
}

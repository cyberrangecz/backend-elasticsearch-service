package cz.muni.ics.kypo.elasticsearch.api.dto;

import java.util.Map;
import java.util.Objects;

public class CommandsStatistics {
    private Long phaseId;
    private Long taskId;
    private Long numberOfCommands;
    private Map<String, Long> keywordsInCommands;

    public CommandsStatistics() {
    }

    public CommandsStatistics(Long phaseId, Long taskId, Long numberOfCommands, Map<String, Long> keywordsInCommands) {
        this.phaseId = phaseId;
        this.taskId = taskId;
        this.numberOfCommands = numberOfCommands;
        this.keywordsInCommands = keywordsInCommands;
    }

    public Long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getNumberOfCommands() {
        return numberOfCommands;
    }

    public void setNumberOfCommands(Long numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }

    public Map<String, Long> getKeywordsInCommands() {
        return keywordsInCommands;
    }

    public void setKeywordsInCommands(Map<String, Long> keywordsInCommands) {
        this.keywordsInCommands = keywordsInCommands;
    }

    public void addKeyword(String keyword, Long keywordOccurrences) {
        this.keywordsInCommands.put(keyword, keywordOccurrences);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandsStatistics that = (CommandsStatistics) o;
        return Objects.equals(getPhaseId(), that.getPhaseId()) &&
                Objects.equals(getTaskId(), that.getTaskId()) &&
                Objects.equals(getNumberOfCommands(), that.getNumberOfCommands()) &&
                Objects.equals(getKeywordsInCommands(), that.getKeywordsInCommands());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhaseId(), getTaskId(), getNumberOfCommands(), getKeywordsInCommands());
    }

    @Override
    public String toString() {
        return "CommandsStatistics{" +
                "phaseId=" + phaseId +
                ", taskId=" + taskId +
                ", numberOfCommands=" + numberOfCommands +
                ", keywordsInCommands=" + keywordsInCommands +
                '}';
    }
}

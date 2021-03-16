package cz.muni.ics.kypo.elasticsearch.api.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OverallPhaseStatistics {
    private Long phaseId;
    private Long taskId;
    private Long phaseTime;
    private List<String> wrongAnswers;
    private Boolean solutionDisplayed;
    private Long numberOfCommands;
    private Map<String, Long> keywordsInCommands;

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

    public Long getPhaseTime() {
        return phaseTime;
    }

    public void setPhaseTime(Long phaseTime) {
        this.phaseTime = phaseTime;
    }

    public List<String> getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(List<String> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Boolean getSolutionDisplayed() {
        return solutionDisplayed;
    }

    public void setSolutionDisplayed(Boolean solutionDisplayed) {
        this.solutionDisplayed = solutionDisplayed;
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
        OverallPhaseStatistics that = (OverallPhaseStatistics) o;
        return Objects.equals(getPhaseId(), that.getPhaseId()) &&
                Objects.equals(getTaskId(), that.getTaskId()) &&
                Objects.equals(getPhaseTime(), that.getPhaseTime()) &&
                Objects.equals(getWrongAnswers(), that.getWrongAnswers()) &&
                Objects.equals(getSolutionDisplayed(), that.getSolutionDisplayed()) &&
                Objects.equals(getNumberOfCommands(), that.getNumberOfCommands()) &&
                Objects.equals(getKeywordsInCommands(), that.getKeywordsInCommands());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhaseId(), getTaskId(), getPhaseTime(), getWrongAnswers(), getSolutionDisplayed(), getNumberOfCommands(), getKeywordsInCommands());
    }

    @Override
    public String toString() {
        return "OverallPhaseStatistics{" +
                "phaseId=" + phaseId +
                ", taskId=" + taskId +
                ", phaseTime=" + phaseTime +
                ", wrongAnswers=" + wrongAnswers +
                ", solutionDisplayed=" + solutionDisplayed +
                ", numberOfCommands=" + numberOfCommands +
                ", keywordsInCommands=" + keywordsInCommands +
                '}';
    }
}

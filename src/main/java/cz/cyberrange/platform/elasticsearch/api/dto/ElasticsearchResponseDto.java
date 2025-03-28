package cz.cyberrange.platform.elasticsearch.api.dto;

/**
 * Encapsulates response from Elasticsearch.
 */
public class ElasticsearchResponseDto {

    private boolean acknowledged;

    /**
     * Instantiates a new Elasticsearch response dto.
     */
    public ElasticsearchResponseDto() {
    }

    /**
     * Is acknowledged boolean.
     *
     * @return the boolean
     */
    public boolean isAcknowledged() {
        return acknowledged;
    }

    /**
     * Sets acknowledged.
     *
     * @param acknowledged the acknowledged
     */
    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

}

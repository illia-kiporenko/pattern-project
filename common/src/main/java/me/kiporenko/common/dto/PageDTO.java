package me.kiporenko.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.*;

import java.util.List;

@Getter
// This remains a good safety net to ignore any other unexpected properties.
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageDTO<T> extends PageImpl<T> {

    /**
     * The constructor Jackson will use when reading from Redis.
     * It only needs the essential properties to reconstruct the page.
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PageDTO(@JsonProperty("content") List<T> content,
                   @JsonProperty("number") int number,
                   @JsonProperty("size") int size,
                   @JsonProperty("totalElements") Long totalElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    /**
     * The helper constructor used in your services.
     */
    public PageDTO(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    /**
     * Standard constructor.
     */
    public PageDTO(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    // --- THE DEFINITIVE FIX IS HERE ---

    /**
     * By overriding this method and annotating it with @JsonIgnore, we are telling
     * Jackson to NEVER include the 'pageable' object in the JSON output.
     * This prevents the deserialization error on the cache read.
     */
    @Override
    @JsonIgnore
    public Pageable getPageable() {
        return super.getPageable();
    }

    /**
     * It's also a best practice to ignore the 'sort' object for the same reason,
     * as it's part of the Pageable and can also be complex.
     */
    @Override
    @JsonIgnore
    public Sort getSort() {
        return super.getSort();
    }
}
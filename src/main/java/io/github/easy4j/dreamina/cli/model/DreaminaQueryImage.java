package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Single image artifact from {@code result_json.images[]}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see DreaminaQueryResult#images()
 * @since 3.0.0
 * @deprecated 旧 result_json.images[].image_url 结构；Canvas 用 resourceId 与资源查询，不能靠字段映射自动完成下载；替代：RESOURCE_GET/DOWNLOAD。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class DreaminaQueryImage {

    /**
     * Downloadable signed image URL.
     */
    @JsonProperty("image_url")
    private String imageUrl;

    /**
     * Image width in pixels; {@code null} when not returned by the CLI.
     */
    private Integer width;

    /**
     * Image height in pixels; {@code null} when not returned by the CLI.
     */
    private Integer height;
}

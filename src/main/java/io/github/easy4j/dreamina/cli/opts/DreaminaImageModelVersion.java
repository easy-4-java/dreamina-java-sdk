package io.github.easy4j.dreamina.cli.opts;

import lombok.Getter;

/**
 * Image model version enum.
 * <p>
 * Covers both text-to-image and image-to-image scenarios; whether a specific version
 * is allowed for image-to-image is additionally constrained by the request object.
 * </p>
 *
 * @see DreaminaText2ImageRequest
 * @see DreaminaImage2ImageRequest
 * <p>
 * 同时覆盖文生图与图生图场景；图生图是否允许某一版本由请求对象额外约束。
 * </p>
 * <p>
 * 适配即梦 CLI v1.4.14（2026-07-21）：
 * <ul>
 *   <li>v1.4.4（2026-06-03）新增 Seedream 4.7 → {@link #MODEL_4_7}</li>
 *   <li>v1.4.12（2026-07-15）新增 Seedream 5.0 Pro → {@link #MODEL_5_0_PRO}</li>
 * </ul>
 * 4.0+ 模型同时支持图生图。
 * </p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
public enum DreaminaImageModelVersion {

    MODEL_3_0("3.0"),
    MODEL_3_1("3.1"),
    MODEL_4_0("4.0"),
    MODEL_4_1("4.1"),
    MODEL_4_5("4.5"),
    MODEL_4_6("4.6"),
    /** CLI v1.4.4（2026-06-03）新增。 */
    MODEL_4_7("4.7"),
    MODEL_5_0("5.0"),
    /**
     * CLI v1.4.12（2026-07-15）新增的 Seedream 5.0 Pro 旗舰版本。
     * <p>
     * CLI 参数值为 {@code 5.0Pro}，与人类可读名称“Seedream 5.0 Pro”不同。
     * </p>
     */
    MODEL_5_0_PRO("5.0Pro");

    private final String cliValue;

    DreaminaImageModelVersion(String cliValue) {
        this.cliValue = cliValue;
    }

    /**
     * Whether this version meets the minimum requirement for image-to-image (4.0+).
     *
     * @return true if this version can be used for image2image
     */
    public boolean supportsImageToImage() {
        return this.ordinal() >= MODEL_4_0.ordinal();
    }
}

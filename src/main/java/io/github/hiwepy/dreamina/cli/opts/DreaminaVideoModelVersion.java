package io.github.hiwepy.dreamina.cli.opts;

import lombok.Getter;

/**
 * 视频模型版本枚举。
 * <p>
 * 适配即梦 CLI v1.4.14：
 * <ul>
 *   <li>v1.4.8（2026-06-18）新增 seedance 2.0 mini → {@link #SEEDANCE_2_0_MINI}</li>
 *   <li>v1.3.2（2026-04-05）起 seedance 2.0_vip / seedance2.0fast_vip 通道提速</li>
 *   <li>v1.4.3（2026-05-07）起 seedance 2.0_vip 支持 1080P</li>
 *   <li>v1.4.10（2026-06-26）起 seedance 2.0_vip 支持 4K（参 {@link DreaminaVideoResolutionType#RESOLUTION_4K}）</li>
 *   <li>v1.4.10 将旧 3.x 名称调整为 Seedance 1.x；CLI v1.4.14 的单图生视频公开
 *       {@code seedance1.0fast}/{@code seedance1.5pro}，首尾帧公开 {@code seedance1.5pro}</li>
 * </ul>
 * </p>
 *
 * @author wandl
 * @since 1.0.0
 */
@Getter
public enum DreaminaVideoModelVersion {

    SEEDANCE_2_0_FAST("seedance2.0fast"),
    SEEDANCE_2_0("seedance2.0"),
    SEEDANCE_2_0_FAST_VIP("seedance2.0fast_vip"),
    SEEDANCE_2_0_VIP("seedance2.0_vip"),
    /** CLI v1.4.8（2026-06-18）新增的轻量版 Seedance 2.0 mini。 */
    SEEDANCE_2_0_MINI("seedance2.0mini"),
    /** Web/CLI 统一命名后的 Seedance 1.0 快速模型。 */
    SEEDANCE_1_0_FAST("seedance1.0fast"),
    /** Web/CLI 统一命名后的 Seedance 1.0 模型。 */
    SEEDANCE_1_0("seedance1.0"),
    /** Web/CLI 统一命名后的 Seedance 1.5 Pro 模型。 */
    SEEDANCE_1_5_PRO("seedance1.5pro"),

    /**
     * v1.4.10 起 CLI 已将 3.x 模型名调整为 Seedance 1.x 系列名；该枚举值仅作遗留兼容，
     * 新代码请使用 {@link #SEEDANCE_2_0_FAST} / {@link #SEEDANCE_2_0} / {@link #SEEDANCE_2_0_VIP}
     * / {@link #SEEDANCE_2_0_FAST_VIP} / {@link #SEEDANCE_2_0_MINI}。
     */
    @Deprecated
    MODEL_3_0("3.0"),
    @Deprecated
    MODEL_3_0_FAST("3.0fast"),
    @Deprecated
    MODEL_3_0_PRO("3.0pro"),
    @Deprecated
    MODEL_3_5_PRO("3.5pro"),
    /** image2video 接受的 CLI 别名。 */
    @Deprecated
    MODEL_3_0_FAST_UNDERSCORE("3.0_fast"),
    @Deprecated
    MODEL_3_0_PRO_UNDERSCORE("3.0_pro"),
    @Deprecated
    MODEL_3_5_PRO_UNDERSCORE("3.5_pro");

    private final String cliValue;

    DreaminaVideoModelVersion(String cliValue) {
        this.cliValue = cliValue;
    }

    /**
     * 是否可用于 {@code text2video}（CLI 仅 seedance 系列）。
     *
     * @return true 表示 seedance 系列
     */
    public boolean supportsText2Video() {
        return this == SEEDANCE_2_0
            || this == SEEDANCE_2_0_FAST
            || this == SEEDANCE_2_0_VIP
            || this == SEEDANCE_2_0_FAST_VIP
            || this == SEEDANCE_2_0_MINI;
    }

    /**
     * 是否可用于 {@code image2video}。
     *
     * @return true 表示单图生视频支持该模型
     */
    public boolean supportsImage2Video() {
        return this == SEEDANCE_1_0_FAST
            || this == SEEDANCE_1_5_PRO
            || supportsText2Video();
    }

    /**
     * 是否可用于 {@code frames2video}。
     *
     * @return true 表示首尾帧视频支持该模型
     */
    public boolean supportsFrames2Video() {
        return this == SEEDANCE_1_5_PRO || supportsText2Video();
    }

    /**
     * 是否可用于 {@code multimodal2video}。
     *
     * @return true 表示全能参考支持该模型
     */
    public boolean supportsMultimodal2Video() {
        return supportsText2Video();
    }

    /**
     * 按 CLI help 返回该模型允许的视频时长下限（秒）。
     */
    public int minDurationSeconds() {
        if (this == SEEDANCE_1_0 || this == SEEDANCE_1_0_FAST || this == SEEDANCE_1_5_PRO) {
            return 5;
        }
        if (this == MODEL_3_0 || this == MODEL_3_0_FAST || this == MODEL_3_0_PRO
            || this == MODEL_3_0_FAST_UNDERSCORE || this == MODEL_3_0_PRO_UNDERSCORE) {
            return 3;
        }
        if (this == MODEL_3_5_PRO || this == MODEL_3_5_PRO_UNDERSCORE) {
            return 4;
        }
        return 4;
    }

    /**
     * 按 CLI help 返回该模型允许的视频时长上限（秒）。
     */
    public int maxDurationSeconds() {
        if (this == SEEDANCE_1_0 || this == SEEDANCE_1_0_FAST) {
            return 10;
        }
        if (this == SEEDANCE_1_5_PRO) {
            return 12;
        }
        if (this == MODEL_3_0 || this == MODEL_3_0_FAST || this == MODEL_3_0_PRO
            || this == MODEL_3_0_FAST_UNDERSCORE || this == MODEL_3_0_PRO_UNDERSCORE) {
            return 10;
        }
        if (this == MODEL_3_5_PRO || this == MODEL_3_5_PRO_UNDERSCORE) {
            return 12;
        }
        return 15;
    }
}

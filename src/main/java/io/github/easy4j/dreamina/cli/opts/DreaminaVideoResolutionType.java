package io.github.easy4j.dreamina.cli.opts;

import lombok.Getter;

/**
 * 视频分辨率类型。
 * <p>
 * 适配即梦 CLI v1.4.x：
 * <ul>
 *   <li>默认/常用：{@link #RESOLUTION_720P}</li>
 *   <li>v1.4.3（2026-05-07）：{@link #RESOLUTION_1080P} 仅 {@code seedance2.0_vip} 可用</li>
 *   <li>v1.4.10（2026-06-26）：{@link #RESOLUTION_4K} 仅 {@code seedance2.0_vip} + VIP 账户可用</li>
 *   <li>v1.4.15（2026-08-01）：{@link #RESOLUTION_480P} 仅 Seedance 2.5（{@code seedance2.5}）可用</li>
 * </ul>
 * </p>
 * <p>
 * 调用方若传 4K 但模型版本非 {@code seedance2.0_vip}，CLI 会报错。建议在使用方做
 * 预校验（{@link DreaminaVideoModelVersion#SEEDANCE_2_0_VIP} / {@link DreaminaVideoModelVersion#SEEDANCE_2_5}）。
 * </p>
 *
 * @author wandl
 * @since 1.0.0
 */
@Getter
public enum DreaminaVideoResolutionType {

    RESOLUTION_720P("720p"),
    RESOLUTION_1080P("1080p"),
    /** CLI v1.4.15（2026-08-01）新增，仅 Seedance 2.5（{@code seedance2.5}）可用。 */
    RESOLUTION_480P("480p"),
    /** CLI v1.4.10（2026-06-26）新增，仅 seedance2.0_vip + VIP 账户可用。 */
    RESOLUTION_4K("4k");

    private final String cliValue;

    DreaminaVideoResolutionType(String cliValue) {
        this.cliValue = cliValue;
    }
}

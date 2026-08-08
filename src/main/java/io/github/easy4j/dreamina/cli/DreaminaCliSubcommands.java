package io.github.easy4j.dreamina.cli;

/**
 * Dreamina CLI subcommand literal constants: top-level commands grouped by capability;
 * {@link LoginSub} and {@link SessionSub} are second-level tokens after
 * {@code login} / {@code session}, used by the executor to assemble chains
 * such as {@code dreamina login checklogin} and {@code dreamina session create}.
 * <p>
 * Aligned with the command divisions in OpenClaw / Jimeng skill documentation;
 * specific flags are still appended by the caller via convenience methods or
 * {@link DreaminaCliExecutor#invoke(String, java.util.List)}.
 * </p>
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public final class DreaminaCliSubcommands {

    private DreaminaCliSubcommands() {}

    /**
     * Top-level built-in commands (help, task listing, etc.).
     */
    public static final class Builtin {

        private Builtin() {}

        /** {@code dreamina help}（可再接子命令名，如 {@code dreamina help login}） */
        public static final String HELP = "help";
    }

    /**
     * Account and session management: version check, credits, login state, session context, etc.
     */
    public static final class Account {

        private Account() {}

        /** {@code dreamina version} */
        public static final String VERSION = "version";

        /** {@code dreamina user_credit} */
        public static final String USER_CREDIT = "user_credit";

        /** {@code dreamina login} (only {@code --headless} is currently supported; {@code --debug} is deprecated since v1.4.1) */
        public static final String LOGIN = "login";

        /** {@code dreamina logout} */
        public static final String LOGOUT = "logout";

        /** {@code dreamina relogin} */
        public static final String RELOGIN = "relogin";

        /** {@code dreamina session} */
        public static final String SESSION = "session";
    }

    /**
     * Sub-actions under {@code dreamina login} (second-level token, placed after {@code login}).
     */
    public static final class LoginSub {

        private LoginSub() {}

        /**
         * {@code dreamina login checklogin ...}: polls to complete authorization in the headless / device-code flow.
         */
        public static final String CHECKLOGIN = "checklogin";
    }

    /**
     * Sub-actions under {@code dreamina session} (second-level token, placed after {@code session}).
     */
    public static final class SessionSub {

        private SessionSub() {}

        /** {@code dreamina session create} */
        public static final String CREATE = "create";

        /** {@code dreamina session list} */
        public static final String LIST = "list";

        /** {@code dreamina session ls}: official alias of {@link #LIST}. */
        public static final String LS = "ls";

        /** {@code dreamina session search} */
        public static final String SEARCH = "search";

        /** {@code dreamina session find}: official alias of {@link #SEARCH}. */
        public static final String FIND = "find";

        /** {@code dreamina session rename} */
        public static final String RENAME = "rename";

        /** {@code dreamina session update}: official alias of {@link #RENAME}. */
        public static final String UPDATE = "update";

        /** {@code dreamina session delete} */
        public static final String DELETE = "delete";

        /** {@code dreamina session rm}: official alias of {@link #DELETE}. */
        public static final String RM = "rm";
    }

    /**
     * Image generation and editing subcommands.
     */
    public static final class Image {

        private Image() {}

        /** {@code dreamina text2image} */
        public static final String TEXT2IMAGE = "text2image";

        /** {@code dreamina image2image} (image-to-image) */
        public static final String IMAGE2IMAGE = "image2image";

        /** {@code dreamina image_upscale} */
        public static final String IMAGE_UPSCALE = "image_upscale";
    }

    /**
     * Video generation subcommands (including multiple input modes for image-to-video).
     */
    public static final class Video {

        private Video() {}

        /** {@code dreamina text2video} */
        public static final String TEXT2VIDEO = "text2video";

        /** {@code dreamina image2video} (single-image driven) */
        public static final String IMAGE2VIDEO = "image2video";

        /** {@code dreamina frames2video} (first-last frames) */
        public static final String FRAMES2VIDEO = "frames2video";

        /** {@code dreamina multiframe2video} (multi-image storyboard) */
        public static final String MULTIFRAME2VIDEO = "multiframe2video";

        /** {@code dreamina multimodal2video} (multimodal synthesis) */
        public static final String MULTIMODAL2VIDEO = "multimodal2video";
    }

    /**
     * Task listing and result querying.
     */
    public static final class Task {

        private Task() {}

        /** {@code dreamina query_result} */
        public static final String QUERY_RESULT = "query_result";

        /** {@code dreamina list_task} */
        public static final String LIST_TASK = "list_task";
    }
}

package io.github.easy4j.dreamina.cli;

import io.github.easy4j.dreamina.util.DreaminaStrings;
import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCliSubcommands;
import io.github.easy4j.dreamina.exception.DreaminaCliExecutableFailureException;
import io.github.easy4j.dreamina.exception.DreaminaCliException;
import io.github.easy4j.dreamina.exception.DreaminaCliNonZeroExitException;
import io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException;
import io.github.easy4j.dreamina.cli.parser.DreaminaCliStructuredPayloadMapper;
import io.github.easy4j.dreamina.cli.parser.DreaminaCliOutputParser;
import io.github.easy4j.dreamina.cli.parser.DreaminaParsedFields;
import io.github.easy4j.dreamina.cli.opts.DreaminaFrames2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaImage2ImageRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaImage2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaImageUpscaleRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaListTaskRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaQueryResultRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaMultiframe2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaMultimodal2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaText2ImageRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaText2VideoRequest;
import io.github.easy4j.dreamina.cli.DreaminaCliResponse;
import io.github.easy4j.dreamina.cli.model.DreaminaCheckLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaDeviceLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaGenerateSubmit;
import io.github.easy4j.dreamina.cli.model.DreaminaHelp;
import io.github.easy4j.dreamina.cli.model.DreaminaLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaLogout;
import io.github.easy4j.dreamina.cli.model.DreaminaQueryResult;
import io.github.easy4j.dreamina.cli.model.DreaminaRelogin;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionDelete;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionList;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionMutation;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionSearch;
import io.github.easy4j.dreamina.cli.model.DreaminaTaskItem;
import io.github.easy4j.dreamina.cli.model.DreaminaUserCredit;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import io.github.easy4j.dreamina.cli.support.SubprocessExecutionSupport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

/**
 * 基于 Apache Commons Exec 的 Dreamina CLI 进程执行封装。
 * <p>
 * 设计要点（对齐 OpenClaw Jimeng CLI 技能的「能力边界 + 编排节奏」启示，但不承担业务编排）：
 * </p>
 * <ul>
 *   <li><b>能力分组</b>：内置 {@code help}、账号/会话（含 {@code login checklogin}、{@code session *}）、图片生成、视频生成、任务查询；
 *       与 {@link DreaminaCliSubcommands} 常量一致，上层可按 「CHECK(user_credit) → SUBMIT(gen) → POLL(query_result)」 拼装流程。</li>
 *   <li><b>扩展位</b>：{@link #invoke(String, List)} 与各 {@code xxx(List&lt;String&gt; additionalRawArgs)}
 *       过载允许挂载官方新增 flag，无需为每个参数加长方法签名。</li>
 *   <li><b>执行语义</b>：统一超时、流捕获与非零退出映射；不包含会员、配额或与业务 ApplicationService 的耦合。</li>
 *   <li><b>结构化视图</b>：{@code versionInfo}/{@code *Submit} 等系列便捷方法与 {@link DreaminaCliResponse}
 *       在<strong>同一条执行链路</strong>上绑定原始 {@link DreaminaCliResult}；解析沉淀于 {@link DreaminaCliStructuredPayloadMapper}。</li>
 * </ul>
 * <pre>
 * Usage:
 *   dreamina [flags]
 *
 * 即梦 official AIGC CLI tool for login, account, and generation workflows
 *
 * About:
 *   dreamina is the 即梦 official AIGC CLI tool.
 *
 * Quick start:
 *   1. Run "dreamina login" to complete OAuth device login.
 *   2. For headless login, run "dreamina login --headless", then "dreamina login checklogin --device_code=<device_code>".
 *   3. Run a generator command such as "dreamina text2image --prompt=\"a cat portrait\"".
 *   4. Use "dreamina query_result --submit_id=<id>" for async tasks, or "dreamina list_task" to review saved tasks.
 *   5. Use "dreamina user_credit" to check the current account credit balance.
 *
 * Tips:
 *   Run "dreamina <subcommand> -h" to view detailed help for any subcommand.
 *   Login now uses OAuth Device Flow and prints verification_uri, user_code, and device_code in the terminal.
 *   All generation operations consume credits.
 *   Seedance 2.0 family is a flagship video generation model family and is a strong choice when output quality matters most.
 *
 * Built-in Commands:
 *   help                 Help about any command
 *   list_task            List saved tasks with status and result summary
 *   login                Log in locally with OAuth Device Flow before using task and account commands
 *   logout               Clear the local OAuth login state
 *   query_result         Query the current result of an async generation task
 *   relogin              Clear the local OAuth login state and force a fresh OAuth login
 *   session              Manage sessions (create/list/search/rename/delete)
 *   user_credit          Show the current user's remaining credit balance
 *   version              Print build version and commit information
 *
 *
 * Generator Commands:
 *   frames2video         Submit a Dreamina first-last-frames video task
 *   image2image          Submit a Dreamina image-to-image task
 *   image2video          Animate one image into video; use multiframe2video for multi-image stories
 *   image_upscale        Submit a Dreamina image upscale task
 *   multiframe2video     Create a coherent video story from multiple images
 *   multimodal2video     Dreamina flagship video mode (全能参考 / formerly ref2video) with all-around references and Seedance 2.0
 *   text2image           Submit a Dreamina text-to-image task
 *   text2video           Submit a Dreamina text-to-video task
 *
 *
 * Examples:
 *   dreamina login
 *   dreamina login --headless
 *   dreamina login checklogin --device_code=<device_code> --poll=30
 *   dreamina logout
 *   dreamina relogin
 *   dreamina user_credit
 *   dreamina list_task --gen_status=success
 *   dreamina query_result --submit_id=550e8400-e29b-41d4-a716-446655440000
 *   dreamina text2image --prompt="a cat portrait" --ratio=1:1 --resolution_type=2k
 * </pre>
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Slf4j
@Getter
public class DreaminaCliExecutor {

    private final DreaminaCliProperties properties;

    /**
     * Maps raw CLI snapshots to structured payloads (no framework injection required, facilitating testing with {@code new DreaminaCliExecutor(props)}).
     */
    private final DreaminaCliStructuredPayloadMapper structuredPayloadMapper = new DreaminaCliStructuredPayloadMapper();

    /**
     * Constructs the executor with runtime configuration.
     *
     * @param properties CLI path, timeout, working directory, etc.; must not be null
     */
    public DreaminaCliExecutor(DreaminaCliProperties properties) {
        this.properties = Objects.requireNonNull(properties, "properties");
        SubprocessExecutionSupport.configureMaxConcurrentExecutions(properties.getMaxConcurrentExecutions());
    }

    // -------------------------------------------------------------------------
    // 帮助（help）
    // -------------------------------------------------------------------------

    /**
     * Invokes {@code dreamina help} to print the overall help or equivalent output.
     * <p>CLI 帮助（采集自本机 {@code dreamina help}）：</p>
     * <pre>
     * Usage:
     *   dreamina [flags]
     * 
     * 即梦 official AIGC CLI tool for login, account, and generation workflows
     * 
     * About:
     *   dreamina is the 即梦 official AIGC CLI tool.
     * 
     * Quick start:
     *   1. Run "dreamina login" to complete OAuth device login.
     *   2. For headless login, run "dreamina login --headless", then "dreamina login checklogin --device_code=<device_code>".
     *   3. Run a generator command such as "dreamina text2image --prompt=\"a cat portrait\"".
     *   4. Use "dreamina query_result --submit_id=<id>" for async tasks, or "dreamina list_task" to review saved tasks.
     *   5. Use "dreamina user_credit" to check the current account credit balance.
     * 
     * Tips:
     *   Run "dreamina <subcommand> -h" to view detailed help for any subcommand.
     *   Login now uses OAuth Device Flow and prints verification_uri, user_code, and device_code in the terminal.
     *   All generation operations consume credits.
     *   Seedance 2.0 family is a flagship video generation model family and is a strong choice when output quality matters most.
     * 
     * Built-in Commands:
     *   help                 Help about any command
     *   list_task            List saved tasks with status and result summary
     *   login                Log in locally with OAuth Device Flow before using task and account commands
     *   logout               Clear the local OAuth login state
     *   query_result         Query the current result of an async generation task
     *   relogin              Clear the local OAuth login state and force a fresh OAuth login
     *   session              Manage sessions (create/list/search/rename/delete)
     *   user_credit          Show the current user's remaining credit balance
     *   version              Print build version and commit information
     * 
     * 
     * Generator Commands:
     *   frames2video         Submit a Dreamina first-last-frames video task
     *   image2image          Submit a Dreamina image-to-image task
     *   image2video          Animate one image into video; use multiframe2video for multi-image stories
     *   image_upscale        Submit a Dreamina image upscale task
     *   multiframe2video     Create a coherent video story from multiple images
     *   multimodal2video     Dreamina flagship video mode (全能参考 / formerly ref2video) with all-around references and Seedance 2.0
     *   text2image           Submit a Dreamina text-to-image task
     *   text2video           Submit a Dreamina text-to-video task
     * 
     * 
     * Examples:
     *   dreamina login
     *   dreamina login --headless
     *   dreamina login checklogin --device_code=<device_code> --poll=30
     *   dreamina logout
     *   dreamina relogin
     *   dreamina user_credit
     *   dreamina list_task --gen_status=success
     *   dreamina query_result --submit_id=550e8400-e29b-41d4-a716-446655440000
     *   dreamina text2image --prompt="a cat portrait" --ratio=1:1 --resolution_type=2k
     * </pre>
     */
    public DreaminaCliResult help() {
        return invoke(DreaminaCliSubcommands.Builtin.HELP, Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina help <subcommand>} to view the help for a specific top-level subcommand (behavior defined by the CLI).
     *
     * @param subcommand 一级Subcommand name, e.g., {@link DreaminaCliSubcommands.Image#TEXT2IMAGE}; must not be null or blank
     */
    public DreaminaCliResult help(String subcommand) {
        return help(subcommand, Collections.emptyList());
    }

    /**
     * Same as above, with additional officially-supported parameter fragments appended.
     *
     * @param subcommand        子命令名；不得为 null 或空白
     * @param additionalRawArgs CLI suffix parameters; may be null
     */
    public DreaminaCliResult help(String subcommand, List<String> additionalRawArgs) {
        Objects.requireNonNull(subcommand, "subcommand");
        if (DreaminaStrings.isBlank(subcommand)) {
            throw new IllegalArgumentException("subcommand must not be blank");
        }
        CommandLine cmd = newSubcommandChain(DreaminaCliSubcommands.Builtin.HELP, subcommand.trim());
        appendCleanArgs(cmd, additionalRawArgs);
        return run(cmd);
    }

    // -------------------------------------------------------------------------
    // 账号与会话（version / user_credit / login / logout / relogin / session）
    // -------------------------------------------------------------------------

    /**
     * Invokes {@code dreamina version} to query local CLI version information (typically JSON).
     * <p>CLI 帮助（采集自本机 {@code dreamina version -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina version [flags]
     * 
     * Print build version and commit information
     * 
     * 
     * Flags:
     *   -h, --help   help for version
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina version
     * </pre>
     */
    public DreaminaCliResult version() {
        return invoke(DreaminaCliSubcommands.Account.VERSION, Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina user_credit} to query the raw CLI output related to user credits.
     * <p>CLI 帮助（采集自本机 {@code dreamina user_credit -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina user_credit [flags]
     * 
     * Query the current logged-in user's remaining Dreamina credits.
     * 
     * 
     * Flags:
     *   -h, --help   help for user_credit
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina user_credit
     * </pre>
     */
    public DreaminaCliResult userCredit() {
        return invoke(DreaminaCliSubcommands.Account.USER_CREDIT, Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina login} (default OAuth browser flow).
     *
     * @return Returns only when the process exits with zero and no timeout; otherwise throws a unified execution-layer exception
     */
    public DreaminaCliResult login() {
        return login(Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina login} with additional officially-supported suffix parameters (e.g., {@code --headless}).
     * <p>
     * 注意：CLI v1.4.1（2026-04-17）起登录方式更新，{@code --debug} 已不再支持；调用方若仍
     * 传 {@code --debug}，CLI 会原样回显并最终拒绝。排障请改为读取 {@code ~/.dreamina_cli/logs/}。
     * </p>
     * <p>CLI 帮助（采集自本机 {@code dreamina login -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina login [flags]
     * 
     * Reuse the current local OAuth login state when it is still valid; otherwise start OAuth Device Flow.
     * By default the CLI prints verification_uri, user_code, and device_code, then waits for authorization to complete.
     * With --headless, the CLI prints the authorization material and exits without polling checklogin.
     * The legacy browser callback and manual-import login flow are no longer used.
     * 
     * 
     * Flags:
     *       --headless   print OAuth authorization material and exit without polling checklogin
     *   -h, --help       help for login
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina login
     *   dreamina login --headless
     *   dreamina login checklogin --device_code=<device_code> --poll=30
     * </pre>
     */
    public DreaminaCliResult login(List<String> additionalRawArgs) {
        return invoke(DreaminaCliSubcommands.Account.LOGIN, additionalRawArgs);
    }

    /**
     * 调用 {@code dreamina login --headless}，进入无浏览器交互的设备码登录流程（后续常接
     * {@link #checkLogin(String, int, List)}）。
     *
     * @return 仅在进程零退出且无超时时返回；否则抛出统一的执行层异常
     */
    public DreaminaCliResult loginHeadless() {
        return loginHeadless(Collections.emptyList());
    }

    /**
     * Appends more raw parameters after {@code --headless}.
     * <p>
     * Note: {@code --debug} is no longer supported since CLI v1.4.1; do not pass it.
     * </p>
     *
     * @param additionalRawArgs CLI fragments to append after {@code --headless}; may be null
     */
    public DreaminaCliResult loginHeadless(List<String> additionalRawArgs) {
        List<String> merged = new ArrayList<>();
        merged.add("--headless");
        if (additionalRawArgs != null) {
            for (String a : additionalRawArgs) {
                if (a != null && !a.trim().isEmpty()) {
                    merged.add(a);
                }
            }
        }
        return login(merged);
    }

    /**
     * Invokes {@code dreamina login checklogin --device_code=... --poll=...} to poll for OAuth completion by device code.
     *
     * @param deviceCode device_code returned by the headless flow
     * @param pollSeconds Polling interval in seconds, corresponding to {@code --poll=}
     */
    public DreaminaCliResult checkLogin(String deviceCode, int pollSeconds) {
        return checkLogin(deviceCode, pollSeconds, Collections.emptyList());
    }

    /**
     * Same as above, with the ability to attach additional official flags (e.g., future CLI debug switches).
     * <p>CLI 帮助（采集自本机 {@code dreamina login checklogin -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina login checklogin [flags]
     * 
     * Check the authorization result for a prior headless OAuth Device Flow login.
     * Pass the device_code printed by "dreamina login --headless" or "dreamina relogin --headless".
     * --poll=N waits for up to N seconds; --poll=0 checks only once.
     * 
     * 
     * Flags:
     *       --device_code string   device code printed by a prior headless OAuth login
     *   -h, --help                 help for checklogin
     *       --poll int             wait for up to N seconds before timing out; 0 checks once
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina login checklogin --device_code=<device_code>
     *   dreamina login checklogin --device_code=<device_code> --poll=30
     * </pre>
     */
    public DreaminaCliResult checkLogin(String deviceCode, int pollSeconds, List<String> additionalRawArgs) {
        Objects.requireNonNull(deviceCode, "deviceCode");
        if (DreaminaStrings.isBlank(deviceCode)) {
            throw new IllegalArgumentException("deviceCode must not be blank");
        }
        if (pollSeconds < 0) {
            throw new IllegalArgumentException("pollSeconds must be non-negative");
        }
        CommandLine cmd = newSubcommandChain(
            DreaminaCliSubcommands.Account.LOGIN, DreaminaCliSubcommands.LoginSub.CHECKLOGIN);
        appendQuotedKv(cmd, "--device_code", deviceCode.trim());
        cmd.addArgument("--poll=" + pollSeconds, false);
        appendCleanArgs(cmd, additionalRawArgs);
        return run(cmd);
    }

    /**
     * Invokes {@code dreamina logout} to clear credentials (retaining config and local task DB behavior is CLI-defined).
     *
     * @return 仅在进程零退出且无超时时返回；否则抛出统一的执行层异常
     */
    public DreaminaCliResult logout() {
        return logout(Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina logout} with optional extra raw parameters.
     * <p>CLI 帮助（采集自本机 {@code dreamina logout -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina logout [flags]
     * 
     * Remove the local OAuth login state without touching tasks or config.
     * 
     * 
     * Flags:
     *   -h, --help   help for logout
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina logout
     * </pre>
     */
    public DreaminaCliResult logout(List<String> additionalRawArgs) {
        return invoke(DreaminaCliSubcommands.Account.LOGOUT, additionalRawArgs);
    }

    /**
     * Invokes {@code dreamina relogin} for account switching and similar scenarios.
     *
     * @return 仅在进程零退出且无超时时返回；否则抛出统一的执行层异常
     */
    public DreaminaCliResult relogin() {
        return relogin(Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina relogin} with optional extra raw parameters.
     * <p>CLI 帮助（采集自本机 {@code dreamina relogin -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina relogin [flags]
     * 
     * Remove the local OAuth login state first, then force a fresh OAuth Device Flow login.
     * By default the CLI prints verification_uri, user_code, and device_code, then waits for authorization to complete.
     * With --headless, the CLI prints the authorization material and exits without polling checklogin.
     * 
     * 
     * Flags:
     *       --headless   print OAuth authorization material and exit without polling checklogin
     *   -h, --help       help for relogin
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina relogin
     *   dreamina relogin --headless
     * </pre>
     */
    public DreaminaCliResult relogin(List<String> additionalRawArgs) {
        return invoke(DreaminaCliSubcommands.Account.RELOGIN, additionalRawArgs);
    }

    /**
     * Invokes {@code dreamina session} (no extra parameters).
     *
     * @return 仅在进程零退出且无超时时返回；否则抛出统一的执行层异常
     */
    public DreaminaCliResult session() {
        return session(Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina session} with subcommand-level parameters appended.
     * <p>CLI 帮助（采集自本机 {@code dreamina session -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina session [flags]
     * 
     * Manage Dreamina sessions (create, list, search, rename, delete).
     * 
     * A session is a container for organizing your creation history.
     * All generator commands accept a --session=<id> flag to submit tasks into a specific session.
     * 
     * Available Commands:
     *   create    Create a new session (auto-named or custom)
     *   list      List your recent sessions (alias: ls)
     *   search    Find a session ID by its name (alias: find)
     *   rename    Change a session's name (alias: update)
     *   delete    Delete a session (alias: rm)
     * 
     * Notes:
     * - All session commands require login (run "dreamina login" first).
     * - Session 0 is the default session. It cannot be renamed or deleted.
     * - Deleting a session will safely move its history back to the default session.
     * 
     * 
     * Flags:
     *   -h, --help   help for session
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   # 1. Create a session
     *   dreamina session create
     *   dreamina session create "My Video Project"
     * 
     *   # 2. List sessions (default 30; user-specified -n is capped at 100)
     *   dreamina session list
     *   dreamina session ls -n 100
     * 
     *   # 3. Find a session by name
     *   dreamina session search "Video"
     * 
     *   # 4. Rename a session
     *   dreamina session rename 10086 "New Project Name"
     * 
     *   # 5. Delete a session
     *   dreamina session rm 10086
     * </pre>
     */
    public DreaminaCliResult session(List<String> additionalRawArgs) {
        return invoke(DreaminaCliSubcommands.Account.SESSION, additionalRawArgs);
    }

    /**
     * Structured view of {@link #session()}: when no subcommand is given, the CLI prints session subcommand help (plain text, see {@link DreaminaCliResponse#getCombinedText()}).
     */
    public DreaminaCliResponse<DreaminaHelp> sessionInfo() {
        return structuredPayloadMapper.mapHelp(DreaminaCliSubcommands.Account.SESSION, session());
    }

    /**
     * Structured view of {@link #session(List)} (e.g., {@code dreamina session -h}).
     *
     * @param additionalRawArgs Flags passed through to the CLI
     */
    public DreaminaCliResponse<DreaminaHelp> sessionInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapHelp(DreaminaCliSubcommands.Account.SESSION, session(additionalRawArgs));
    }

    /**
     * {@code dreamina session create}; no extra parameters (equivalent to {@link #sessionCreate(List)} with an empty list).
     */
    public DreaminaCliResult sessionCreate() {
        return sessionCreate(Collections.emptyList());
    }

    /**
     * {@code dreamina session create}; creation parameters (name, model, etc.) are defined by the official CLI and passed via {@code additionalRawArgs}.
     * <p>CLI 帮助（采集自本机 {@code dreamina session create -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina session create [name] [flags]
     * 
     * Create a new session.
     * 
     * Args:
     * - name (optional): session name. If omitted, the backend generates a default name like "新对话 01-04 10:30".
     * 
     * Notes:
     * - name must be 1-50 characters after trimming spaces.
     * 
     * 
     * 
     * Flags:
     *   -h, --help   help for create
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina session create
     *   dreamina session create "我的视频项目"
     * </pre>
     */
    public DreaminaCliResult sessionCreate(List<String> additionalRawArgs) {
        return runSessionSub(DreaminaCliSubcommands.SessionSub.CREATE, null, null, additionalRawArgs);
    }

    /**
     * {@code dreamina session list}; no filter parameters.
     */
    public DreaminaCliResult sessionList() {
        return sessionList(Collections.emptyList());
    }

    /**
     * {@code dreamina session list}; filtering/pagination via {@code additionalRawArgs}.
     * <p>CLI 帮助（采集自本机 {@code dreamina session list -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina session list [flags]
     * 
     * List recent sessions.
     * 
     * By default it requests and shows the latest 30 sessions from the backend, ordered by pinned first and then updated time descending.
     * If you pass -n/--max-count, the CLI requests that many sessions from the backend.
     * User-specified values are capped at 100.
     * 
     * Output:
     * - Table columns: ID, NAME, PINNED, UPDATED_AT
     * - UPDATED_AT is formatted as local time: YYYY-MM-DD HH:MM
     * 
     * 
     * 
     * Flags:
     *   -h, --help            help for list
     *   -n, --max-count int   maximum number of sessions to display (default 30)
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina session list
     *   dreamina session list -n 5
     *   dreamina session list -n 100
     * </pre>
     */
    public DreaminaCliResult sessionList(List<String> additionalRawArgs) {
        return runSessionSub(DreaminaCliSubcommands.SessionSub.LIST, null, null, additionalRawArgs);
    }

    /**
     * {@code dreamina session ls}：{@code session list} 的官方别名；常用 {@code -n/--max-count} 可通过
     * {@code additionalRawArgs} 传入。
     *
     * @param additionalRawArgs Optional flags; may be null
     */
    public DreaminaCliResult sessionLs(List<String> additionalRawArgs) {
        return runSessionSub(DreaminaCliSubcommands.SessionSub.LS, null, null, additionalRawArgs);
    }

    /**
     * {@code dreamina session search <searchTerm>}; no extra flags.
     *
     * @param searchTerm Search keyword; may be null (consistent with {@link #sessionSearch(String, List)}, null skips positional argument)
     */
    public DreaminaCliResult sessionSearch(String searchTerm) {
        return sessionSearch(searchTerm, Collections.emptyList());
    }

    /**
     * {@code dreamina session search}. If {@code searchTerm} is non-empty, appends one positional argv after the {@code search} subcommand.
     * <p>CLI 帮助（采集自本机 {@code dreamina session search -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina session search <name> [flags]
     * 
     * Search sessions by name.
     * 
     * The CLI requests the first 100 sessions from the backend and matches records whose name contains the input string. Matching is case-sensitive.
     * 
     * 
     * 
     * Flags:
     *   -h, --help   help for search
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina session search "视频"
     *   dreamina session search "我的年度总结"
     * </pre>
     */
    public DreaminaCliResult sessionSearch(String searchTerm, List<String> additionalRawArgs) {
        return runSessionSub(DreaminaCliSubcommands.SessionSub.SEARCH, searchTerm, null, additionalRawArgs);
    }

    /**
     * {@code dreamina session find <searchTerm>}: official alias of {@code session search}.
     *
     * @param searchTerm        Search term; may be null
     * @param additionalRawArgs Other flags; may be null
     */
    public DreaminaCliResult sessionFind(String searchTerm, List<String> additionalRawArgs) {
        return runSessionSub(DreaminaCliSubcommands.SessionSub.FIND, searchTerm, null, additionalRawArgs);
    }

    /**
     * {@code dreamina session rename <sessionId> <newName>}.
     */
    public DreaminaCliResult sessionRename(String sessionId, String newName) {
        return sessionRename(sessionId, newName, Collections.emptyList());
    }

    /**
     * {@code dreamina session rename <sessionId> <newName>} (the last two are independent argv; spaces are handled by Commons Exec escaping).
     * <p>CLI 帮助（采集自本机 {@code dreamina session rename -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina session rename <session_id> <new_name> [flags]
     * 
     * Rename a session.
     * 
     * This command only exposes renaming. Pin/unpin is intentionally not exposed in CLI.
     * 
     * Args:
     * - session_id: the target session ID
     * - new_name: the new session name (1-50 characters)
     * 
     * Notes:
     * - Session 0 is the default session and cannot be renamed.
     * - Negative session IDs are invalid.
     * 
     * 
     * 
     * Flags:
     *   -h, --help   help for rename
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina session rename 10086 "2024年度宣传片"
     * </pre>
     */
    public DreaminaCliResult sessionRename(String sessionId, String newName, List<String> additionalRawArgs) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(newName, "newName");
        if (DreaminaStrings.isBlank(sessionId) || DreaminaStrings.isBlank(newName)) {
            throw new IllegalArgumentException("sessionId and newName must not be blank");
        }
        return runSessionSub(
            DreaminaCliSubcommands.SessionSub.RENAME, sessionId.trim(), newName.trim(), additionalRawArgs);
    }

    /**
     * {@code dreamina session update <sessionId> <newName>}: official alias of {@code session rename}.
     */
    public DreaminaCliResult sessionUpdate(String sessionId, String newName) {
        return sessionUpdate(sessionId, newName, Collections.emptyList());
    }

    /**
     * {@code dreamina session update <sessionId> <newName>}。
     *
     * @param sessionId         Current session ID; must not be null/blank
     * @param newName           New display name; must not be null/blank
     * @param additionalRawArgs Other flags; may be null
     */
    public DreaminaCliResult sessionUpdate(String sessionId, String newName, List<String> additionalRawArgs) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(newName, "newName");
        if (DreaminaStrings.isBlank(sessionId) || DreaminaStrings.isBlank(newName)) {
            throw new IllegalArgumentException("sessionId and newName must not be blank");
        }
        return runSessionSub(
            DreaminaCliSubcommands.SessionSub.UPDATE, sessionId.trim(), newName.trim(), additionalRawArgs);
    }

    /**
     * {@code dreamina session delete <sessionId>}.
     * <p>CLI 帮助（采集自本机 {@code dreamina session delete -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina session delete <session_id> [flags]
     * 
     * Delete a session.
     * 
     * Notes:
     * - Session 0 is the default session and cannot be deleted.
     * - Negative session IDs are invalid.
     * - This operation is safe. The backend performs a soft delete and will move related history records back to the default session.
     * 
     * 
     * 
     * Flags:
     *   -h, --help   help for delete
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina session delete 10085
     *   dreamina session rm 10085
     * </pre>
     */
    public DreaminaCliResult sessionDelete(String sessionId) {
        return sessionDelete(sessionId, Collections.emptyList());
    }

    /**
     * {@code dreamina session delete <sessionId>}。
     *
     * @param sessionId         Session ID to delete; must not be null/blank
     * @param additionalRawArgs Extension flags such as {@code --force}; may be null
     */
    public DreaminaCliResult sessionDelete(String sessionId, List<String> additionalRawArgs) {
        Objects.requireNonNull(sessionId, "sessionId");
        if (DreaminaStrings.isBlank(sessionId)) {
            throw new IllegalArgumentException("sessionId must not be blank");
        }
        return runSessionSub(DreaminaCliSubcommands.SessionSub.DELETE, sessionId.trim(), null, additionalRawArgs);
    }

    /**
     * {@code dreamina session rm <sessionId>}: official alias of {@code session delete}.
     */
    public DreaminaCliResult sessionRm(String sessionId) {
        return sessionRm(sessionId, Collections.emptyList());
    }

    /**
     * {@code dreamina session rm <sessionId>}。
     *
     * @param sessionId         要删除的会话标识；不得为 null/空白
     * @param additionalRawArgs 其它 flag；可为 null
     */
    public DreaminaCliResult sessionRm(String sessionId, List<String> additionalRawArgs) {
        Objects.requireNonNull(sessionId, "sessionId");
        if (DreaminaStrings.isBlank(sessionId)) {
            throw new IllegalArgumentException("sessionId must not be blank");
        }
        return runSessionSub(DreaminaCliSubcommands.SessionSub.RM, sessionId.trim(), null, additionalRawArgs);
    }

    /**
     * 拼装 {@code dreamina session &lt;verb&gt;} 及可选的一到两个位置参数后执行。
     */
    private DreaminaCliResult runSessionSub(
        String verb,
        String firstPositional,
        String secondPositional,
        List<String> additionalRawArgs) {
        Objects.requireNonNull(verb, "verb");
        CommandLine cmd = newSubcommandChain(DreaminaCliSubcommands.Account.SESSION, verb);
        if (DreaminaStrings.isNotBlank(firstPositional)) {
            cmd.addArgument(firstPositional.trim(), true);
        }
        if (DreaminaStrings.isNotBlank(secondPositional)) {
            cmd.addArgument(secondPositional.trim(), true);
        }
        appendCleanArgs(cmd, additionalRawArgs);
        return run(cmd);
    }

    // -------------------------------------------------------------------------
    // 图片生成（text2image / image2image / image_upscale）
    // -------------------------------------------------------------------------

    /**
     * Invokes {@code dreamina text2image --prompt=...} to trigger a text-to-image task.
     *
     * @param prompt Required prompt text
     */
    public DreaminaCliResult text2Image(String prompt) {
        return text2Image(prompt, Collections.emptyList());
    }

    /**
     * Same as above, with extra raw parameter fragments (each passed as a single argv, no shell splitting).
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina text2image -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult text2Image(String prompt, List<String> additionalRawArgs) {
        Objects.requireNonNull(prompt, "prompt");
        return runWithPromptFlag(
            DreaminaCliSubcommands.Image.TEXT2IMAGE,
            prompt,
            withDefaultFlag(additionalRawArgs, "--resolution_type", "2k"));
    }

    /**
     * Invokes {@code dreamina text2image} using a strongly-typed request object.
     * <p>
     * 将 Jimeng 技能中沉淀的 ratio / model / resolution / session / poll 约束固定在请求模型中，
     * 避免上层重复手写原始 flag。
     * </p>
     *
     * @param request Text-to-image request; must not be null
     * @return Raw CLI execution snapshot
     */
    public DreaminaCliResult text2Image(DreaminaText2ImageRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Image.TEXT2IMAGE, request.toCliArgs());
    }

    /**
     * Invokes {@code dreamina image2image}: image-to-image, requires a reference image list and edit prompt.
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina image2image -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult image2Image(String imagesCsv, String prompt, List<String> additionalRawArgs) {
        Objects.requireNonNull(imagesCsv, "imagesCsv");
        Objects.requireNonNull(prompt, "prompt");
        CommandLine cmd = newSubcommand(DreaminaCliSubcommands.Image.IMAGE2IMAGE);
        appendQuotedKv(cmd, "--images", imagesCsv);
        appendQuotedKv(cmd, "--prompt", prompt);
        appendCleanArgs(cmd, withDefaultFlag(additionalRawArgs, "--resolution_type", "2k"));
        return run(cmd);
    }

    /**
     * Invokes {@code dreamina image2image} using a strongly-typed request object.
     *
     * @param request Image-to-image request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult image2Image(DreaminaImage2ImageRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Image.IMAGE2IMAGE, request.toCliArgs());
    }

    /**
     * {@code dreamina image_upscale}, no extra parameters.
     */
    public DreaminaCliResult imageUpscale() {
        return imageUpscale(Collections.emptyList());
    }

    /**
     * Invokes {@code dreamina image_upscale}; required parameters are provided by the caller in {@code additionalRawArgs}.
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina image_upscale -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult imageUpscale(List<String> additionalRawArgs) {
        return invoke(
            DreaminaCliSubcommands.Image.IMAGE_UPSCALE,
            withDefaultFlag(additionalRawArgs, "--resolution_type", "2k"));
    }

    /**
     * Invokes {@code dreamina image_upscale} using a strongly-typed request object.
     *
     * @param request Image upscale request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult imageUpscale(DreaminaImageUpscaleRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Image.IMAGE_UPSCALE, request.toCliArgs());
    }

    // -------------------------------------------------------------------------
    // 视频生成（text2video / image2video / frames2video / multiframe2video / multimodal2video）
    // -------------------------------------------------------------------------

    /**
     * Invokes {@code dreamina text2video --prompt=...} to trigger text-to-video.
     *
     * @param prompt 必填提示词
     */
    public DreaminaCliResult text2video(String prompt) {
        return text2video(prompt, Collections.emptyList());
    }

    /**
     * Text-to-video with extra raw parameters (e.g., {@code --duration=}, {@code --model_version=}, {@code --poll=}).
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina text2video -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult text2video(String prompt, List<String> additionalRawArgs) {
        Objects.requireNonNull(prompt, "prompt");
        return runWithPromptFlag(
            DreaminaCliSubcommands.Video.TEXT2VIDEO,
            prompt,
            withDefaultFlag(additionalRawArgs, "--video_resolution", "720p"));
    }

    /**
     * Invokes {@code dreamina text2video} using a strongly-typed request object.
     *
     * @param request Text-to-video request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult text2video(DreaminaText2VideoRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Video.TEXT2VIDEO, request.toCliArgs());
    }

    /**
     * 调用 {@code dreamina image2video}；CLI v1.4.14 起 {@code --prompt} 必填，
     * 因此调用方必须在 {@code additionalRawArgs} 中提供提示词。
     *
     * @param imagePath         {@code --image=} local path, required
     * @param additionalRawArgs 其它 flag；可为 null
     */
    public DreaminaCliResult image2video(String imagePath, List<String> additionalRawArgs) {
        return image2video(imagePath, null, additionalRawArgs);
    }

    /**
     * Invokes {@code dreamina image2video}: single reference image drives video generation.
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina image2video -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult image2video(String imagePath, String prompt, List<String> additionalRawArgs) {
        Objects.requireNonNull(imagePath, "imagePath");
        if (DreaminaStrings.isBlank(prompt) && !containsFlag(additionalRawArgs, "--prompt")) {
            throw new IllegalArgumentException("prompt is required by Dreamina CLI v1.4.14+");
        }
        CommandLine cmd = newSubcommand(DreaminaCliSubcommands.Video.IMAGE2VIDEO);
        appendQuotedKv(cmd, "--image", imagePath);
        if (DreaminaStrings.isNotBlank(prompt)) {
            appendQuotedKv(cmd, "--prompt", prompt);
        }
        appendCleanArgs(cmd, withDefaultFlag(additionalRawArgs, "--video_resolution", "720p"));
        return run(cmd);
    }

    /**
     * Invokes {@code dreamina image2video} using a strongly-typed request object.
     *
     * @param request Single-image-to-video request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult image2video(DreaminaImage2VideoRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Video.IMAGE2VIDEO, request.toCliArgs());
    }

    /**
     * {@code dreamina frames2video}, uses an empty list when parameters are filled via CLI interaction or subsequent calls.
     */
    public DreaminaCliResult frames2video() {
        return frames2video(Collections.emptyList());
    }

    /**
     * {@code dreamina frames2video}: first-last-frame transition; required parameters go in {@code additionalRawArgs} (e.g., {@code --first=} / {@code --last=}).
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina frames2video -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult frames2video(List<String> additionalRawArgs) {
        return invoke(
            DreaminaCliSubcommands.Video.FRAMES2VIDEO,
            withDefaultFlag(additionalRawArgs, "--video_resolution", "720p"));
    }

    /**
     * Invokes {@code dreamina frames2video} using a strongly-typed request object.
     *
     * @param request First-last-frame video request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult frames2video(DreaminaFrames2VideoRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Video.FRAMES2VIDEO, request.toCliArgs());
    }

    /**
     * {@code dreamina multiframe2video}, no extra parameters.
     */
    public DreaminaCliResult multiframe2video() {
        return multiframe2video(Collections.emptyList());
    }

    /**
     * {@code dreamina multiframe2video}: multi-storyboard narrative; required parameters go in {@code additionalRawArgs}.
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina multiframe2video -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult multiframe2video(List<String> additionalRawArgs) {
        return invoke(
            DreaminaCliSubcommands.Video.MULTIFRAME2VIDEO,
            withDefaultFlag(additionalRawArgs, "--video_resolution", "720p"));
    }

    /**
     * Invokes {@code dreamina multiframe2video} using a strongly-typed request object.
     *
     * @param request Multi-frame storyboard video request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult multiframe2video(DreaminaMultiframe2VideoRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Video.MULTIFRAME2VIDEO, request.toCliArgs());
    }

    /**
     * {@code dreamina multimodal2video}, no extra parameters.
     */
    public DreaminaCliResult multimodal2video() {
        return multimodal2video(Collections.emptyList());
    }

    /**
     * {@code dreamina multimodal2video}: multimodal synthesis; required parameters go in {@code additionalRawArgs}.
     * <p>CLI v1.4.14/v1.4.15 的完整参数契约由 {@code dreamina multimodal2video -h} 与
     * {@code dreamina-v1.4.14-help.snapshot.tsv} / {@code dreamina-v1.4.15-help.snapshot.tsv}
     * 双向契约测试共同维护，避免在执行器 JavaDoc 中复制易漂移的第二份帮助文本。</p>
     */
    public DreaminaCliResult multimodal2video(List<String> additionalRawArgs) {
        return invoke(
            DreaminaCliSubcommands.Video.MULTIMODAL2VIDEO,
            withDefaultFlag(additionalRawArgs, "--video_resolution", "720p"));
    }

    /**
     * Invokes {@code dreamina multimodal2video} using a strongly-typed request object.
     *
     * @param request Multimodal video request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult multimodal2video(DreaminaMultimodal2VideoRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Video.MULTIMODAL2VIDEO, request.toCliArgs());
    }

    // -------------------------------------------------------------------------
    // 任务查询（query_result / list_task）
    // -------------------------------------------------------------------------

    /**
     * Invokes {@code dreamina query_result --submit_id=...} to query task status or artifact information.
     *
     * @param submitId Dreamina-side submit ID
     */
    public DreaminaCliResult queryResult(String submitId) {
        return queryResult(submitId, Collections.emptyList());
    }

    /**
     * Same as above, with extra raw parameters appended.
     * <p>CLI 帮助（采集自本机 {@code dreamina query_result -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina query_result [flags]
     * 
     * Query one async task by submit_id.
     * 
     * 
     * Flags:
     *       --download_dir string   download result media into the target directory
     *   -h, --help                  help for query_result
     *       --submit_id string      task submit_id
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina query_result --submit_id=3f6eb41f425d23a3
     * </pre>
     */
    public DreaminaCliResult queryResult(String submitId, List<String> additionalRawArgs) {
        Objects.requireNonNull(submitId, "submitId");
        CommandLine cmd = newSubcommand(DreaminaCliSubcommands.Task.QUERY_RESULT);
        appendQuotedKv(cmd, "--submit_id", submitId);
        appendCleanArgs(cmd, additionalRawArgs);
        return run(cmd);
    }

    /**
     * Invokes {@code dreamina query_result} using a strongly-typed request object.
     *
     * @param request Query request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult queryResult(DreaminaQueryResultRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Task.QUERY_RESULT, request.toCliArgs());
    }

    /**
     * Invokes {@code dreamina list_task} to enumerate the task list.
     *
     * @return CLI aggregated stdout/stderr snapshot
     */
    public DreaminaCliResult listTask() {
        return listTask(Collections.emptyList());
    }

    /**
     * {@code dreamina list_task} with filter parameters (e.g., {@code --gen_status=success}).
     * <p>CLI 帮助（采集自本机 {@code dreamina list_task -h}）：</p>
     * <pre>
     * Usage:
     *   dreamina list_task [flags]
     * 
     * List tasks saved for the current logged-in user.
     * 
     * 
     * Flags:
     *       --gen_status string      filter by gen_status
     *       --gen_task_type string   filter by gen_task_type
     *   -h, --help                   help for list_task
     *       --limit int              max number of tasks to return (default 20)
     *       --offset int             offset for pagination
     *       --submit_id string       filter by submit_id
     * 
     * Global Flags:
     *       --version   print build version information
     * 
     * Examples:
     *   dreamina list_task
     *   dreamina list_task --gen_status=success
     * </pre>
     */
    public DreaminaCliResult listTask(List<String> additionalRawArgs) {
        return invoke(DreaminaCliSubcommands.Task.LIST_TASK, additionalRawArgs);
    }

    /**
     * Invokes {@code dreamina list_task} using a strongly-typed request object.
     *
     * @param request List filter request; must not be null
     * @return CLI 原始执行快照
     */
    public DreaminaCliResult listTask(DreaminaListTaskRequest request) {
        Objects.requireNonNull(request, "request");
        return invoke(DreaminaCliSubcommands.Task.LIST_TASK, request.toCliArgs());
    }

    // -------------------------------------------------------------------------
    // 结构化便捷封装（所见即所得：{@link DreaminaCliResponse}）
    // -------------------------------------------------------------------------

    /**
     * Structured view of {@link #version()}.
     *
     * @return Binds the raw snapshot with {@link DreaminaVersion}
     */
    public DreaminaCliResponse<DreaminaVersion> versionInfo() {
        return structuredPayloadMapper.mapVersion(version());
    }

    /**
     * Structured view of {@link #userCredit()}.
     */
    public DreaminaCliResponse<DreaminaUserCredit> userCreditInfo() {
        return structuredPayloadMapper.mapUserCredit(userCredit());
    }

    /**
     * Structured view of {@link #help()}.
     */
    public DreaminaCliResponse<DreaminaHelp> helpInfo() {
        return structuredPayloadMapper.mapHelp(null, help());
    }

    /**
     * Structured view of {@link #help(String)}.
     *
     * @param subcommand Target subcommand name
     */
    public DreaminaCliResponse<DreaminaHelp> helpInfo(String subcommand) {
        return structuredPayloadMapper.mapHelp(subcommand, help(subcommand));
    }

    /**
     * Structured view of {@link #help(String, List)}.
     *
     * @param subcommand        目标子命令名
     * @param additionalRawArgs Additional raw parameters
     */
    public DreaminaCliResponse<DreaminaHelp> helpInfo(String subcommand, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapHelp(subcommand, help(subcommand, additionalRawArgs));
    }

    /**
     * Structured view of {@link #login()}.
     */
    public DreaminaCliResponse<DreaminaLogin> loginInfo() {
        return structuredPayloadMapper.mapLogin(login());
    }

    /**
     * Structured view of {@link #login(List)}.
     */
    public DreaminaCliResponse<DreaminaLogin> loginInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapLogin(login(additionalRawArgs));
    }

    /**
     * Structured view of {@link #logout()}.
     */
    public DreaminaCliResponse<DreaminaLogout> logoutInfo() {
        return structuredPayloadMapper.mapLogout(logout());
    }

    /**
     * Structured view of {@link #logout(List)}.
     */
    public DreaminaCliResponse<DreaminaLogout> logoutInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapLogout(logout(additionalRawArgs));
    }

    /**
     * Structured view of {@link #relogin()}.
     */
    public DreaminaCliResponse<DreaminaRelogin> reloginInfo() {
        return structuredPayloadMapper.mapRelogin(relogin());
    }

    /**
     * Structured view of {@link #relogin(List)}.
     */
    public DreaminaCliResponse<DreaminaRelogin> reloginInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapRelogin(relogin(additionalRawArgs));
    }

    /**
     * Structured view of {@link #checkLogin(String, int)}.
     */
    public DreaminaCliResponse<DreaminaCheckLogin> checkLoginInfo(String deviceCode, int pollSeconds) {
        return structuredPayloadMapper.mapCheckLogin(checkLogin(deviceCode, pollSeconds));
    }

    /**
     * Structured view of {@link #checkLogin(String, int, List)}.
     */
    public DreaminaCliResponse<DreaminaCheckLogin> checkLoginInfo(
        String deviceCode, int pollSeconds, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapCheckLogin(checkLogin(deviceCode, pollSeconds, additionalRawArgs));
    }

    /**
     * Structured view of {@link #loginHeadless()}: may be Device Flow JSON or merely a "reuse local OAuth" hint.
     */
    public DreaminaCliResponse<DreaminaLogin> loginHeadlessInfo() {
        return structuredPayloadMapper.mapLogin(loginHeadless());
    }

    /**
     * Structured view of {@link #loginHeadless(List)}.
     *
     * @param additionalRawArgs Headless suffix parameters
     */
    public DreaminaCliResponse<DreaminaLogin> loginHeadlessInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapLogin(loginHeadless(additionalRawArgs));
    }

    /**
     * Explicitly parses Device Flow JSON (fields are empty when stdout is not JSON).
     */
    public DreaminaCliResponse<DreaminaDeviceLogin> deviceLoginMaterial(DreaminaCliResult loginStdOutSnapshot) {
        return structuredPayloadMapper.mapDeviceLogin(loginStdOutSnapshot);
    }

    /**
     * Structured view of {@link #sessionList()}.
     */
    public DreaminaCliResponse<DreaminaSessionList> sessionListInfo() {
        return structuredPayloadMapper.mapSessionList(sessionList());
    }

    /**
     * Structured view of {@link #sessionList(List)}.
     *
     * @param additionalRawArgs 透传到 CLI 的 flag
     */
    public DreaminaCliResponse<DreaminaSessionList> sessionListInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapSessionList(sessionList(additionalRawArgs));
    }

    /**
     * Structured view of {@link #sessionLs(List)}.
     *
     * @param additionalRawArgs 透传到 CLI 的 flag，E.g., {@code -n=100}
     */
    public DreaminaCliResponse<DreaminaSessionList> sessionLsInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapSessionList(sessionLs(additionalRawArgs));
    }

    /**
     * Structured view of {@link #sessionSearch(String)}.
     *
     * @param searchTerm Search keyword; may be null (consistent with underlying CLI semantics)
     */
    public DreaminaCliResponse<DreaminaSessionSearch> sessionSearchInfo(String searchTerm) {
        return structuredPayloadMapper.mapSessionSearch(searchTerm, sessionSearch(searchTerm));
    }

    /**
     * Structured view of {@link #sessionSearch(String, List)}.
     */
    public DreaminaCliResponse<DreaminaSessionSearch> sessionSearchInfo(
        String searchTerm, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapSessionSearch(searchTerm, sessionSearch(searchTerm, additionalRawArgs));
    }

    /**
     * Structured view of {@link #sessionFind(String, List)}.
     */
    public DreaminaCliResponse<DreaminaSessionSearch> sessionFindInfo(
        String searchTerm, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapSessionSearch(searchTerm, sessionFind(searchTerm, additionalRawArgs));
    }

    /**
     * Structured view of {@link #sessionCreate()}.
     */
    public DreaminaCliResponse<DreaminaSessionMutation> sessionCreateInfo() {
        return structuredPayloadMapper.mapSessionMutation(sessionCreate());
    }

    /**
     * Structured view of {@link #sessionCreate(List)}.
     *
     * @param additionalRawArgs Session name or other official flags
     */
    public DreaminaCliResponse<DreaminaSessionMutation> sessionCreateInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapSessionMutation(sessionCreate(additionalRawArgs));
    }

    /**
     * Structured view of {@link #sessionRename(String, String)}.
     */
    public DreaminaCliResponse<DreaminaSessionMutation> sessionRenameInfo(String sessionId, String newName) {
        return structuredPayloadMapper.mapSessionMutation(sessionRename(sessionId, newName));
    }

    /**
     * Structured view of {@link #sessionRename(String, String, List)}.
     */
    public DreaminaCliResponse<DreaminaSessionMutation> sessionRenameInfo(
        String sessionId, String newName, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapSessionMutation(sessionRename(sessionId, newName, additionalRawArgs));
    }

    /**
     * Structured view of {@link #sessionUpdate(String, String, List)}.
     */
    public DreaminaCliResponse<DreaminaSessionMutation> sessionUpdateInfo(
        String sessionId, String newName, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapSessionMutation(sessionUpdate(sessionId, newName, additionalRawArgs));
    }

    /**
     * Structured view of {@link #listTask()}.
     */
    public DreaminaCliResponse<List<DreaminaTaskItem>> listTaskInfo() {
        return structuredPayloadMapper.mapTaskList(listTask());
    }

    /**
     * Structured view of {@link #listTask(List)}.
     */
    public DreaminaCliResponse<List<DreaminaTaskItem>> listTaskInfo(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapTaskList(listTask(additionalRawArgs));
    }

    /**
     * Structured view of {@link #listTask(DreaminaListTaskRequest)}.
     */
    public DreaminaCliResponse<List<DreaminaTaskItem>> listTaskInfo(DreaminaListTaskRequest request) {
        return structuredPayloadMapper.mapTaskList(listTask(request));
    }

    /**
     * Structured view of {@link #queryResult(String)}.
     *
     * @param submitId Submit ID
     */
    public DreaminaCliResponse<DreaminaQueryResult> queryResultInfo(String submitId) {
        return structuredPayloadMapper.mapQueryResult(queryResult(submitId));
    }

    /**
     * Structured view of {@link #queryResult(String, List)}.
     */
    public DreaminaCliResponse<DreaminaQueryResult> queryResultInfo(String submitId, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapQueryResult(queryResult(submitId, additionalRawArgs));
    }

    /**
     * Structured view of {@link #queryResult(DreaminaQueryResultRequest)}.
     */
    public DreaminaCliResponse<DreaminaQueryResult> queryResultInfo(DreaminaQueryResultRequest request) {
        return structuredPayloadMapper.mapQueryResult(queryResult(request));
    }

    /**
     * Structured submit view of {@link #text2Image(String, List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> text2ImageSubmit(String prompt, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(text2Image(prompt, additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #text2Image(DreaminaText2ImageRequest)}.
     *
     * @param request Text-to-image request
     * @return Raw snapshot and structured submit result
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> text2ImageSubmit(DreaminaText2ImageRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(text2Image(request));
    }

    /**
     * Structured submit view of {@link #image2Image(String, String, List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> image2ImageSubmit(
        String imagesCsv, String prompt, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(image2Image(imagesCsv, prompt, additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #image2Image(DreaminaImage2ImageRequest)}.
     *
     * @param request Image-to-image request
     * @return 原始快照与结构化提交结果
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> image2ImageSubmit(DreaminaImage2ImageRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(image2Image(request));
    }

    /**
     * Structured submit view of {@link #imageUpscale(List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> imageUpscaleSubmit(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(imageUpscale(additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #imageUpscale(DreaminaImageUpscaleRequest)}.
     *
     * @param request Image upscale request
     * @return 原始快照与结构化提交结果
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> imageUpscaleSubmit(DreaminaImageUpscaleRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(imageUpscale(request));
    }

    /**
     * Structured submit view of {@link #text2video(String, List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> text2VideoSubmit(String prompt, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(text2video(prompt, additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #text2video(DreaminaText2VideoRequest)}.
     *
     * @param request Text-to-video request
     * @return 原始快照与结构化提交结果
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> text2VideoSubmit(DreaminaText2VideoRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(text2video(request));
    }

    /**
     * Structured submit view of {@link #image2video(String, String, List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> image2VideoSubmit(
        String imagePath, String prompt, List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(image2video(imagePath, prompt, additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #image2video(DreaminaImage2VideoRequest)}.
     *
     * @param request Single-image-to-video request
     * @return 原始快照与结构化提交结果
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> image2VideoSubmit(DreaminaImage2VideoRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(image2video(request));
    }

    /**
     * Structured submit view of {@link #frames2video(List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> frames2VideoSubmit(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(frames2video(additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #frames2video(DreaminaFrames2VideoRequest)}.
     *
     * @param request First-last-frame video request
     * @return 原始快照与结构化提交结果
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> frames2VideoSubmit(DreaminaFrames2VideoRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(frames2video(request));
    }

    /**
     * Structured submit view of {@link #multiframe2video(List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> multiframe2VideoSubmit(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(multiframe2video(additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #multiframe2video(DreaminaMultiframe2VideoRequest)}.
     *
     * @param request 多帧故事视频请求
     * @return 原始快照与结构化提交结果
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> multiframe2VideoSubmit(
        DreaminaMultiframe2VideoRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(multiframe2video(request));
    }

    /**
     * Structured submit view of {@link #multimodal2video(List)}.
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> multimodal2VideoSubmit(List<String> additionalRawArgs) {
        return structuredPayloadMapper.mapGenerateSubmit(multimodal2video(additionalRawArgs));
    }

    /**
     * Structured submit view of {@link #multimodal2video(DreaminaMultimodal2VideoRequest)}.
     *
     * @param request Multimodal video request
     * @return 原始快照与结构化提交结果
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> multimodal2VideoSubmit(
        DreaminaMultimodal2VideoRequest request) {
        return structuredPayloadMapper.mapGenerateSubmit(multimodal2video(request));
    }

    /**
     * Generic escape hatch: maps any subcommand result to a "generate submit" view (fields are mostly empty if JSON doesn't match).
     *
     * @param raw Pre-obtained CLI snapshot; must not be null
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> mapGenerateSubmitOnly(DreaminaCliResult raw) {
        return structuredPayloadMapper.mapGenerateSubmit(raw);
    }

    /**
     * Generic: maps {@link DreaminaQueryResult}.
     *
     * @param raw 事先取得的 CLI snapshot; must not be null
     */
    public DreaminaCliResponse<DreaminaQueryResult> mapQueryResultOnly(DreaminaCliResult raw) {
        return structuredPayloadMapper.mapQueryResult(raw);
    }

    /**
     * Generic: maps {@link List<DreaminaTaskItem>}.
     *
     * @param raw 事先取得的 CLI 快照；不得为 null
     */
    public DreaminaCliResponse<List<DreaminaTaskItem>> mapTaskListOnly(DreaminaCliResult raw) {
        return structuredPayloadMapper.mapTaskList(raw);
    }

    /**
     * Generic: maps {@link DreaminaHelp}.
     *
     * @param topic Help topic; may be null
     * @param raw   CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaHelp> mapHelpOnly(String topic, DreaminaCliResult raw) {
        return structuredPayloadMapper.mapHelp(topic, raw);
    }

    /**
     * Exposes the underlying mapper: allows the upper layer to compose custom logic or substitute strategies in tests.
     *
     * @return Non-null default mapper instance
     */
    public DreaminaCliStructuredPayloadMapper structuredPayloadMapper() {
        return structuredPayloadMapper;
    }

    /**
     * Generic escape hatch: appends any Dreamina-supported top-level subcommand and subsequent argv.
     * <p>Applicable when the official CLI adds new capabilities before this module.</p>
     *
     * @param subcommand        Top-level subcommand name (e.g., {@code DreaminaCliSubcommands.Image#TEXT2IMAGE}); must not be empty
     * @param additionalRawArgs Parameters after the subcommand; may be null
     */
    public DreaminaCliResult invoke(String subcommand, List<String> additionalRawArgs) {
        Objects.requireNonNull(subcommand, "subcommand");
        if (DreaminaStrings.isBlank(subcommand)) {
            throw new IllegalArgumentException("subcommand must not be blank");
        }
        CommandLine cmd = newSubcommand(subcommand.trim());
        appendCleanArgs(cmd, additionalRawArgs);
        return run(cmd);
    }

    /**
     * Assembles the executable {@link CommandLine} root command from configuration.
     */
    private CommandLine baseCommandLine() {
        return new CommandLine(properties.getExecutable());
    }

    /**
     * Appends a top-level subcommand under the root executable; the returned command line can have more flags attached.
     */
    private CommandLine newSubcommand(String subcommand) {
        return newSubcommandChain(subcommand);
    }

    /**
     * 追加从子命令起的连续 argv 段（不含可执行文件路径），用于 {@code dreamina login checklogin}、
     * {@code dreamina session create} 等多级子命令。
     *
     * @param subcommandTokens At least one segment; each must be a non-blank subcommand token
     */
    CommandLine newSubcommandChain(String... subcommandTokens) {
        if (subcommandTokens == null || subcommandTokens.length == 0) {
            throw new IllegalArgumentException("subcommandTokens must be non-empty");
        }
        CommandLine cmd = baseCommandLine();
        for (String token : subcommandTokens) {
            if (DreaminaStrings.isBlank(token)) {
                throw new IllegalArgumentException("subcommand token must not be null/blank");
            }
            cmd.addArgument(token.trim());
        }
        return cmd;
    }

    /**
     * Shared {@code --prompt=} assembly logic for text-to-image / text-to-video, reducing duplication.
     */
    private DreaminaCliResult runWithPromptFlag(String subcommand, String prompt, List<String> additionalRawArgs) {
        CommandLine cmd = newSubcommand(subcommand);
        appendQuotedKv(cmd, "--prompt", prompt);
        appendCleanArgs(cmd, additionalRawArgs);
        return run(cmd);
    }

    /**
     * Assembles a {@code --key=value} style parameter with {@code handleQuoting=true} to avoid space or shell special character issues.
     */
    private static void appendQuotedKv(CommandLine cmd, String key, String value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        if (!key.startsWith("--")) {
            throw new IllegalArgumentException("CLI key must start with '--', got: " + key);
        }
        String prefix = key.endsWith("=") ? key.substring(0, key.length() - 1) : key;
        cmd.addArgument(prefix + "=" + value, true);
    }

    /**
     * Appends CLI fragments one by one after filtering blank entries.
     */
    private static void appendCleanArgs(CommandLine cmd, List<String> args) {
        if (args == null || args.isEmpty()) {
            return;
        }
        for (String a : args) {
            if (a != null && !a.trim().isEmpty()) {
                cmd.addArgument(a, false);
            }
        }
    }

    /**
     * CLI v1.4.14 made image/video resolution mandatory; fills in a stable default when the raw parameter escape hatch doesn't explicitly provide one.
     */
    private static List<String> withDefaultFlag(
        List<String> additionalRawArgs,
        String flag,
        String defaultValue) {
        if (containsFlag(additionalRawArgs, flag)) {
            return additionalRawArgs;
        }
        List<String> normalized = new ArrayList<>();
        if (Objects.nonNull(additionalRawArgs)) {
            normalized.addAll(additionalRawArgs);
        }
        normalized.add(flag + "=" + defaultValue);
        return normalized;
    }

    private static boolean containsFlag(List<String> additionalRawArgs, String flag) {
        if (Objects.isNull(additionalRawArgs) || additionalRawArgs.isEmpty()) {
            return false;
        }
        for (String argument : additionalRawArgs) {
            if (DreaminaStrings.isBlank(argument)) {
                continue;
            }
            String normalized = argument.trim();
            if (flag.equals(normalized) || normalized.startsWith(flag + "=")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes the command line with Commons Exec + Watchdog, completing unified result and exception semantics.
     */
    private DreaminaCliResult run(CommandLine commandLine) {
        long timeoutMs = properties.getCommandTimeoutMillis();
        if (timeoutMs <= 0) {
            throw new IllegalStateException("dreamina.cli.command-timeout-millis must be positive");
        }

        File workingDirectory = resolveWorkingDirectory();
        SubprocessExecutionSupport.ExecutionRequest request =
                new SubprocessExecutionSupport.ExecutionRequest(commandLine, workingDirectory, null, timeoutMs);

        try {
            SubprocessExecutionSupport.RunSession session = executeSubprocess(request);
            return completeAfterWait(
                    commandLine,
                    timeoutMs,
                    session.getStdout(),
                    session.getStderr(),
                    session.getHandler(),
                    session.getWatchdog(),
                    session.isWaitTimedOut(),
                    null);
        } catch (IOException e) {
            throw failedToStart(commandLine, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DreaminaCliException("Interrupted while awaiting Dreamina CLI subprocess", e, null);
        }
    }

    /**
     * Resolves and validates the working directory configuration.
     */
    private File resolveWorkingDirectory() {
        String wdProperty = properties.getWorkingDirectory();
        if (wdProperty == null || wdProperty.trim().isEmpty()) {
            return null;
        }
        File wd = new File(wdProperty.trim());
        if (!wd.isDirectory()) {
            throw new DreaminaCliExecutableFailureException(
                    "dreamina.cli.working-directory is not an existing directory: " + wd.getAbsolutePath(), null);
        }
        return wd;
    }

    /**
     * Starts the subprocess (package-visible, for test injection of failure scenarios).
     */
    SubprocessExecutionSupport.RunSession executeSubprocess(SubprocessExecutionSupport.ExecutionRequest request)
            throws IOException, InterruptedException {
        return SubprocessExecutionSupport.execute(request);
    }

    /**
     * Creates the process executor used by {@link #run(CommandLine)} (package-visible, for test injection of subclasses that throw {@link IOException}).
     *
     * @deprecated Subprocess execution has been migrated to {@link SubprocessExecutionSupport}; retained for backward compatibility with legacy test override points.
     */
    @Deprecated
    DefaultExecutor newRunExecutor() {
        return new DefaultExecutor();
    }

    /**
     * After the subprocess completes, parses the output and maps to {@link DreaminaCliResult} or throws an execution-layer exception (package-visible, for test handler injection).
     *
     * @param asyncFailureOverride Test-only injection: when non-null, overrides {@link DefaultExecuteResultHandler#getException()} result
     */
    DreaminaCliResult completeAfterWait(
        CommandLine commandLine,
        long timeoutMs,
        ByteArrayOutputStream out,
        ByteArrayOutputStream err,
        DefaultExecuteResultHandler handler,
        ExecuteWatchdog watchdog,
        boolean waitTimedOut,
        Exception asyncFailureOverride) {
        String stdoutStr = new String(out.toByteArray(), StandardCharsets.UTF_8);
        String stderrStr = new String(err.toByteArray(), StandardCharsets.UTF_8);
        DreaminaParsedFields parsed = DreaminaCliOutputParser.parseBestEffort(stdoutStr, stderrStr);

        // --- 超时：Watchdog 结束进程或 handler 等待超时，优先抛出超时异常 ---
        if (waitTimedOut || watchdog.killedProcess()) {
            DreaminaCliResult partial = snapshot(stdoutStr, stderrStr, readExitQuietly(handler), parsed);
            throw new DreaminaCliTimeoutException(
                "Dreamina CLI timed out after " + timeoutMs + " ms: " + commandLine, partial);
        }

        // --- ExecuteException：通常对应非零退出或进程被破坏 ---
        Exception asyncFailure = asyncFailureOverride != null ? asyncFailureOverride : handler.getException();
        if (asyncFailure instanceof ExecuteException) {
            ExecuteException ex = (ExecuteException) asyncFailure;
            DreaminaCliResult failed = snapshot(stdoutStr, stderrStr, normalizeExitValue(ex.getExitValue()), parsed);
            throw new DreaminaCliNonZeroExitException(
                "Dreamina CLI failed (exitCode=" + ex.getExitValue() + "): " + commandLine, failed);
        }
        if (asyncFailure != null) {
            DreaminaCliResult partial = snapshot(stdoutStr, stderrStr, readExitQuietly(handler), parsed);
            throw failedAsync(commandLine, asyncFailure, partial);
        }

        final int exit;
        try {
            exit = handler.getExitValue();
        } catch (IllegalStateException e) {
            throw missingExitCode(commandLine, e, snapshot(stdoutStr, stderrStr, null, parsed));
        }

        if (exit != 0) {
            DreaminaCliResult failed = snapshot(stdoutStr, stderrStr, exit, parsed);
            throw nonZeroExitWithoutExecuteException(commandLine, exit, failed);
        }

        return DreaminaCliResult.builder()
            .stdout(stdoutStr)
            .stderr(stderrStr)
            .exitCode(exit)
            .success(true)
            .parsed(parsed)
            .build();
    }

    /**
     * Silently reads the async handler exit code.
     */
    private static Integer readExitQuietly(DefaultExecuteResultHandler handler) {
        try {
            int v = handler.getExitValue();
            return normalizeExitValue(v);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Normalizes Commons Exec's "undefined" sentinel value to {@code null}.
     */
    private static Integer normalizeExitValue(int raw) {
        if (raw == org.apache.commons.exec.Executor.INVALID_EXITVALUE) {
            return null;
        }
        return raw;
    }

    private static DreaminaCliResult snapshot(
        String stdoutStr, String stderrStr, Integer exitCode, DreaminaParsedFields parsed) {
        return DreaminaCliResult.builder()
            .stdout(stdoutStr == null ? "" : stdoutStr)
            .stderr(stderrStr == null ? "" : stderrStr)
            .exitCode(exitCode)
            .success(false)
            .parsed(parsed)
            .build();
    }

    /**
     * Unified exception when the subprocess cannot be started (package-visible, for test coverage of spawn failure branches).
     */
    static DreaminaCliExecutableFailureException failedToStart(CommandLine commandLine, IOException cause) {
        log.warn("Dreamina CLI spawn failed commandLine={}, message={}", commandLine, cause.getMessage());
        return new DreaminaCliExecutableFailureException(
            "Dreamina CLI could not be started (check PATH or executable path): " + commandLine, cause);
    }

    /**
     * Async failure that is not an {@link ExecuteException} (package-visible, for test coverage).
     */
    static DreaminaCliException failedAsync(
        CommandLine commandLine, Exception asyncFailure, DreaminaCliResult partial) {
        return new DreaminaCliException(
            "Dreamina CLI async failure: " + commandLine + " cause=" + asyncFailure.getMessage(),
            asyncFailure,
            partial);
    }

    /**
     * Process completed but exit code could not be read (package-visible, for test coverage).
     */
    static DreaminaCliException missingExitCode(
        CommandLine commandLine, IllegalStateException cause, DreaminaCliResult partial) {
        return new DreaminaCliException(
            "Dreamina CLI completed without observable exit code: " + commandLine, cause, partial);
    }

    /**
     * Non-zero exit without an {@link ExecuteException} wrapper (package-visible, for test coverage).
     */
    static DreaminaCliNonZeroExitException nonZeroExitWithoutExecuteException(
        CommandLine commandLine, int exitCode, DreaminaCliResult failed) {
        return new DreaminaCliNonZeroExitException(
            "Dreamina CLI non-zero exit (exitCode=" + exitCode + "): " + commandLine, failed);
    }
}

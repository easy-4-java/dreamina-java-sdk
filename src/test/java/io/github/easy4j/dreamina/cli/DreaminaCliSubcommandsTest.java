package io.github.easy4j.dreamina.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaCliSubcommandsTest {

    @Test
    void shouldDefineBuiltinHelp() {
        assertEquals("help", DreaminaCliSubcommands.Builtin.HELP);
    }

    @Test
    void shouldDefineAccountCommands() {
        assertEquals("version", DreaminaCliSubcommands.Account.VERSION);
        assertEquals("user_credit", DreaminaCliSubcommands.Account.USER_CREDIT);
        assertEquals("login", DreaminaCliSubcommands.Account.LOGIN);
        assertEquals("logout", DreaminaCliSubcommands.Account.LOGOUT);
        assertEquals("relogin", DreaminaCliSubcommands.Account.RELOGIN);
        assertEquals("session", DreaminaCliSubcommands.Account.SESSION);
    }

    @Test
    void shouldDefineLoginSubcommands() {
        assertEquals("checklogin", DreaminaCliSubcommands.LoginSub.CHECKLOGIN);
    }

    @Test
    void shouldDefineSessionSubcommands() {
        assertEquals("create", DreaminaCliSubcommands.SessionSub.CREATE);
        assertEquals("list", DreaminaCliSubcommands.SessionSub.LIST);
        assertEquals("ls", DreaminaCliSubcommands.SessionSub.LS);
        assertEquals("search", DreaminaCliSubcommands.SessionSub.SEARCH);
        assertEquals("find", DreaminaCliSubcommands.SessionSub.FIND);
        assertEquals("rename", DreaminaCliSubcommands.SessionSub.RENAME);
        assertEquals("update", DreaminaCliSubcommands.SessionSub.UPDATE);
        assertEquals("delete", DreaminaCliSubcommands.SessionSub.DELETE);
        assertEquals("rm", DreaminaCliSubcommands.SessionSub.RM);
    }

    @Test
    void shouldDefineImageCommands() {
        assertEquals("text2image", DreaminaCliSubcommands.Image.TEXT2IMAGE);
        assertEquals("image2image", DreaminaCliSubcommands.Image.IMAGE2IMAGE);
        assertEquals("image_upscale", DreaminaCliSubcommands.Image.IMAGE_UPSCALE);
    }

    @Test
    void shouldDefineVideoCommands() {
        assertEquals("text2video", DreaminaCliSubcommands.Video.TEXT2VIDEO);
        assertEquals("image2video", DreaminaCliSubcommands.Video.IMAGE2VIDEO);
        assertEquals("frames2video", DreaminaCliSubcommands.Video.FRAMES2VIDEO);
        assertEquals("multiframe2video", DreaminaCliSubcommands.Video.MULTIFRAME2VIDEO);
        assertEquals("multimodal2video", DreaminaCliSubcommands.Video.MULTIMODAL2VIDEO);
    }

    @Test
    void shouldDefineTaskCommands() {
        assertEquals("query_result", DreaminaCliSubcommands.Task.QUERY_RESULT);
        assertEquals("list_task", DreaminaCliSubcommands.Task.LIST_TASK);
    }
}

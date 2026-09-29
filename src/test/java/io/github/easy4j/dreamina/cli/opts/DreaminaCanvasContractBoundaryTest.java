package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 检查 CLI 发起前可确定的参数边界，避免无效提交与重复扣费。
 */
class DreaminaCanvasContractBoundaryTest {
    private static final String ID = "12345678-1234-1234-1234-123456789012";
    private static final String OTHER = "87654321-4321-4321-4321-210987654321";

    private static String repeat(char value, int length) {
        char[] result = new char[length];
        Arrays.fill(result, value);
        return new String(result);
    }

    @Test
    void validatesPaginationAndNumericRanges() {
        assertEquals(Arrays.asList("voice", "list", "--offset=0", "--count=100"),
                DreaminaCanvasVoiceListRequest.builder().offset(0L).count(100L).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasVoiceListRequest.builder().offset(-1L).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasVoiceListRequest.builder().count(0L).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasVoiceListRequest.builder().count(101L).build().toArguments());
        assertTrue(DreaminaCanvasNodeCreateImageRequest.builder().count(1L).build().toArguments().contains("--count=1"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().count((long) Integer.MAX_VALUE + 1).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateVideoRequest.builder().duration(new BigDecimal("-0.1")).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeFindRequest.builder().limit(51L).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeFindRequest.builder().offset(1001L).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasLsRequest.builder().limit(101L).build().toArguments());
    }

    @Test
    void validatesIdsEnumsTitlesAndLiteralPrompt() {
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().projectId("not-a-uuid").build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().title(repeat('x', 513)).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().mode("unknown").build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeFindRequest.builder().type(Collections.singletonList("invented")).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().prompt("bad\u0000prompt").build().toArguments());
        assertEquals("--prompt=  中文\n", DreaminaCanvasNodeCreateImageRequest.builder().prompt("  中文\n").build().toCliArgs().get(0));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasOperationStatusRequest.builder().operationRef("bad").build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasOperationStatusRequest.builder().operationRef("  ").build().toArguments());
        assertThrows(UnsupportedOperationException.class, () -> DreaminaCanvasVersionRequest.builder().build().toArguments().add("extra"));
    }

    @Test
    void approvalAndBatchIdentitiesAreConsistent() {
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().creditToken("t").creditCeiling(1L).run(true).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().waitForCompletion(true).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().timeout(Duration.ofSeconds(1)).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCreateImageRequest.builder().run(false).creditToken("t").build().toArguments());
        assertTrue(DreaminaCanvasNodeCreateImageRequest.builder().run(true).waitForCompletion(true).creditCeiling(1L).build().toArguments().contains("--credit-ceiling=1"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeRunRequest.builder().nodeId(Arrays.asList("node_a", "node_b")).submitId(Collections.singletonList(ID)).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeRunRequest.builder().nodeId(Arrays.asList("node_a", "node_b")).submitId(Arrays.asList(ID, ID)).build().toArguments());
        assertEquals(2, DreaminaCanvasNodeRunRequest.builder().nodeId(Arrays.asList("node_a", "node_b")).submitId(Arrays.asList(ID, OTHER)).build().getSubmitId().size());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasResourceUploadRequest.builder().build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasResourceUploadRequest.builder().file("a").sourceUrl("https://example.test/a").build().toArguments());
        assertTrue(DreaminaCanvasResourceUploadRequest.builder().file("a.png").build().toArguments().contains("--file=a.png"));
    }

    @Test
    void durationAndRepeatedValuesPreserveCliMeaning() {
        List<String> args = DreaminaCanvasOperationWaitRequest.builder().operationRef(ID).timeout(Duration.ZERO)
                .interval(Duration.ofMillis(500)).build().toArguments();
        assertTrue(args.contains("--timeout=0ns"));
        assertTrue(args.contains("--interval=500000000ns"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasOperationWaitRequest.builder().operationRef(ID).interval(Duration.ZERO).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasOperationWaitRequest.builder().operationRef(ID).timeout(Duration.ofSeconds(-1)).build().toArguments());
        List<String> refs = DreaminaCanvasNodeShowRequest.builder().nodeId(Arrays.asList("node_first", "node_second")).build().toCliArgs();
        assertEquals(Arrays.asList("--node-id=node_first", "--node-id=node_second"), refs);
        assertEquals(Arrays.asList("model", "find", "--type=image", "--", "one", "two"),
                DreaminaCanvasModelFindRequest.builder().type("image").name(Arrays.asList("one", "two")).build().toArguments());
    }

    @Test
    void internalEncoderProtectsDurationOverflowAndNullableCollections() {
        DreaminaCanvasArguments values = new DreaminaCanvasArguments();
        values.flag("nullable", (String) null);
        values.flag("nullable", (Boolean) null);
        values.flag("nullable", (Number) null);
        values.flag("nullable", (Duration) null);
        values.flag("nullable", (List<String>) null);
        values.position((String) null);
        values.position((List<String>) null);
        assertEquals(Collections.emptyList(), values.render());
        assertThrows(IllegalArgumentException.class, () -> values.flag("timeout", Duration.ofSeconds(Long.MAX_VALUE)));
        values.flag("active", Boolean.TRUE);
        values.flag("count", 1L);
        values.flag("tag", Arrays.asList("first", "second"));
        values.position("--literal");
        values.position(Arrays.asList("next", "last"));
        values.yes(true);
        assertEquals(Arrays.asList("--active=true", "--count=1", "--tag=first", "--tag=second", "--yes", "--", "--literal", "next", "last"), values.render());
    }

    @Test
    void internalValidatorAcceptsOnlyEncodedNanoseconds() {
        DreaminaCanvasArguments request = new DreaminaCanvasArguments();
        request.position(ID);
        request.flag("timeout", "1s");
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasContract.validate(DreaminaCanvasCommand.OPERATION_WAIT, request));
        request.options.clear();
        request.flag("timeout", "badns");
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasContract.validate(DreaminaCanvasCommand.OPERATION_WAIT, request));
        request.options.clear();
        request.flag("timeout", "0ns");
        DreaminaCanvasContract.validate(DreaminaCanvasCommand.OPERATION_WAIT, request);
    }
}

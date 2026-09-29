package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasOperationStatusResult;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasResourceDownloadResult;
import io.github.easy4j.dreamina.cli.opts.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;

/**
 * 对已完成的真实生成执行强类型恢复与下载验收；不会提交新任务或消耗积分。
 */
public final class DreaminaCanvasRealCommandCoverageMain {
    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            throw new IllegalArgumentException("projectId nodeId submitId resourceId output required");
        }
        DreaminaCanvasCliExecutor cli = new DreaminaCanvasCliExecutor();
        DreaminaCanvasResponse<DreaminaCanvasOperationStatusResult> operation = cli.operationStatus(DreaminaCanvasOperationStatusRequest.builder().projectId(args[0]).operationRef(args[2]).build());
        require(operation.isSuccess(), "operation status");
        require("succeeded".equals(operation.getData().getState()), "operation terminal state");
        require(cli.operationWait(DreaminaCanvasOperationWaitRequest.builder().projectId(args[0]).operationRef(args[2]).timeout(Duration.ofSeconds(10)).interval(Duration.ofSeconds(1)).build()).isSuccess(), "operation wait");
        require(cli.nodeShow(DreaminaCanvasNodeShowRequest.builder().projectId(args[0]).nodeId(Collections.singletonList(args[1])).build()).isSuccess(), "node show");
        require(cli.resourceGet(DreaminaCanvasResourceGetRequest.builder().projectId(args[0]).resourceId(args[3]).build()).isSuccess(), "resource get");
        DreaminaCanvasResponse<DreaminaCanvasResourceDownloadResult> download = cli.resourceDownload(DreaminaCanvasResourceDownloadRequest.builder().projectId(args[0]).resourceId(args[3]).output(args[4]).build());
        require(download.isSuccess(), "resource download");
        require(Files.size(Paths.get(args[4])) == download.getData().getSize(), "download size");
        System.out.println("Typed real acceptance: operation status/wait, node show, resource get/download passed; bytes=" + download.getData().getSize() + " sha256=" + download.getData().getSha256());
    }

    private static void require(boolean value, String label) {
        if (!value) {
            throw new IllegalStateException(label);
        }
    }
}

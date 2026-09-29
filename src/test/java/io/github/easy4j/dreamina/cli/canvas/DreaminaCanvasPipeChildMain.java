package io.github.easy4j.dreamina.cli.canvas;

import java.nio.file.Paths;

/**
 * 子进程测试夹具：先退出父进程，验证后代持有或写入输出管道。
 */
public final class DreaminaCanvasPipeChildMain {
    private DreaminaCanvasPipeChildMain() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "child".equals(args[0])) {
            if (args.length > 1 && "overflow".equals(args[1])) {
                Thread.sleep(150);
                byte[] bytes = new byte[8192];
                java.util.Arrays.fill(bytes, (byte) 'x');
                System.out.write(bytes);
                System.out.flush();
            } else {
                Thread.sleep(1500);
            }
            return;
        }
        String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
        String testClasses = Paths.get("target", "test-classes").toAbsolutePath().toString();
        new ProcessBuilder(javaBin, "-cp", testClasses, DreaminaCanvasPipeChildMain.class.getName(), "child", args[0])
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        System.out.println("{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":\"1.0.1\"}}");
    }
}

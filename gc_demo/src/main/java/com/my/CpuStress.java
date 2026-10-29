package com.my;

public class CpuStress {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("用法：java -jar xxx.jar [mode]");
            System.err.println("mode列表：");
            System.err.println("  full      100%满CPU");
            System.err.println("  load80    80%波动CPU（你之前的版本）");
            System.err.println("  gc        频繁GC高CPU");
            return;
        }
        String mode = args[0];
        switch (mode) {
            case "full":
                fullCpu();
                break;
            case "load80":
                load80Cpu();
                break;
            case "gc":
                gcStress();
                break;
            default:
                System.err.println("未知模式：" + mode);
        }
    }

    /** 满负载CPU */
    public static void fullCpu() {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("启动满CPU压测，核心数：" + cores);
        for (int i = 0; i < cores; i++) {
            new Thread(() -> {
                double num = Math.random();
                while (true) {
                    num = Math.sin(num) * Math.log(Math.abs(num) + 1.1);
                }
            }).start();
        }
    }

    /** 80% 带随机波动CPU */
    public static void load80Cpu() {
        final double TARGET_LOAD = 0.80;
        final long CYCLE_MS = 20;
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.printf("启动80%%波动CPU压测，核心数：%d，周期：%dms%n", cores, CYCLE_MS);
        for (int i = 0; i < cores; i++) {
            new Thread(() -> {
                double num = Math.random();
                while (true) {
                    long busyTime = (long) (CYCLE_MS * TARGET_LOAD);
                    long sleepTime = CYCLE_MS - busyTime;
                    double randFactor = 0.85 + Math.random() * 0.3;
                    busyTime = (long) (busyTime * randFactor);
                    sleepTime = CYCLE_MS - busyTime;
                    if (sleepTime < 0) sleepTime = 0;

                    long start = System.nanoTime();
                    long busyNano = busyTime * 1_000_000L;
                    while (System.nanoTime() - start < busyNano) {
                        num = Math.sin(num) * Math.log(Math.abs(num) + 1.1);
                    }
                    try {
                        if (sleepTime > 0) Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();
        }
    }

    /** GC压测 */
    public static void gcStress() {
        System.out.println("启动GC压测");
        while (true) {
            Object[] arr = new Object[1000];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = new String("test-" + i);
            }
        }
    }
}

package com.anland.shell.ds;

import java.util.ArrayList;
import java.util.List;

/** Test-only command recorder. Put the probe jar before the APK on CLASSPATH. */
public final class RootExec {
    public static String current = "", legacy = "", defined = "";
    public static boolean currentWorks = true, legacyWorks = true, binaryExists = true;
    public static final List<String> calls = new ArrayList<>();
    public static final class Result {
        public final String stdout, stderr, error;
        public final int exit;
        public final boolean ok;
        public Result(String out, String err, int status, String failure) {
            stdout = out; stderr = err; exit = status; error = failure;
            ok = status == 0 && failure == null;
        }
    }
    public static Result exec(String command, long timeout) {
        calls.add(command);
        if (command.startsWith("command -v"))
            return new Result(binaryExists ? "/fixture/droidspaces" : "", "", binaryExists ? 0 : 1, null);
        if (command.endsWith("show --json"))
            return new Result(current, "", currentWorks ? 0 : 1, null);
        if (command.endsWith("show --format"))
            return new Result(legacy, "", legacyWorks ? 0 : 1, null);
        if (command.startsWith("ls "))
            return new Result(defined, "", 0, null);
        throw new AssertionError("Unexpected command " + command);
    }
}

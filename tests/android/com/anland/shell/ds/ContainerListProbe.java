package com.anland.shell.ds;

import java.lang.reflect.Field;
import java.util.List;

public final class ContainerListProbe {
    private static int checks;
    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("check " + (checks + 1));
        checks++;
    }
    private static void reset() throws Exception {
        Field binary = DsCli.class.getDeclaredField("dsBin");
        binary.setAccessible(true);
        binary.set(null, null);
        RootExec.current = RootExec.legacy = RootExec.defined = "";
        RootExec.currentWorks = RootExec.legacyWorks = RootExec.binaryExists = true;
        RootExec.calls.clear();
    }
    public static void main(String[] args) throws Exception {
        reset();
        RootExec.current = "{\"running\":[{\"name\":\"Debian13\",\"pid\":7550}]}";
        RootExec.defined = "Ubuntu\nDebian13\n";
        List<ContainerState> values = DsCli.listContainers();
        check(values.size() == 2); check(values.get(0).name.equals("Debian13"));
        check(values.get(0).pid == 7550); check(values.get(1).name.equals("Ubuntu"));
        check(RootExec.calls.stream().noneMatch(s -> s.endsWith("show --format")));
        reset();
        RootExec.currentWorks = false;
        RootExec.legacy = "{\"running\":[{\"name\":\"Old\",\"pid\":42}]}";
        values = DsCli.listContainers();
        check(values.size() == 1 && values.get(0).pid == 42);
        check(RootExec.calls.stream().anyMatch(s -> s.endsWith("show --format")));
        reset();
        RootExec.currentWorks = false;
        RootExec.legacy = "CONT_Z=99 OTHER=1\nCONT_A=2\nCONT_B=oops\nCONT_C=0\n";
        values = DsCli.listContainers();
        check(values.size() == 2); check(values.get(0).name.equals("A"));
        check(values.get(1).name.equals("Z"));
        reset();
        RootExec.current = "{\"running\":[]}";
        RootExec.defined = "Stopped\n";
        values = DsCli.listContainers();
        check(values.size() == 1 && values.get(0).name.equals("Stopped"));
        check(RootExec.calls.stream().noneMatch(s -> s.endsWith("show --format")));
        reset();
        RootExec.current = "{broken";
        check(DsCli.listContainers().isEmpty());
        reset();
        RootExec.current = "{\"running\":[null,3,{\"name\":\"bad/path\",\"pid\":1},{\"name\":\"\",\"pid\":2},{\"name\":\"neg\",\"pid\":-1},{\"name\":\"Valid\",\"pid\":7}]}";
        values = DsCli.listContainers();
        check(values.size() == 1 && values.get(0).name.equals("Valid"));
        reset();
        RootExec.currentWorks = RootExec.legacyWorks = false;
        RootExec.defined = "StillDefined\n";
        check(DsCli.listContainers().size() == 1);
        reset();
        RootExec.binaryExists = false;
        check(DsCli.listContainers().isEmpty()); check(RootExec.calls.size() == 1);
        reset();
        RootExec.current = "{}";
        check(DsCli.listContainers().isEmpty());
        System.out.println("PASS " + checks + " real container-list/fallback checks");
    }
}

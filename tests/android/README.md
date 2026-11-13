# Container-list compatibility probe

Build the release APK. Compile the two probe Java files against the Android
SDK and compiled application classes, then convert them with d8. Put the probe
jar before the APK on the isolated Android process classpath

```sh
CLASSPATH=probe.jar:shell.apk app_process /system/bin com.anland.shell.ds.ContainerListProbe
```

Use absolute device paths. The test-only RootExec records calls and returns
fixture command output. The actual DsCli.listContainers method and Android
JSON implementation execute. Assertions cover current/legacy flags, fallback,
malformed JSON, invalid entries, stopped/running merge and binary discovery
failure. The stub never invokes root commands or a real container operation.

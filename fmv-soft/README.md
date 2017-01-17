# FMV-Soft

Easy way to fond and execute an external binary. 


## Getting started

Example with FFMpeg, display the version text block:

```java
Soft soft = Soft.withExecFile("/path/to/ffmpeg");
soft.withParameters("-version")
  .addCommonReadLine(System.out::println)
  .execute();
```

Really, for FFmpeg, use the [defined ffmpeg soft](/..fmv-soft-auto/src/main/java/org/fagu/fmv/soft/ffmpeg/FFMpeg.java) as
```java
Soft soft = FFMpeg.search();
```


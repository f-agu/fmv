# FMV-FFMpeg

FMV-FFMpeg helps the developer to use `ffmpeg`.

# Example

Extract a part of a video from 2s to 5.4s (see [this class](/fmv-ffmpeg/src/main/java/org/fagu/fmv/ffmpeg/FFHelper.java#L417)):
```java
File inFile = ...;
File outFile = ...;
Time startTime = Time.valueOf(2);
Duration duration = Duration.valueOf(3.4);


FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

// input
InputProcessor inputProcessor = builder.addMediaInputFile(inFile);
inputProcessor.timeSeek(startTime);

// output
OutputProcessor outputProcessor = builder.mux(MP4Muxer.to(outFile).avoidNegativeTs(AvoidNegativeTs.MAKE_NON_NEGATIVE));
outputProcessor.duration(duration);
outputProcessor.qualityScale(0);
outputProcessor.codec(Libx264.build().mostCompatible());
outputProcessor.overwrite();

// execute
builder.build().execute();
```

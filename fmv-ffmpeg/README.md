# FMV-FFMpeg

FMV-FFMpeg helps the developer to use `ffmpeg`.

# Examples

For all example, see [FFHelper](/fmv-ffmpeg/src/main/java/org/fagu/fmv/ffmpeg/FFHelper.java)

### Extract a part of a video from 2s to 5.4s (see [this class](/fmv-ffmpeg/src/main/java/org/fagu/fmv/ffmpeg/FFHelper.java#L414)):
```java
File inFile = ...;
File outFile = ...;

FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

// input
builder.addMediaInputFile(inFile)
		.timeSeek(Time.valueOf(2)); // from 2s

// ouput
builder.mux(MP4Muxer.to(outFile).avoidNegativeTs(AvoidNegativeTs.MAKE_NON_NEGATIVE))
		.duration(Duration.valueOf(3.4)) //  to 2s + 3.4s = 5.4s
		.qualityScale(0)
		.codec(Libx264.build().mostCompatible())
		.overwrite();

// execute
builder.build().execute();
```

### Resize to 852x480 (HD480) (see [this class](/fmv-ffmpeg/src/main/java/org/fagu/fmv/ffmpeg/FFHelper.java#L438)):

```java
FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

// input
InputProcessor inputProcessor = builder.addMediaInputFile(inFile);

// resize with filter
builder.filter(Scale.to(Size.HD480, ScaleMode.fitToBoxKeepAspectRatio()));

// output
builder.mux(MP4Muxer.to(outFile))
		.qualityScaleVideo(0)
		.codecCopy(Type.AUDIO)
		.codecCopy(Type.SUBTITLE)
		.mapAllStreams(inputProcessor)
		.overwrite();

// execute
builder.build().execute();
```


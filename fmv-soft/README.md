# FMV-Soft

Easy way to fInd and execute an external binary. 


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


## On your application startup

On starting, add a check to find all binaries your application needs. An example:
```java
private boolean checkSome() {
	List<Soft> softs = new ArrayList<>(8);
	softs.add(FFMpeg.search());
	softs.add(FFProbe.search());
	softs.add(Identify.search());
	softs.add(Convert.search());
	softs.add(Composite.search());
	softs.add(PdfInfo.search());
	softs.add(PdfToText.search());
	softs.add(GS.search());
	SoftLogger softLogger = new SoftLogger(softs);
	return softLogger.log(System.out::println);
}
```
display
```
composite   [   FOUND   ]  C:\Program Files\ImageMagick-7.0.3-Q16\magick.exe (7.0.3.5) 
convert     [   FOUND   ]  C:\Program Files\ImageMagick-7.0.3-Q16\magick.exe (7.0.3.5) 
ffmpeg      [   FOUND   ]  C:\Program Files\ImageMagick-7.0.3-Q16\ffmpeg.exe (2.8.4) 
ffprobe     [   FOUND   ]  C:\Program Files\FFMpeg\bin\ffprobe.exe (81755) 
gs          [ NOT_FOUND ] 
   Download at: http://ghostscript.com/download/ 
   Minimum version: All platforms[>= v9.15] 
identify    [   FOUND   ]  C:\Program Files\ImageMagick-7.0.3-Q16\magick.exe (7.0.3.5) 
pdfinfo     [   FOUND   ]  C:\Program Files (x86)\xpdfbin-win-3.04\bin64\pdfinfo.exe (3.4) 
pdftotext   [   FOUND   ]  C:\Program Files (x86)\xpdfbin-win-3.04\bin64\pdftotext.exe (3.4) 
===================================================================== 
A software is missing 
Don't forget the PATH env 
PATH: ......
===================================================================== 
```



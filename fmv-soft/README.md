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


## Add another binary in 3 steps

#### 1. Implement a SoftProvider

Example for `another-binary` with minimal version 4.7.

```java
class AnotherBinarySoftProvider extends SoftProvider {

	public static final String NAME = "another-binary"; // the file name on Linux, the base file name on Windows

	public AnotherBinarySoftProvider() {
		super(NAME);
	}
	
	// Used to extract the version of the binary
	// Command line executed: another-binary --version
	public SoftFoundFactory createSoftFoundFactory() {
		return ExecSoftFoundFactory.withParameters("--version")
				.parseFactory(file -> createParser(getSoftName(), file))
				.build();
	}
	
	// minimal version
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy().onAllPlatforms().minVersion(new Version(7, 7));
	}
	
	Parser createParser(SoftName softName, File file) {
		return new Parser() {

			private Version version;
			
			public void readLine(String line) {
				// parse each line of the output
				version = ...;
			}

			public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
				SoftPolicy<?, ?, ?> softPolicy = getSoftPolicy();
				return softPolicy.toSoftFound(new VersionSoftInfo(file, softName, version));
			}

		};
	}
}
```

#### 2. Declare your new SoftProvider

Create the file META-INF/services/org.fagu.fmv.soft.find.SoftProvider and add into the class name.
Example:
```
org.test.AnotherBinarySoftProvider
```

#### 3. Shortcut your soft

Create a simple class like:
```java
public class AnotherBinary {

	public static Soft search() {
		return Soft.search(AnotherBinarySoftProvider.NAME);
	}

}
```


For more examples, take a look a the module fmv-soft-auto.
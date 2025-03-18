package org.fagu.fmv.mymedia.classify.duplicate;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.utils.ByteSize;
import org.fagu.fmv.utils.file.FileFinder.FileFound;
import org.fagu.fmv.utils.file.FileFinder.InfosFile;
import org.fagu.fmv.utils.file.MD5Sum;

import dev.brachtendorf.jimagehash.hash.Hash;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 mars 2025 14:32:43
 */
@SuppressWarnings("rawtypes")
public interface DuplicatedFiles<K> {

	public record FileInfosFile(File file, InfosFile infosFile) {}

	void populate(FileFound fileFound, InfosFile infosFile);

	boolean analyze();

	Map<K, List<FileInfosFile>> getMap();

	Map<K, List<FileInfosFile>> getDuplicateds();

	static DuplicatedFiles<Long> bySize() {
		NavigableMap<Long, List<FileInfosFile>> bySizes = new TreeMap<>();
		return new AbstractDuplicatedFiles<Long>(
				"size",
				(size, infosFiles) -> ByteSize.formatSize(size) + " (" + size + "): " + infosFiles.size() + " files",
				(size, infosFiles) -> infosFiles.stream().map(inff -> inff.file().getName()).collect(Collectors.joining(", "))) {

			@Override
			public void populate(FileFound fileFound, InfosFile infosFile) {
				bySizes.computeIfAbsent(
						fileFound.getFileFound().length(),
						k -> new ArrayList<>())
						.add(new FileInfosFile(fileFound.getFileFound(), infosFile));
			}

			@Override
			public Map<Long, List<FileInfosFile>> getMap() {
				return bySizes;
			}
		};
	}

	static DuplicatedFiles<String> byMD5Sum() {
		Map<String, List<FileInfosFile>> byMD5s = new HashMap<>();
		return new AbstractDuplicatedFiles<String>(
				"content (MD5)",
				(md5, infosFiles) -> md5 + ": " + infosFiles.size() + " files",
				(md5, infosFiles) -> infosFiles.stream().map(inff -> inff.file().getName()).collect(Collectors.joining(", "))) {

			@Override
			public void populate(FileFound fileFound, InfosFile infosFile) {
				@SuppressWarnings("unchecked")
				Optional<MD5Sum> opt = infosFile.getInfo(MD5Sum.class);
				opt.ifPresent(md5 -> byMD5s.computeIfAbsent(md5.value(), k -> new ArrayList<>())
						.add(new FileInfosFile(fileFound.getFileFound(), infosFile)));
			}

			@Override
			public Map<String, List<FileInfosFile>> getMap() {
				return byMD5s;
			}
		};
	}

	static DuplicatedFiles<BigInteger> byPerceptionHash(double threshold, boolean normalized, Logger logger) {
		logger.log("Use " + (normalized ? "normalizedHammingDistanceFast" : "hammingDistanceFast"));
		BiFunction<Hash, Hash, Double> distance = normalized ? (h1, h2) -> h1.normalizedHammingDistanceFast(h2)
				: (h1, h2) -> (double)h1.hammingDistanceFast(h2);

		Map<BigInteger, List<FileInfosFile>> byHash = new HashMap<>();
		return new AbstractDuplicatedFiles<BigInteger>(
				"content (PerceptionHash)",
				(md5, infosFiles) -> md5 + ": " + infosFiles.size() + " files",
				(md5, infosFiles) -> infosFiles.stream().map(inff -> inff.file().getName()).collect(Collectors.joining(", "))) {

			@Override
			public Map<BigInteger, List<FileInfosFile>> getDuplicateds() {
				Map<BigInteger, List<FileInfosFile>> retMap = new HashMap<>();
				AtomicInteger offset = new AtomicInteger();
				byHash.values().stream()
						.flatMap(List::stream)
						.forEach(fif -> {
							@SuppressWarnings("unchecked")
							Hash hash1 = (Hash)fif.infosFile().getInfo(Hash.class).orElseThrow();
							byHash.values().stream()
									.flatMap(List::stream)
									.skip(offset.incrementAndGet())
									.filter(fif2 -> {
										@SuppressWarnings("unchecked")
										Hash hash2 = (Hash)fif2.infosFile().getInfo(Hash.class).orElseThrow();
										return isEquivalent(distance, fif, hash1, fif2, hash2);
									})
									.forEach(fif3 -> {
										retMap.computeIfAbsent(hash1.getHashValue(), k -> new ArrayList<>(List.of(fif)))
												.add(fif3);
									});
						});
				return retMap;
			}

			@Override
			public void populate(FileFound fileFound, InfosFile infosFile) {
				@SuppressWarnings("unchecked")
				Optional<Hash> opt = infosFile.getInfo(Hash.class);
				opt.ifPresent(md5 -> byHash.computeIfAbsent(md5.getHashValue(), k -> new ArrayList<>())
						.add(new FileInfosFile(fileFound.getFileFound(), infosFile)));
			}

			@Override
			public Map<BigInteger, List<FileInfosFile>> getMap() {
				return byHash;
			}

			// *****************************************************

			private boolean isEquivalent(BiFunction<Hash, Hash, Double> distance, FileInfosFile fif1, Hash h1, FileInfosFile fif2,
					Hash h2) {
				double dist = distance.apply(h1, h2);
				logger.log("Distance(" + fif1.file().getName() + " =? " + fif2.file().getName() + "): " + dist + " => " + (dist < threshold
						? "equivalent"
						: "different"));
				return dist <= threshold;
			}
		};
	}

}

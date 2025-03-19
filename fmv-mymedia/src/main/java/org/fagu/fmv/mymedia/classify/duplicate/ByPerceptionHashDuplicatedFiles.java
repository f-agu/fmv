package org.fagu.fmv.mymedia.classify.duplicate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.part.PercentPart;
import org.fagu.fmv.textprogressbar.part.SupplierTextPart;
import org.fagu.fmv.textprogressbar.part.TextPart;
import org.fagu.fmv.utils.file.FileFinder.FileFound;
import org.fagu.fmv.utils.file.FileFinder.InfosFile;

import dev.brachtendorf.jimagehash.hash.Hash;


/**
 * @author f.agu
 */
public class ByPerceptionHashDuplicatedFiles extends AbstractDuplicatedFiles<BigInteger> {

	private final Map<BigInteger, List<FileInfosFile>> byHash = new HashMap<>();

	private final double threshold;

	private final BiFunction<Hash, Hash, Double> distance;

	public ByPerceptionHashDuplicatedFiles(Logger logger, double threshold, boolean normalized) {
		super(logger,
				"content (PerceptionHash)",
				(md5, infosFiles) -> md5 + ": " + infosFiles.size() + " files",
				(md5, infosFiles) -> infosFiles.stream().map(inff -> inff.fileFound().getFileFound().getName()).collect(Collectors.joining(", ")));
		this.threshold = threshold;
		distance = normalized ? (h1, h2) -> h1.normalizedHammingDistanceFast(h2)
				: (h1, h2) -> (double)h1.hammingDistanceFast(h2);
	}

	@Override
	public void populate(FileFound fileFound, @SuppressWarnings("rawtypes") InfosFile infosFile) {
		@SuppressWarnings("unchecked")
		Optional<Hash> opt = infosFile.getInfo(Hash.class);
		opt.ifPresent(md5 -> byHash.computeIfAbsent(md5.getHashValue(), k -> new ArrayList<>())
				.add(new FileInfosFile(fileFound, infosFile)));
	}

	@Override
	public Map<BigInteger, List<FileInfosFile>> getMap() {
		return byHash;
	}

	// *****************************************************

	@Override
	protected Map<BigInteger, List<FileInfosFile>> loadCache() {
		int total = (int)byHash.values().stream()
				.flatMap(List::stream)
				.count();
		int countIteration = total * (total - 1) / 2;
		AtomicInteger index = new AtomicInteger();

		SupplierTextPart supplierTextPart = new SupplierTextPart();
		try (TextProgressBar textProgressBar = TextProgressBar.newBar()
				.append(new TextPart("Analyzing duplicated images with perception hash: "))
				.append(supplierTextPart)
				.append(new TextPart("   "))
				.append(new PercentPart())
				.buildAndSchedule(() -> 100 * index.get() / countIteration)) {

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
								.peek(fif0 -> supplierTextPart.setText(Integer.toString(index.incrementAndGet()) + "/" + countIteration))
								.filter(fif2 -> {
									@SuppressWarnings("unchecked")
									Hash hash2 = (Hash)fif2.infosFile().getInfo(Hash.class).orElseThrow();
									return isEquivalent(distance, fif, hash1, fif2, hash2);
								})
								.forEach(fif3 -> retMap.computeIfAbsent(hash1.getHashValue(), k -> new ArrayList<>(List.of(fif)))
										.add(fif3));
					});
			return retMap;
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			System.out.println();
		}
	}

	// *****************************************************

	private boolean isEquivalent(BiFunction<Hash, Hash, Double> distance, FileInfosFile fif1, Hash h1, FileInfosFile fif2,
			Hash h2) {
		double dist = distance.apply(h1, h2);
		if(dist <= threshold) {
			logger.log("Distance(" + fif1.fileFound().getFileFound().getName() + " =? " + fif2.fileFound().getFileFound().getName() + "): " + dist
					+ " => " + (dist < threshold
							? "equivalent"
							: "different"));
			return true;
		}
		return false;
	}

}

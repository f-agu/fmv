package org.fagu.fmv.mymedia.sources;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.fagu.fmv.mymedia.utils.ScannerHelper;
import org.fagu.fmv.mymedia.utils.ScannerHelper.YesNo;


/**
 * @author Oodrive
 * @author f.agu
 * @created 22 déc. 2023 15:52:56
 */
public class GitPullBootstrap {

	public static void main(String... args) throws IOException {
		if(args.length != 1) {
			System.out.println("Usage: java " + GitPullBootstrap.class.getName() + " <file-list-folders|path-to-backup>");
			return;
		}
		List<Path> folders = FileListFolders.loadFolders(args[0]);
		AtomicInteger count = new AtomicInteger();
		List<Path> list = new ArrayList<>();
		Map<Path, String> map = new TreeMap<>();
		for(Path folder : folders) {
			ProjectWalker.searchProject(folder, path -> {
				Path gitPath = path.resolve(".git");
				if(Files.exists(gitPath)) {
					count.incrementAndGet();
					list.add(path);
					getProjectNotBranchMaster(gitPath).ifPresent(v -> map.put(folder, v));
					return FileVisitResult.SKIP_SUBTREE;
				}
				return FileVisitResult.CONTINUE;
			});
		}

		System.out.println();
		System.out.println(count.get() + " git projects");

		if( ! map.isEmpty()) {
			System.out.println("Some projects not in master/main :");
			map.forEach((p, v) -> System.out.println(p + " : " + v));
			if(YesNo.NO.equals(ScannerHelper.yesNo("Continue ", YesNo.YES))) {
				return;
			}
		}

		for(Path path : list) {
			Path gitPath = path.resolve(".git");
			System.out.print(gitPath + " ...");
			List<String> conflictingPaths = pull(gitPath);
			if( ! conflictingPaths.isEmpty() && isOnlyDeletableFiles(path, conflictingPaths)) {
				pull(gitPath);
			}
			System.out.println();
		}
	}

	private static Optional<String> getProjectNotBranchMaster(Path gitPath) throws IOException {
		try (Repository repository = new FileRepositoryBuilder()
				.setGitDir(gitPath.toFile())
				.build()) {
			String branchName = repository.getBranch();
			if( ! "main".equalsIgnoreCase(branchName) && ! "master".equalsIgnoreCase(branchName)) {
				return Optional.of(branchName);
			}
		}
		return Optional.empty();
	}

	private static List<String> pull(Path gitPath) throws IOException {
		try (Repository repository = new FileRepositoryBuilder()
				.setGitDir(gitPath.toFile())
				.build();
				Git git = new Git(repository)) {
			PullCommand pullCmd = git.pull();
			pullCmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider("token", "5SKsgciPKtcRcM1ZKAfE"));
			try {
				pullCmd.call();
			} catch(org.eclipse.jgit.api.errors.CheckoutConflictException e) {
				return e.getConflictingPaths();
				// .stream().forEach(s -> System.out.println(" " + s))
			} catch(GitAPIException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	private static boolean isOnlyDeletableFiles(Path path, List<String> conflictingPaths) {
		System.out.print(".");
		List<Predicate<Path>> filters = new ArrayList<>();
		filters.add(p -> "APPENDIX.md".equals(p.getFileName().toString()));
		filters.add(p -> "TID.md".equals(p.getFileName().toString()));
		filters.add(p -> "TID.md.generated".equals(p.getFileName().toString()));
		filters.add(p -> "application.properties.full".equals(p.getFileName().toString()));
		filters.add(p -> "application.properties.j2.generated".equals(p.getFileName().toString()));

		List<Path> unresolveds = conflictingPaths.stream()
				.map(s -> path.resolve(s))
				.map(p -> {
					if(filters.stream().anyMatch(f -> f.test(p))) {
						try {
							Files.delete(p);
							return null;
						} catch(IOException e) {
							System.out.println();
							e.printStackTrace();
						}
					}
					return p;
				})
				.filter(Objects::nonNull)
				.toList();
		if(unresolveds.isEmpty()) {
			return true;
		}
		System.out.println();
		unresolveds.forEach(p -> System.out.println("    >>> " + p));
		return false;
	}

}

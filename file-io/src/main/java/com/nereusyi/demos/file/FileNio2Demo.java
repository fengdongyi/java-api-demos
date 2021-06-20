package com.nereusyi.demos.file;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nereus Yi
 */
public class FileNio2Demo {

    public static void main(String[] args) throws IOException, InterruptedException {
//        dirEvent();
        basic();
    }

    public static void basic() throws IOException {
        List<FileSystemProvider> providers = FileSystemProvider.installedProviders();
        System.out.println(providers);
        FileSystem defaultFileSystem = FileSystems.getDefault();
        System.out.println(defaultFileSystem);
        Iterable<Path> rootDirectories = defaultFileSystem.getRootDirectories();
        System.out.println(rootDirectories.iterator().next());
        Iterable<FileStore> fileStores = defaultFileSystem.getFileStores();
        FileStore fileStore = fileStores.iterator().next();
        System.out.println(fileStore.name());
        System.out.println(fileStore.type());
//        defaultFileSystem.provider().createDirectory(Path.of("/tmp/testDir"));
    }

    public static void jarFileSystem() throws IOException {
        URI jarUri = URI.create("jar:file:///tmp/test.jar");

        Map<String, String> options = new HashMap<>();
        // Create
        options.put("create", "true");
        try( FileSystem jarFs = FileSystems.newFileSystem(jarUri, options);){
            // Write file into jar
            Path dir = jarFs.getPath("files");
            jarFs.provider().createDirectory(dir);

            Path textTxt = jarFs.getPath("files/text.txt");
            Files.writeString(textTxt, "HelloWorld Jar File", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            // Read file in jar
            Path path = jarFs.getPath("files/text.txt");
            String result = Files.readString(path);
            System.out.println(result);
        }

    }

    public static void visitDirTree() throws IOException {
        Path tmpDir = Paths.get("/tmp");
        DirectoryStream<Path> paths = Files.newDirectoryStream(tmpDir, Files::isDirectory);
    }

    public static void watchEvent() throws IOException, InterruptedException {
        Path watchPath = Path.of("/tmp");
        try (FileSystem fileSystem = FileSystems.getDefault();
             WatchService watchService = fileSystem.newWatchService();) {

            // only watch current dir , not include sub-dir
            WatchKey key = watchPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.OVERFLOW
            );

            while (key.isValid()) {
                WatchKey watchKey = watchService.take();
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                for (WatchEvent<?> event : watchEvents) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path context = (Path) event.context();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        System.out.println("event too much");
                    } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("file created , path=" + context + ",type=" + Files.probeContentType(context));
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("file modified , path=" + context + ",type=" + Files.probeContentType(context));
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("file deleted , path=" + context + ",type=" + Files.probeContentType(context));
                    }
                    // reset or never get event
                    watchKey.reset();
                }
            }
            System.out.println("watch key is invalid");

        }
    }

}

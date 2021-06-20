package com.nereusyi.demos.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * demo shows file nio api
 * @author Nereus
 */
public class FileNioDemo {

    private static final String DEMO_WALK_PATH = "G:/tmp";
    private static final String DEMO_PATH = "G:/demoFile.txt";
    private static final String DEMO_COPY_TAR_PATH = "G:/demoFile2.txt";
    private static final String DEMO_MOVE_PATH = "G:/demoFile3.txt";

    public static void main(String[] args) throws IOException {
    }

    public void createPathInstance() throws URISyntaxException {
        URI uri = new URI("file:///" + DEMO_PATH);
        // method 1
        Path path1 = Paths.get(DEMO_PATH);

        // method 2
        Path path2 = Paths.get(uri);

        // method 3
        Path path3 = Path.of(DEMO_PATH);

        // method 4
        Path path4 = Path.of(uri);
    }

    public void getInfoFromPath() throws IOException {
        Path path = Path.of(DEMO_PATH);

        boolean exists = Files.exists(path);
        System.out.println("exists = "+exists);

        boolean exists2 = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        System.out.println("exists2 = " + exists2);

        boolean hidden = Files.isHidden(path);
        System.out.println("hidden = " + hidden);

        boolean directory = Files.isDirectory(path);
        System.out.println("directory = " + directory);

        boolean regularFile = Files.isRegularFile(path);
        System.out.println("regularFile = " + regularFile);

        boolean readable = Files.isReadable(path);
        System.out.println("readable = " + readable);

        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
        System.out.println("creationTime = " + attrs.creationTime());
        System.out.println("size = " + attrs.size());

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // windows
            DosFileAttributes dosAttrs = Files.readAttributes(path, DosFileAttributes.class);
            System.out.println("isSystem = " + dosAttrs.isSystem());
            System.out.println("isReadOnly = " + dosAttrs.isReadOnly());
        }else{
            // linux
            PosixFileAttributes posixAttrs = Files.readAttributes(path, PosixFileAttributes.class);
            System.out.println("permissions = " + posixAttrs.permissions());
        }
    }

    public void writeFile() throws IOException {
        Path path = Path.of(DEMO_PATH);

        OpenOption openOption = StandardOpenOption.APPEND;

        // method 1
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, openOption);){
            writer.write("HelloWorld");
            writer.newLine();
            writer.write("HelloWorld2");
        }

        // method 2
        Files.writeString(path, "HelloWorld3", StandardCharsets.UTF_8,openOption);

        // method 3
        Files.write(path, List.of("HelloWorld4"), StandardCharsets.UTF_8,openOption);
    }

    public void readFile() throws IOException {
        Path path = Path.of(DEMO_PATH);

        // method 1
        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
            String line = reader.readLine();
            System.out.println(line);
        }

        // method 2
        try( Stream<String> lines = Files.lines(path) ){
            lines.forEach(System.out::println);
        }

        // method 3
        String string = Files.readString(path);
        System.out.println(string);
    }

    public void createFile() throws IOException {
        Path path = Files.createFile(Path.of(DEMO_PATH));
        System.out.println(Files.exists(path));
    }

    public void moveFile() throws IOException {
        Path path = Files.move(Path.of(DEMO_PATH), Path.of(DEMO_MOVE_PATH), StandardCopyOption.REPLACE_EXISTING);
        boolean sameFile = Files.isSameFile(path, Path.of(DEMO_MOVE_PATH));
        System.out.println(sameFile);
    }

    public void copyFile() throws IOException {
        Path path = Files.copy(Path.of(DEMO_PATH), Path.of(DEMO_COPY_TAR_PATH), StandardCopyOption.REPLACE_EXISTING);
        boolean sameFile = Files.isSameFile(path, Path.of(DEMO_COPY_TAR_PATH));
        System.out.println(sameFile);
    }

    public void findFile() throws IOException {
        Path dir = Path.of("G:/");
        Path file = Path.of("demoFile2.txt");

        Files.find(dir, 1, (p, attr) ->
                        Files.isRegularFile(p) && p.getFileName().endsWith(file)
                , FileVisitOption.FOLLOW_LINKS).findAny()
                .ifPresent(System.out::println);
    }

    public void walkFiles() throws IOException {
        Path walkPath = Path.of(DEMO_WALK_PATH);

        // method 1
        AtomicLong fileCount = new AtomicLong();
        FileVisitor<Path> fileVisitor = new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileCount.getAndIncrement();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(walkPath, fileVisitor);
        System.out.println(fileCount.get());

        // method 2
        long count2 = Files.walk(walkPath, FileVisitOption.FOLLOW_LINKS)
                .filter(Files::isRegularFile).count();
        System.out.println(count2);

        //
//        Files.find()
    }

    public void fileAndPath(){
        Path path = new File(DEMO_PATH).toPath();
        File file = path.toFile();
    }

    public static void createAndWriteFile(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("HelloWorld".getBytes(StandardCharsets.UTF_8));
        try ( FileChannel fileChannel = FileChannel.open(Path.of("/tmp/test.txt"),
                StandardOpenOption.CREATE,StandardOpenOption.WRITE) ){
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("write success.");
    }

    public static void readFile2(){
        Charset utf8 = StandardCharsets.UTF_8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try ( FileChannel fileChannel = FileChannel.open(Path.of("/tmp/test.txt"),
                StandardOpenOption.READ) ){
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CharBuffer charBuffer = utf8.decode(byteBuffer);
        System.out.print("read result:");
        System.out.println(new String(charBuffer.array()));
    }

}

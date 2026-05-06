import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.*;


class Parser{
    // Attributes
    String commandName;
    String[] args = new String[0];
    //-------------------------------------------
    // Methods
    public boolean parse(String input) {
        input = input.trim();
        String[] parts = input.split("\\s+");
        if (parts.length > 0) {
            commandName = parts[0];
            // Taking arguments into args array if there is arguments
            if (parts.length > 1) {
                args = new String[parts.length - 1];
                // Start from 1 because 0 have commandName
                for (int i = 1; i < parts.length; i++) {
                    args[i - 1] = parts[i];
                }
            }
            else {
                // handle commands without args by making empty array
                args = new String[0];
            }
        }
        return true;
    }

    // Getter for the command name
    public String getCommandName() {
        return commandName;
    }

    // Getter for the args array
    public String[] getArgs() {
        return args;
    }
}

public class Terminal {
    // Attributes
    static Parser parser = new Parser();
    String path = Paths.get("").toAbsolutePath().toString();
    //-------------------------------------------

    // Methods

    // Prints current directory
    public String pwd(){
        System.out.println("Current Directory (System Property): " + path);
        return path;
    }

    // Traverse between directory
    public void cd(String[] args){
        if (args.length == 0) {
            String homeDir = System.getProperty("user.home");
            path = homeDir;
            System.out.println("Current Directory (Home): " + path);
        }else if(args.length == 1 && args[0].equals("..")){
            StringBuilder pathstring = new StringBuilder(path);
            boolean flag = true;
            // Loop backwards from the end of the string
            for (int i = path.length() - 1; i >= 0; i--) {
                char currentChar = pathstring.charAt(i);
                if (currentChar != '\\' && flag == true ) {
                    pathstring.deleteCharAt(i);
                } else {
                    flag = false;
                    if (pathstring.length() > 3){
                        pathstring.deleteCharAt(i);
                    }
                    path = pathstring.toString();
                    System.out.println("Current Directory: " + path);
                    break;
                }
            }
        }else {
            String targetPathString = args[0];
                Path resolvedPath = Paths.get(path).resolve(targetPathString).normalize();
                if (Files.exists(resolvedPath)) {
                    if (Files.isDirectory(resolvedPath)) {
                        path = resolvedPath.toAbsolutePath().toString();
                        System.out.println("Changed directory to: " + path);
                    } else {
                        System.err.println("Error: " + targetPathString + " exists but is not a directory.");
                    }
                }else{
                    System.err.println("The system cannot find the path specified.");
                    System.out.println(path);
                }
        }
    }

    // Prints all the contents in current directory
    public void ls(){
        List<Path> entries = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path entry : stream) {
                entries.add(entry);
            }
            entries.sort((p1, p2) -> p1.compareTo(p2));
            for (Path entry : entries) {
                String type = Files.isDirectory(entry) ? "[DIR]" : "[FILE]";
                System.out.printf("%-5s %s%n", type, entry.getFileName());
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the directory: " + e.getMessage());
        }
    }

    // Make directory
    public void mkdir(String[] args){
        for (String dir : args) {
            if (dir == null || dir.trim().isEmpty()) {
                continue;
            }
            
            try {
                // Resolve and create new directory in the current path
                Path dirPath = Paths.get(path).resolve(dir).normalize();
                
                // Check if directory already exists
                if (Files.exists(dirPath)) {
                    if (Files.isDirectory(dirPath)) {
                        System.out.println("Note Directory already exists: " + dirPath.getFileName());
                    } 
                    else {
                        System.err.println("Error: A file with this name exists: " + dirPath.getFileName());
                    }
                    continue;
                }
                
                Files.createDirectories(dirPath);
                System.out.println("Directory created: " + dirPath.toAbsolutePath());
                
            } catch (Exception e) {
                System.err.println("Error: Failed to create '" + dir + "': " + e.getMessage());
            }
        }
    }

    // Delete directory
    public void rmdir(String[] args){
        // Case 1: Handle '*' argument
        String dir = args[0];
        if(args[0].equals("*")){
            // Getting the current path iam on
            Path dirPath = Paths.get(path);
            // Converting it to file to enable using of .list()
            File dirPathFile = dirPath.toFile();
            // Return sub directorys and files in the current directory
            String[] subDirs = dirPathFile.list();
            // Loop over every file and directory
            for(String subdir : subDirs){
                // If exist
                if(subdir != null){
                    // Resolve or attach the subdir at the end of the current path
                    Path subDirPath = dirPath.resolve(subdir);
                    // Deletes only empty directorys (Not files)
                    if(Files.isDirectory(subDirPath)){
                        try {
                            Files.delete(subDirPath);
                            System.out.println("Deleted empty directory: " + subdir);
                        } catch (IOException e) {
                            System.err.println("Failed to delete " + subdir + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
        else{
            // Case 2: Handle short and absloute paths
            try {
                Path dirPath = Paths.get(path).resolve(dir).normalize();
                Files.delete(dirPath);
                System.out.println("Directory deleted successfully: " + dirPath.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error: This is not directory or is not empty: " + e.getMessage());
            }
        }
    }

    // Create file
    public void touch(String[] args){
        String fileArg = args[0];
        Path resolvedPath = Paths.get(path).resolve(fileArg).normalize();

        try {
            Files.createFile(resolvedPath);
            System.out.println("File created: " + resolvedPath.toAbsolutePath());
        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Copy two directories or two files
    public void cp(String[] args){
        if(!(args[0].equals("-r"))){
            String file1 = args[0];
            String file2 = args[1];
        
            Path resolvedF1 = Paths.get(path).resolve(file1).normalize();
            Path resolvedF2 = Paths.get(path).resolve(file2).normalize();

            if(Files.isRegularFile(resolvedF1) && Files.isRegularFile(resolvedF2)){
                
                try {
                    Files.copy(resolvedF1, resolvedF2, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File copied");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.err.println("Error: File is not exist or is not a file");
            }
        }
        else{
            String dir1 = args[1];
            String dir2 = args[2];
            
            Path resolvedD1 = Paths.get(path).resolve(dir1).normalize();
            Path resolvedD2 = Paths.get(path).resolve(dir2).normalize();

            if(Files.isDirectory(resolvedD1)){
                try {
                    // Create target directory if it doesn't exist
                    if (!Files.exists(resolvedD2)) {
                        Files.createDirectories(resolvedD2);
                    }
                    
                    // Copy directory contents recursively using Files.walk
                    Files.walk(resolvedD1).forEach(source -> {
                            try {
                                Path destination = resolvedD2.resolve(resolvedD1.relativize(source));
                                if (Files.isDirectory(source)) {
                                    if (!Files.exists(destination)) {
                                        Files.createDirectories(destination);
                                    }
                                } else {
                                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    
                    System.out.println("Directory copied recursively");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.err.println("Error: Source directory does not exist or is not a directory");
            } 
        }
    }

    // remove a file
    public void rm(String[] args){
        if(args.length==0){
            System.err.println("Error: Please specify at least one file to remove.");
            return;
        }
        for (String filename : args){
            Path filePath =  Paths.get(path).resolve(filename).normalize();
            try {
                if(Files.exists(filePath) && Files.isRegularFile(filePath)){
                    Files.delete(filePath);
                    System.out.println("File got deleted: "+filename);
                }else{
                    System.err.println("Error: file doesn't exist or not regular: "+filename);
                }
                
            } catch (IOException e) {
                System.err.println("Error deleting this file: "+filename+ " "+e.getMessage());
            }
        }
    }

    // Show the file's content
    public void cat(String args[]){
        if(args.length==0){
            System.err.println("Error: Please specify at least one file to read.");
            return;
        }
        for (String filename : args){
            Path filePath =  Paths.get(path).resolve(filename).normalize();
            if(!Files.exists(filePath)){
                System.err.println("File not found: "+filename);
                continue;
            }
            try {
                List<String> lines = Files.readAllLines(filePath);
                for(String line :lines){
                    System.out.println(line);
                }
                
            } catch (IOException e) {
                System.err.println("Error reading this file: "+filename+ " "+e.getMessage());

            }
        }
    }

    // counting chars, words and lines
    public void wc(String args[]){
        if (args.length == 0) {
            System.err.println("Error: Please provide a file name to count.");
            return;
        }
        for(String filename : args){
            Path filePath =  Paths.get(path).resolve(filename).normalize();
        
            if(!Files.exists(filePath)){
                System.err.println("File not found: "+filename);
                continue;
            }
            int chars = 0, words = 0, lines = 0;
            try {
                List<String> content = Files.readAllLines(filePath);
                for(String line :content){
                    lines++;
                    words += line.trim().isEmpty() ? 0 : line.trim().split("\\s+").length;
                    chars += line.length();
                }
                System.out.println("Lines: " + lines + " , " + 
                "Words: "+ words + " , "+
                "chars: " + chars + " in: " +filename);
                
            } catch (IOException e) {
                System.err.println("Error reading this file: "+filename+ " "+e.getMessage());
            }  
        }
    }
    



    public void zip(String[] args) {
        if(!(args[0].equals("-r"))){
            // First argument is the name of the zipfile we want to create
            String zipName = args[0];
            // Important for combining the current drectory with the zip filename  (curr_directory/filename.zip)
            Path resolvedPathZip = Paths.get(path).resolve(zipName).normalize();
            // Converting path to file to use it in file stream
            File zipToFile = resolvedPathZip.toFile();

            try (FileOutputStream fos = new FileOutputStream(zipToFile);
                ZipOutputStream zos = new ZipOutputStream(fos)) {
                
                for (int i = 1; i < args.length; i++) {
                    Path resolvedPath = Paths.get(path).resolve(args[i]).normalize();
                    File strToFile = resolvedPath.toFile();
                    
                    try (FileInputStream fis = new FileInputStream(strToFile)) {
                        ZipEntry zipEntry = new ZipEntry(strToFile.getName());
                        // zip entry let us know the related data to a certain file
                        zos.putNextEntry(zipEntry);

                        // To help us reading the contents in the files
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zos.write(bytes, 0, length);
                        }
                        // Closing each entry
                        zos.closeEntry();
                        
                        System.out.println("Added: " + strToFile.getName());
                        
                    } catch (IOException e) {
                        System.err.println("Error processing file " + args[i] + ": " + e.getMessage());
                    }
                }
                
                System.out.println("ZIP file created successfully: " + zipName);
                
            } catch (FileNotFoundException e) {
                System.err.println("Error: Cannot create ZIP file - " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error writing to ZIP file: " + e.getMessage());
            }
        }
        else {
            // Recursive directory zipping
            String zipName = args[1];
            String dirToZip = args[2];
            
            Path resolvedPathZip = Paths.get(path).resolve(zipName).normalize();
            Path resolvedDirPath = Paths.get(path).resolve(dirToZip).normalize();
            File zipToFile = resolvedPathZip.toFile();

            try (FileOutputStream fos = new FileOutputStream(zipToFile);
                ZipOutputStream zos = new ZipOutputStream(fos)) {
                
                if (Files.isDirectory(resolvedDirPath)) {
                    // Recursively add directory contents to zip
                    Files.walk(resolvedDirPath).forEach(source -> {
                        try {
                            // Skip the root directory itself, only add its contents
                            if (!source.equals(resolvedDirPath)) {
                                // Calculate relative path for proper directory structure in zip
                                String relativePath = resolvedDirPath.relativize(source).toString();
                                    
                                // For Windows paths, convert backslashes to forward slashes
                                relativePath = relativePath.replace("\\", "/");
                                    
                                if (Files.isDirectory(source)) {
                                    // For directories, add with trailing slash
                                    ZipEntry zipEntry = new ZipEntry(relativePath + "/");
                                    zos.putNextEntry(zipEntry);
                                    zos.closeEntry();
                                } else {
                                    // For files, add the file content
                                    ZipEntry zipEntry = new ZipEntry(relativePath);
                                    zos.putNextEntry(zipEntry);
                                        
                                    try (FileInputStream fis = new FileInputStream(source.toFile())) {
                                        byte[] bytes = new byte[1024];
                                        int length;
                                        while ((length = fis.read(bytes)) >= 0) {
                                            zos.write(bytes, 0, length);
                                        }
                                    }
                                    zos.closeEntry();
                                    System.out.println("Added: " + relativePath);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    System.out.println("Directory zipped recursively successfully: " + zipName);
                } else {
                    System.err.println("Error: Directory does not exist or is not a directory");
                }
                
            } catch (FileNotFoundException e) {
                System.err.println("Error: Cannot create ZIP file - " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error writing to ZIP file: " + e.getMessage());
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    System.err.println("Error processing files: " + e.getCause().getMessage());
                }
            }
        }
    }

    public void unzip(String[] args) {
        // First argument is the name of the zipfile we want to unzip
        String zipName = args[0];
        // Important for combining the current directory with the zip filename (curr_directory/filename.zip)
        Path resolvedPathZip = Paths.get(path).resolve(zipName).normalize();
        // Converting path to file to use it in file stream
        File zipToFile = resolvedPathZip.toFile();

        byte[] bytes = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipToFile))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                // Use current directory as base
                File newFile = new File(path, zipEntry.getName());
                
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                    System.out.println("Created directory: " + newFile.getPath());
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(bytes)) > 0) {
                            fos.write(bytes, 0, len);
                        }
                    }
                    System.out.println("Extracted: " + newFile.getName());
                }
                zipEntry = zis.getNextEntry();
            }
            System.out.println("Unzip completed successfully!");
        } catch (IOException e) {
            System.err.println("Error decompressing from ZIP file: " + e.getMessage());
        }
    }

    boolean validCommand = false;

    public void chooseCommandAction(Terminal terminal) {
        if (parser.getCommandName().equals("pwd")) {
            terminal.pwd();
            validCommand = true;
        }
        else if (parser.getCommandName().equals("cd")) {
            terminal.cd(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("ls")) {
            terminal.ls();
            validCommand = true;
        }
        else if (parser.getCommandName().equals("mkdir")) {
            terminal.mkdir(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("rmdir")) {
            terminal.rmdir(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("touch")) {
            terminal.touch(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("cp")) {
            terminal.cp(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("cp -r")) {
            validCommand = true;
        }
        else if (parser.getCommandName().equals("rm")) {
            terminal.rm(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("cat")) {
            terminal.cat(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("wc")) {
            terminal.wc(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("zip")) {
            terminal.zip(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("unzip")) {
            terminal.unzip(parser.getArgs());
            validCommand = true;
        }
        else if (parser.getCommandName().equals("exit")) {
            System.exit(0);
        }
    }



    public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    Terminal terminal = new Terminal();

    while (true) {
        System.out.print(terminal.path + " $ ");
        if (!scanner.hasNextLine()) break;
        String input = scanner.nextLine().trim();

        // Handle redirection
        String redirectFile = null;
        boolean append = false;

        // Detect >
        if (input.contains(" > ")) {
            String[] parts = input.split("\\s>\\s", 2);
            input = parts[0];
            redirectFile = parts[1].trim();
        } 
        // Detect >>
        else if (input.contains(" >> ")) {
            String[] parts = input.split("\\s>>\\s", 2);
            input = parts[0];
            redirectFile = parts[1].trim();
            append = true;
        }

        parser.parse(input);

        java.io.ByteArrayOutputStream outputBuffer = new java.io.ByteArrayOutputStream();
        java.io.PrintStream oldOut = System.out;
        java.io.PrintStream ps = new java.io.PrintStream(outputBuffer);
        System.setOut(ps);

        // Reset validCommand before each command
        terminal.validCommand = false;

        terminal.chooseCommandAction(terminal);

        System.out.flush();
        System.setOut(oldOut);

        String commandOutput = outputBuffer.toString();

        if (!terminal.validCommand) {
            System.err.println("Error: Unknown command '" + parser.getCommandName() + "'");
            continue;
        }

        // Handle redirection output
        if (redirectFile != null) {
            try {
                Path redirectPath = Paths.get(terminal.path).resolve(redirectFile).normalize();
                if (append) {
                    Files.writeString(redirectPath, commandOutput, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } else {
                    Files.writeString(redirectPath, commandOutput, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
                }
                System.out.println("Output written to: " + redirectPath);
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        } else {
            System.out.print(commandOutput);
        }
    }

        scanner.close();
    }

}
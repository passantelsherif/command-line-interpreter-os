# Command Line Interpreter (CLI) - Operating Systems 

A Java-based command line interpreter implementing core shell commands with comprehensive file system operations. This project was developed for the Cairo University CS241 (Operating Systems 1).

## 📋 Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Implemented Commands](#implemented-commands)
- [Features](#features)
- [Installation & Setup](#installation--setup)
- [Usage Guide](#usage-guide)
- [Architecture](#architecture)
- [Implementation Details](#implementation-details)
- [Error Handling](#error-handling)
- [Code Quality](#code-quality)
- [Testing Examples](#testing-examples)


---

## 🎯 Overview

This Command Line Interpreter (CLI) is a Java application that mimics the behavior of standard Unix/Linux shell commands. It provides an interactive terminal interface where users can:

- Navigate the file system
- Create and delete files and directories
- Manipulate file contents
- Compress and decompress files
- Redirect command output to files

The CLI continuously accepts user input until the `exit` command is issued, making it a fully functional standalone shell application.

### Key Characteristics:
- **Language**: Java
- **Paradigm**: Object-Oriented Programming (OOP)
- **File Operations**: Native Java NIO (java.nio.file)
- **Architecture**: Two-class design (Parser + Terminal)
- **Status**: ✅ Complete with all assignment requirements met

---

## 📂 Project Structure

```
command-line-interpreter-os/
├── Terminal.java          # Main implementation (Parser + Terminal classes)
├── README.md              # This comprehensive documentation
└── [Test files/dirs]      # Created during runtime testing
```

### Class Architecture

#### **1. Parser Class**
Responsible for parsing user input and extracting commands and arguments.

**Attributes:**
- `String commandName` - The entered command
- `String[] args` - Command arguments array

**Methods:**
- `parse(String input)` - Parses input string into command and arguments
- `getCommandName()` - Returns the command name
- `getArgs()` - Returns the arguments array

#### **2. Terminal Class**
The main class implementing all CLI commands and managing the terminal interface.

**Attributes:**
- `static Parser parser` - Shared parser instance
- `String path` - Current working directory path
- `boolean validCommand` - Flag for command validation

**Methods:**
- Command implementations (pwd, cd, ls, mkdir, rmdir, touch, cp, rm, cat, wc, zip, unzip)
- `chooseCommandAction()` - Command dispatcher
- `main()` - Main entry point with input loop and output redirection

---

## 🔧 Implemented Commands

### 1. **pwd** - Print Working Directory
```bash
pwd
```
**Functionality**: Displays the absolute path of the current directory.

**Example Output**:
```
Current Directory (System Property): C:\Users\YourName\Documents
```

---

### 2. **cd** - Change Directory
```bash
# Go to home directory
cd

# Go to parent directory
cd ..

# Change to specific directory (relative or absolute path)
cd folder_name
cd C:\Users\YourName\Desktop
cd ../relative/path
```

**Functionality**:
- With no arguments: Changes to user's home directory
- With `..`: Moves to parent directory
- With path: Changes to specified directory

**Features**:
- Supports both absolute and relative paths
- Path normalization to prevent directory traversal issues
- Error handling for non-existent directories

---

### 3. **ls** - List Directory Contents
```bash
ls
```
**Functionality**: Lists all files and directories in the current directory, sorted alphabetically.

**Output Format**:
```
[DIR]  Documents
[DIR]  Downloads
[FILE] report.pdf
[FILE] script.jar
```

---

### 4. **mkdir** - Make Directory
```bash
# Create single directory
mkdir my_folder

# Create multiple directories
mkdir folder1 folder2 folder3

# Create in specific path
mkdir path/to/new_folder
```

**Functionality**: Creates one or more directories.

**Features**:
- Multiple directory creation in single command
- Supports absolute and relative paths
- Handles nested path creation with `createDirectories()`
- Error checking for existing directories

---

### 5. **rmdir** - Remove Directory
```bash
# Remove all empty directories in current directory
rmdir *

# Remove specific directory (must be empty)
rmdir folder_name
rmdir path/to/folder
```

**Functionality**: Removes empty directories.

**Features**:
- Wildcard support (`*`) to remove all empty directories at once
- Only removes empty directories (fails if non-empty)
- Supports both absolute and relative paths
- Detailed error messages

---

### 6. **touch** - Create File
```bash
touch filename.txt
touch path/to/filename.txt
```

**Functionality**: Creates a new empty file.

**Features**:
- Supports absolute and relative paths
- Error handling for existing files
- Nested directory support

---

### 7. **cp** - Copy Files & Directories
```bash
# Copy file to another file
cp source.txt destination.txt

# Copy directory recursively
cp -r source_directory destination_directory
```

**Functionality**: Copies files or directories.

**Features**:
- **Simple copy**: Requires both arguments to be files
- **Recursive copy** (`cp -r`): Copies entire directory structure
- Preserves file attributes when using `-r` flag
- Overwrites destination if it exists
- Comprehensive error handling

---

### 8. **rm** - Remove File
```bash
rm filename.txt
rm file1.txt file2.txt file3.txt
```

**Functionality**: Deletes one or more files permanently.

**Features**:
- Multiple file deletion support
- Only removes regular files (not directories)
- Detailed error messages for each file

---

### 9. **cat** - Concatenate & Display File Content
```bash
# Display single file
cat filename.txt

# Display multiple files
cat file1.txt file2.txt
```

**Functionality**: Reads and displays file contents.

**Features**:
- Supports multiple files
- Displays content in sequential order
- File existence validation
- Error handling with informative messages

---

### 10. **wc** - Word Count
```bash
wc filename.txt
wc file1.txt file2.txt
```

**Output Format**:
```
Lines: 9, Words: 79, chars: 483 in: filename.txt
```

**Functionality**: Counts lines, words, and characters in files.

**Features**:
- Multiple file support
- Accurate word counting (whitespace-based splitting)
- Character count includes newlines
- Per-file statistics

---

### 11. **zip** - Compress Files & Directories
```bash
# Compress single or multiple files
zip archive.zip file1.txt file2.jpg file3.pdf

# Compress entire directory recursively
zip -r archive.zip my_folder/
```

**Functionality**: Creates ZIP archives.

**Features**:
- **Basic mode**: Compresses multiple files
- **Recursive mode** (`-r`): Includes subdirectories and all contents
- Proper directory structure preservation in archive
- Cross-platform path handling (Windows backslash conversion)
- Detailed progress feedback

**Example**:
```
Added: file1.txt
Added: file2.jpg
ZIP file created successfully: archive.zip
```

---

### 12. **unzip** - Extract ZIP Archives
```bash
unzip archive.zip
```

**Functionality**: Extracts all files from a ZIP archive into the current directory.

**Features**:
- Creates directories as needed
- Handles both files and directories in archive
- Cross-platform compatibility
- Progress reporting for each extracted file
- Error handling for corrupted archives

---

### 13. **Output Redirection**

#### **> (Overwrite)**
```bash
ls > file_list.txt
pwd > current_path.txt
```
Redirects command output to a file, **overwriting** if file exists.

#### **>> (Append)**
```bash
ls >> file_list.txt
cat log.txt >> combined_log.txt
```
Redirects command output to a file, **appending** if file exists.

**Features**:
- Works with any command that produces output
- Creates file if it doesn't exist
- Supports both absolute and relative paths
- Confirmation message after redirection

---

### 14. **exit** - Terminate CLI
```bash
exit
```
Cleanly terminates the CLI application.

---

## ✨ Features

### Path Handling
- ✅ **Absolute paths**: Full system paths (e.g., `C:\Users\folder` or `/home/user/folder`)
- ✅ **Relative paths**: Short paths relative to current directory (e.g., `../folder`, `./file.txt`)
- ✅ **Path normalization**: Prevents directory traversal vulnerabilities
- ✅ **Cross-platform support**: Handles both Windows and Unix-style paths

### Error Handling
- ✅ Custom error messages for invalid operations
- ✅ File/directory existence validation
- ✅ Type checking (file vs. directory)
- ✅ Permission and I/O error handling
- ✅ Unknown command detection
- ✅ Invalid argument validation

### Advanced Features
- ✅ **Output buffering**: Captures command output for redirection
- ✅ **Recursive operations**: Directory copying and compression
- ✅ **Batch operations**: Multiple file/directory handling in single command
- ✅ **Interactive prompt**: Shows current directory in terminal prompt
- ✅ **Persistent state**: Maintains working directory across commands

---

## 🚀 Installation & Setup

### Prerequisites
- **Java Development Kit (JDK)** version 8 or higher
- **Java Compiler** (javac)
- **Terminal/Command Prompt** for running the application

### Compilation

```bash
# Navigate to project directory
cd command-line-interpreter-os

# Compile the Java file
javac Terminal.java
```

### Running the CLI

```bash
# Run the compiled application
java Terminal
```

You should see the prompt:
```
/current/directory/path $
```

---

## 📖 Usage Guide

### Basic Workflow Example

```bash
# 1. Check current directory
/home/user $ pwd
Current Directory (System Property): /home/user

# 2. Create new folders
/home/user $ mkdir projects documents

# 3. Navigate to projects folder
/home/user $ cd projects

# 4. Create files
/home/user/projects $ touch myfile.txt

# 5. List contents
/home/user/projects $ ls
[FILE] myfile.txt

# 6. Go back to parent
/home/user/projects $ cd ..

# 7. Create archive
/home/user $ zip -r my_backup.zip projects/

# 8. Exit CLI
/home/user $ exit
```

### Advanced Examples

#### Creating a directory structure
```bash
/home $ mkdir project/src/main/java
```

#### Copying and backing up
```bash
/backup $ cp -r documents ./documents_backup
```

#### Searching file content and redirecting
```bash
/logs $ cat app.log >> combined_logs.txt
```

#### Creating compressed backups
```bash
/backup $ zip -r backup_2026.zip projects/
```

---

## 🏗️ Architecture

### Design Pattern: Command Dispatcher Pattern

The CLI uses a **command dispatcher pattern** for command execution:

```
User Input
    ↓
Parser (parse input string)
    ↓
Command Dispatcher (chooseCommandAction)
    ↓
Execute Command Method
    ↓
Display Output / Handle Redirection
```

### Data Flow

```
┌─────────────────────────────────────┐
│   Terminal (Main REPL Loop)         │
│  while(true): input → parse → exec  │
└─────────────────────────────────────┘
         ↓
┌─────────────────────────────────────┐
│   Parser                             │
│   - Tokenizes input                  │
│   - Extracts command & arguments     │
└─────────────────────────────────────┘
         ↓
┌─────────────────────────────────────┐
│   Command Handler Methods            │
│   (pwd, cd, ls, mkdir, etc.)        │
└─────────────────────────────────────┘
         ↓
┌─────────────────────────────────────┐
│   Output Handler                     │
│   - Buffer capture                   │
│   - File redirection (>, >>)         │
│   - Console display                  │
└─────────────────────────────────────┘
```

---

## 💾 Implementation Details

### Key Technologies Used

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **File Operations** | `java.nio.file.*` | Modern file handling |
| **Path Handling** | `java.nio.file.Paths` | Cross-platform paths |
| **Directory Traversal** | `java.nio.file.DirectoryStream` | Efficient directory listing |
| **Compression** | `java.util.zip.*` | ZIP file creation/extraction |
| **I/O Streams** | `java.io.*` | File reading/writing |
| **Output Redirection** | `ByteArrayOutputStream` | Command output buffering |
| **User Input** | `java.util.Scanner` | Console input handling |

### Critical Implementation Points

#### 1. **Output Redirection System**
```java
// Captures output
ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(outputBuffer);
System.setOut(ps);

// Execute command
terminal.chooseCommandAction(terminal);

// Restore and redirect
System.setOut(oldOut);
String commandOutput = outputBuffer.toString();

// Write to file if needed
if (redirectFile != null) {
    Files.writeString(redirectPath, commandOutput, ...);
}
```

#### 2. **Path Normalization**
```java
Path resolvedPath = Paths.get(path).resolve(targetPath).normalize();
```
This prevents directory traversal attacks and normalizes paths across platforms.

#### 3. **Recursive Directory Operations**
```java
Files.walk(resolvedDirPath).forEach(source -> {
    // Process each file/directory recursively
});
```

#### 4. **Error Handling Pattern**
Every command validates:
- Argument count
- File/directory existence
- Type correctness (file vs. directory)
- I/O exceptions

---

## ⚠️ Error Handling

The CLI implements comprehensive error handling:

| Error Type | Example Message | Handling |
|-----------|-----------------|----------|
| Invalid Command | `Error: Unknown command 'xyz'` | Display error, continue loop |
| File Not Found | `File not found: myfile.txt` | Skip file, continue |
| Directory Not Empty | `Error: This is not directory or is not empty` | Show error, continue |
| Invalid Path | `The system cannot find the path specified.` | Show error, maintain state |
| I/O Exception | `Error: Failed to create 'name': ...` | Detailed error message |
| Missing Arguments | `Error: Please specify at least one file to remove.` | Guide user |



## 💻 Code Quality

### Best Practices Implemented
- ✅ **OOP Principles**: Encapsulation with Parser and Terminal classes
- ✅ **Error Handling**: Try-catch blocks with meaningful messages
- ✅ **Code Comments**: Detailed explanations of complex logic
- ✅ **Method Documentation**: Clear purpose for each method
- ✅ **Resource Management**: Proper stream closing (try-with-resources)
- ✅ **Path Safety**: Normalized paths prevent security issues
- ✅ **User Feedback**: Informative prompts and success messages

---

## 🔍 Testing Examples

### Test Case 1: Basic Navigation
```bash
$ pwd
Current Directory (System Property): C:\Users\User

$ cd Desktop
Changed directory to: C:\Users\User\Desktop

$ cd ..
Current Directory: C:\Users\User
```

### Test Case 2: File Operations
```bash
$ mkdir TestFolder
Directory created: C:\Users\User\TestFolder

$ touch TestFolder/test.txt
File created: C:\Users\User\TestFolder\test.txt

$ ls
[DIR]  TestFolder

$ rm TestFolder/test.txt
File got deleted: test.txt
```

### Test Case 3: Output Redirection
```bash
$ ls > file_list.txt
Output written to: C:\Users\User\file_list.txt

$ cat file_list.txt
TestFolder

$ pwd >> file_list.txt
Output written to: C:\Users\User\file_list.txt
```

### Test Case 4: Compression
```bash
$ zip -r backup.zip TestFolder/
Added: test.txt
Directory zipped recursively successfully: backup.zip

$ unzip backup.zip
Created directory: TestFolder
Extracted: test.txt
Unzip completed successfully!
```

---


## ✨ Summary

This Command Line Interpreter is a fully functional, feature-rich shell implementation demonstrating:
- **Strong OOP design** with clear separation of concerns
- **Comprehensive command implementation** covering all assignment requirements  
- **Robust error handling** for edge cases and invalid inputs
- **Cross-platform compatibility** for Windows and Unix systems
- **Professional code quality** with documentation and best practices

The CLI successfully passes all assignment requirements and provides a practical, usable terminal interface for file system operations.

---


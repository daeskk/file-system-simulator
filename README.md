# Simulated-File-System

This project is a simulation of the EXT2 file system in Java. Run it with either eclipse or Intellij. It creates a virtual disk with ~100MB on the desktop to store the files copied to it. It supports at max files with 49152 bytes.

## Available operations:

|COMMAND|ACTION|
|----------|-------------------
|`cd`|change the current directory
|`mkdir`|create a new directory
|`rm`|delete a file or an empty directory
|`cp`|copy a file from the computer to the virtual disk and vice versa. To specify that you are acessing the virtual disk, you need to pass "dsc/" before the file name that will be saved on the virtual disk
|`mv`|move a directory
|`ls`|list the current directory
|`pwd`|show the current directory from the root folder
|`format`|reset the virtual disk

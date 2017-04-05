package com.advance.library.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Description: FileUtils <br>
 * Creator: Hongd <br>
 * Date: 2017/3/22 16:48 <br>
 * Email: tobu794@163.com <br>
 * Version: 1.0 <br>
 */
public class FileUtils {

  public static void writeDataToSD(String str) {
    try {
    /* 获取File对象，确定数据文件的信息 */
      //File file  = new File(Environment.getExternalStorageDirectory()+"/f.txt");
      File file = new File(Environment.getExternalStorageDirectory(), "f.txt");
    /* 判断sd的外部设置状态是否可以读写 */
      if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = str.getBytes();
        fos.write(buffer);
        fos.close();
      }
      Log.e("success", "sd");
    } catch (Exception ex) {
      Log.e("firie", "ss");
    }
  }

  /**
   * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
   */

  public static void write(Context context, String fileName, String content) {
    if (content == null) {
      content = "";
    }

    try {
      FileOutputStream fos = context.openFileOutput(fileName,
          Context.MODE_PRIVATE);
      fos.write(content.getBytes());

      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 读取文本文件
   */
  public static String read(Context context, String fileName) {
    try {
      FileInputStream in = context.openFileInput(fileName);
      return readInStream(in);
    } catch (Exception e) {
      // e.printStackTrace();
      return "";
    }

  }

  private static String readInStream(FileInputStream inStream) {
    try {
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inStream.read(buffer)) != -1) {
        outStream.write(buffer, 0, length);
      }
      outStream.close();
      inStream.close();
      byte[] content_byte = outStream.toByteArray();
      return outStream.toString();
    } catch (IOException e) {

    }
    return null;
  }

  public static File createFile(String folderPath, String fileName) {
    File destDir = new File(folderPath);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    return new File(folderPath, fileName + fileName);
  }

  /**
   *
   *
   * @param buffer
   * @param folder
   * @param fileName
   * @return
   */
  public static boolean writeFile(byte[] buffer, String folder,
      String fileName) {
    boolean writeSucc = false;

    boolean sdCardExist = Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED);

    String folderPath = "";
    if (sdCardExist) {
      folderPath = Environment.getExternalStorageDirectory()
          + File.separator + folder + File.separator;
    } else {
      writeSucc = false;
    }

    File fileDir = new File(folderPath);
    if (!fileDir.exists()) {
      fileDir.mkdirs();
    }

    File file = new File(folderPath + fileName);
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(file);
      out.write(buffer);
      writeSucc = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return writeSucc;
  }

  /**
   * 根据文件绝对路径获取文件名
   */
  public static String getFileName(String filePath) {
    if (null == filePath || "".equals(filePath)) {
      return "";
    }
    return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
  }

  /**
   * 根据文件的绝对路径获取文件名但不包含扩展名
   */
  public static String getFileNameNoFormat(String filePath) {
    if (null == filePath || "".equals(filePath)) {
      return "";
    }
    int point = filePath.lastIndexOf('.');
    return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
        point);
  }

  /**
   * 获取文件扩展名
   */
  public static String getFileFormat(String fileName) {
    if (null == fileName || "".equals(fileName)) {
      return "";
    }

    int point = fileName.lastIndexOf('.');
    return fileName.substring(point + 1);
  }

  /**
   * 获取文件大小
   */
  public static long getFileSize(String filePath) {
    long size = 0;

    File file = new File(filePath);
    if (file != null && file.exists()) {
      size = file.length();
    }
    return size;
  }

  /**
   * 获取文件大小
   *
   * @param size 字节
   */
  public static String getFileSize(long size) {
    if (size <= 0) {
      return "0";
    }
    java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
    float temp = (float) size / 1024;
    if (temp >= 1024) {
      return df.format(temp / 1024) + "M";
    } else {
      return df.format(temp) + "K";
    }
  }

  /**
   * 转换文件大小
   *
   * @return B/KB/MB/GB
   */
  public static String formatFileSize(long fileS) {
    java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
    String fileSizeString = "";
    if (fileS < 1024) {
      fileSizeString = df.format((double) fileS) + "B";
    } else if (fileS < 1048576) {
      fileSizeString = df.format((double) fileS / 1024) + "KB";
    } else if (fileS < 1073741824) {
      fileSizeString = df.format((double) fileS / 1048576) + "MB";
    } else {
      fileSizeString = df.format((double) fileS / 1073741824) + "G";
    }
    return fileSizeString;
  }

  /**
   * 获取目录文件大小
   */
  public static long getDirSize(File dir) {
    if (dir == null) {
      return 0;
    }
    if (!dir.isDirectory()) {
      return 0;
    }
    long dirSize = 0;
    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isFile()) {
        dirSize += file.length();
      } else if (file.isDirectory()) {
        dirSize += file.length();
        dirSize += getDirSize(file); // 递归调用继续统计
      }
    }
    return dirSize;
  }

  /**
   * 获取目录文件个数
   */
  public long getFileList(File dir) {
    long count = 0;
    File[] files = dir.listFiles();
    count = files.length;
    for (File file : files) {
      if (file.isDirectory()) {
        count = count + getFileList(file);// 递归
        count--;
      }
    }
    return count;
  }

  public static byte[] toBytes(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int ch;
    while ((ch = in.read()) != -1) {
      out.write(ch);
    }
    byte buffer[] = out.toByteArray();
    out.close();
    return buffer;
  }

  /**
   * 检查文件是否存在
   */
  public static boolean checkFileExists(String name) {
    boolean status;
    if (!name.equals("")) {
      File path = Environment.getExternalStorageDirectory();
      File newPath = new File(path.toString() + name);
      status = newPath.exists();
    } else {
      status = false;
    }
    return status;

  }

  /**
   * 计算SD卡的剩余空间
   *
   * @return 返回-1，说明没有安装sd卡
   */
  public static long getFreeDiskSpace() {
    String status = Environment.getExternalStorageState();
    long freeSpace = 0;
    if (status.equals(Environment.MEDIA_MOUNTED)) {
      try {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        freeSpace = availableBlocks * blockSize / 1024;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      return -1;
    }
    return (freeSpace);
  }

  /**
   * 新建目录
   */
  public static boolean createDirectory(String directoryName) {
    boolean status;
    if (!directoryName.equals("")) {
      File path = Environment.getExternalStorageDirectory();
      File newPath = new File(path.toString() + directoryName);
      status = newPath.mkdir();
      status = true;
    } else {
      status = false;
    }
    return status;
  }

  /**
   * 检查是否安装SD卡
   */
  public static boolean checkSaveLocationExists() {
    String sDCardStatus = Environment.getExternalStorageState();
    boolean status;
    if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
      status = true;
    } else {
      status = false;
    }
    return status;
  }

  /**
   * 删除目录(包括：目录里的所有文件)
   */
  public static boolean deleteDirectory(String fileName) {
    boolean status;
    SecurityManager checker = new SecurityManager();

    if (!fileName.equals("")) {

      File path = Environment.getExternalStorageDirectory();
      File newPath = new File(path.toString() + fileName);
      checker.checkDelete(newPath.toString());
      if (newPath.isDirectory()) {
        String[] listfile = newPath.list();
        // delete all files within the specified directory and then
        // delete the directory
        try {
          for (int i = 0; i < listfile.length; i++) {
            File deletedFile = new File(newPath.toString() + "/"
                + listfile[i].toString());
            deletedFile.delete();
          }
          newPath.delete();
          status = true;
        } catch (Exception e) {
          e.printStackTrace();
          status = false;
        }

      } else {
        status = false;
      }
    } else {
      status = false;
    }
    return status;
  }

  /**
   * 删除文件
   */
  public static boolean deleteFile(String fileName) {
    boolean status;
    SecurityManager checker = new SecurityManager();

    if (!fileName.equals("")) {

      File path = Environment.getExternalStorageDirectory();
      File newPath = new File(path.toString() + fileName);
      checker.checkDelete(newPath.toString());
      if (newPath.isFile()) {
        try {
          newPath.delete();
          status = true;
        } catch (SecurityException se) {
          se.printStackTrace();
          status = false;
        }
      } else {
        status = false;
      }
    } else {
      status = false;
    }
    return status;
  }

  public static List<File> list(File dir, String nametxt, String ext,
      String type, List<File> fs) {

    listFile(dir, nametxt, type, ext, fs);
    File[] all = dir.listFiles();
    // 递归获得当前目录的所有子目录
    for (int i = 0; i < all.length; i++) {
      File d = all[i];
      if (d.isDirectory()) {
        list(d, nametxt, ext, type, fs);
      }
    }
    return null;
    // 遍历子目,列出每个子目录的文
  }

  /**
   * @param dir 根目录
   * @param nametxt 文件名中包含的关键字
   * @param type 文件夹的类型
   * @param ext 后缀
   * @param fs 返回的结果
   */
  private static List<File> listFile(File dir, String nametxt, String type,
      String ext, List<File> fs) {
    File[] all = dir.listFiles((FileFilter) new Fileter(ext));
    for (int i = 0; i < all.length; i++) {
      File d = all[i];
      if (d.getName().toLowerCase().indexOf(nametxt.toLowerCase()) >= 0) {
        if (type.equals("1")) {
          fs.add(d);
        } else if (d.isDirectory() && type.equals("2")) {
          fs.add(d);
        } else if (!d.isDirectory() && type.equals("3")) {
          fs.add(d);
        }
      }

    }
    return fs;
  }

  public static boolean delFile(String filePathAndName) {
    boolean bea = false;
    try {
      String filePath = filePathAndName;
      File myDelFile = new File(filePath);
      if (myDelFile.exists()) {
        myDelFile.delete();
        bea = true;
      } else {
        bea = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bea;
  }

  static class Fileter implements FilenameFilter {

    private final String ext;

    public Fileter(String ext) {
      this.ext = ext;
    }

    @Override
    public boolean accept(File dir, String name) {
      return name.endsWith(ext);

    }
  }

  public static void copyFile(String oldPathFile, String newPathFile) {
    try {
      int bytesum = 0;
      int byteread = 0;
      File oldfile = new File(oldPathFile);
      if (oldfile.exists()) { // 文件存在
        InputStream inStream = new FileInputStream(oldPathFile); // 读入源文�?
        File n = new File(newPathFile);
        if (!n.exists()) {
          n.createNewFile();
        }
        FileOutputStream fs = new FileOutputStream(newPathFile);
        byte[] buffer = new byte[1444];
        while ((byteread = inStream.read(buffer)) != -1) {
          bytesum += byteread; // 字节 文件大小
          fs.write(buffer, 0, byteread);
        }
        fs.flush();
        fs.close();
        inStream.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void moveFile(String oldPath, String newPath) {
    copyFile(oldPath, newPath);
    delFile(oldPath);
  }

}

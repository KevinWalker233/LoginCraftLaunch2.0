package me.logincraftlaunch.utils;

import me.logincraftlaunch.main.Main;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    //图片转化成base64字符串
    public static String GetImageStr(String imgFile) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    //base64字符串转化成图片
    public static boolean GenerateImage(String imgStr, String imgFilePath) {
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static File getBaseDir() {
        try {
            File file = new File(URLDecoder
                    .decode(Main.class.getProtectionDomain().getCodeSource().getLocation().toString(), "utf-8")
                    .substring(6));
            return file.getParentFile();
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 江jar内文件加载为 byte[] 格式
     *
     * @param path 不带第一个斜杠的路径
     * @return byte[]
     */

    public static byte[] loadResource(String path) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(path);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 江内部文件挪到外部
     *
     * @param path   内部路径
     * @param target 生成路径
     */
    public static void saveResource(String path, File target) {
        try {
            InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(path);
            byte[] buffer = new byte[1024];
            int length;
            OutputStream out = new FileOutputStream(target);
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件
     *
     * @param file 文件位置
     * @param type 文件尾缀
     * @return 文件名List
     */
    public static List<String> getFileList(File file, String type) {
        List<String> result = new ArrayList<>();
        if (!file.isDirectory()) {
            System.out.println(file.getAbsolutePath());
            result.add(file.getAbsolutePath());
        }
        else {
            File[] directoryList = file.listFiles(file1 -> file1.isFile() && file1.getName().endsWith(type));
            for (File aDirectoryList : directoryList) {
                result.add(aDirectoryList.getName().substring(0, aDirectoryList.getName().length() - type.length() - 1));
            }
        }
        return result;
    }

    /**
     * 获取文件夹内的所有文件夹
     *
     * @param file
     * @return
     */
    public static ArrayList<String> getDirectorys(File file) {
        ArrayList<String> files = new ArrayList<>();
        File[] tempList = file.listFiles();
        assert tempList != null;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                files.add(tempList[i].getName());
            }
        }
        return files;
    }

    /**
     * 获取文件夹内的所有文件
     *
     * @param file
     * @return
     */
    public static ArrayList<String> getFiles(File file) {
        ArrayList<String> files = new ArrayList<>();
        File[] tempList = file.listFiles();
        assert tempList != null;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i].toString());
            }
        }
        return files;
    }

    public static void write(byte[] buf, File des) {
        try {
            des.delete();
            des.createNewFile();
            FileOutputStream stream = new FileOutputStream(des);
            stream.write(buf);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String content, File des, String charset) {
        try {
            write(content.getBytes(charset), des);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String toString(InputStream in, String charset) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int length = -1;
        byte[] buffer = new byte[1024];
        try {
            while ((length = in.read(buffer)) != -1)
                stream.write(buffer, 0, length);
            return new String(stream.toByteArray(), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取压缩文件流
     *
     * @param source 文件目录
     * @param fileName 文件名
     * @return
     */
    public static ZipInputStream getInputStream(File source, String fileName) {
        try {
            ZipFile zipFile = new ZipFile(source);
            return zipFile.getInputStream(zipFile.getFileHeader(fileName));
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toString(File file, String charset) {
        try {
            if (file.exists())
                return toString(new FileInputStream(file), charset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "{}";
    }
}

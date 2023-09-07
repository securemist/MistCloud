package com.mist.cloud.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author: securemist
 * @Datetime: 2023/8/22 12:56
 * @Description:
 */
public class ZipUtils {
    public static void zipFolder(String sourceFolderPath, String zipFilePath) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File sourceFolder = new File(sourceFolderPath);
            addFolderToZip(sourceFolder, sourceFolder.getName(), zos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFolderToZip(File folder, String parentFolderName, ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();
        byte[] buffer = new byte[1024];

        for (File file : files) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolderName + "/" + file.getName(), zos);
            } else {
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(parentFolderName + "/" + file.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                fis.close();
            }
        }
    }
}

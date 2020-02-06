package pl.snikk.wifistorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFile {
    private ZipOutputStream zipFile;

    public ZipFile(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            zipFile = new ZipOutputStream(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean addFile(String path) {
        Map<String, String> list = createFileList(path, path);
        byte[] buffer = new byte[1024];

        try {
            for (Entry<String, String> paths : list.entrySet()) {
                ZipEntry ze = new ZipEntry(paths.getValue());
                zipFile.putNextEntry(ze);
                FileInputStream in = new FileInputStream(paths.getKey());
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zipFile.write(buffer, 0, len);
                }

                in.close();
                zipFile.closeEntry();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean close() {
        try {
            zipFile.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Map<String, String> createFileList(String path, String relativeTo) {
        Map<String, String> list = new HashMap<String, String>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files)
                list.putAll(createFileList(subFile.getAbsolutePath(),
                        relativeTo));
        } else {
            list.put(path, path.substring(relativeTo.lastIndexOf("/") + 1));
        }
        return list;
    }

    public static boolean unZip(String zipFile, String outputFolder){
        byte[] buffer = new byte[1024];

        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(zipFile));

            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator
                        + fileName);

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        } catch (IOException ex) {
            return false;
        }
        return true;
    }
}


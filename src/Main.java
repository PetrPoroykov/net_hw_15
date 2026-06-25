import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    static StringBuilder log = new StringBuilder();

    public static void main(String[] args) {
        File src = new File("D://Games/src");
        mkDir(src);
        File res = new File("D://Games/res");
        mkDir(res);
        File savegames = new File("D://Games/savegames");
        mkDir(savegames);
        File temp = new File("D://Games/temp");
        mkDir(temp);
        File drawables = new File("D://Games/res/drawables");
        mkDir(drawables);
        File vectors = new File("D://Games/res/vectors");
        mkDir(vectors);
        File icons = new File("D://Games/res/icons");
        mkDir(icons);
        File tempTxt = new File("D://Games/temp/temp.txt");
        mkFile(tempTxt);
        File mainJava = new File("D://Games/src/Main.java");
        mkFile(mainJava);
        File utilsJava = new File("D://Games/src/Utils.java");
        mkFile(utilsJava);

        try (FileWriter writer = new FileWriter("D://Games/temp/temp.txt", false)) {
            writer.write(log.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


        List<GameProgress> progressList = new ArrayList<>();
        progressList.add(new GameProgress(1, 2, 3, 4.5));
        progressList.add(new GameProgress(4, 5, 6, 4.6));
        progressList.add(new GameProgress(7, 8, 9, 4.7));

        for (int i = 0; i < progressList.size(); i++) {
            saveGame("D://Games/savegames/save" + i + ".dat", progressList.get(i));
        }
        File[] files = savegames.listFiles();
        List<String> filesAbsolutePath = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            filesAbsolutePath.add(files[i].getAbsolutePath());
        }

        zipFiles("D://Games/savegames/save.zip", filesAbsolutePath);

        delFiles(filesAbsolutePath);

        unarchive("D://Games/savegames/save.zip");

        GameProgress gameProgress = null;

        deserialization("D://Games/savegames/unzipped_save0.dat", gameProgress);
    }

    public static void mkDir(File dir) {
        String inf;
        if (dir.mkdir()) {
            inf = "Директория " + dir.getName() + " создана. Полный путь - " + dir.getAbsolutePath();
        } else {
            inf = "Директория " + dir.getName() + " не создана ";
        }
        log = log.append(inf);
        log = log.append("\n");
    }

    public static void mkFile(File file) {
        try {
            String inf;
            if (file.createNewFile()) {
                inf = "Файл " + file.getName() + " создан. Полный путь - " + file.getAbsolutePath();
            } else {
                inf = "Файл " + file.getName() + " не создан ";
            }
            log = log.append(inf);
            log = log.append("\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void saveGame(String path, GameProgress progress) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(progress);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    public static void zipFiles(String zipPath, List<String> filesToZipAdress) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath))) {
            byte[] buffer = null;
            for (String fileToZipAdress : filesToZipAdress) {
                try (FileInputStream fis = new FileInputStream(fileToZipAdress)) {
                    ZipEntry entry = new ZipEntry(new File(fileToZipAdress).getName());
                    zout.putNextEntry(entry);
                    buffer = new byte[fis.available()];
                    fis.read(buffer);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                zout.write(buffer);
                zout.closeEntry();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void delFiles(List<String> paths) {
        for (String path : paths) {
            new File(path).delete();
        }
    }

    public static void unarchive(String path) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(path))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = "D://Games/savegames/unzipped_" + entry.getName(); // получим название файла
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deserialization(String path, GameProgress progress) {
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            progress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(progress);
    }
}


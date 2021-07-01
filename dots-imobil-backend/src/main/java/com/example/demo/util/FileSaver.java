package com.example.demo.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSaver {
    private static final String ROOT = "src/main/resources/images";

    public static void delete(String path) throws IOException {
        Files.deleteIfExists(Paths.get("src/main/resources/" + path));
    }

    public static String save(MultipartFile file, String folder, String filename) throws IOException {
        if (isValidFilename(filename)) {
            filename = getAvailableFilename(folder, filename);
            Path path = getRoot(folder, filename);
            Files.write(path, file.getBytes());
            return getResourcePath(path.toString());
        }
        else if (filename == null || filename.isEmpty())
            return null;
        else
            return ROOT;
    }

    public static String saveResized(String folder, String originalFilename, String filename) throws IOException {
        if(isValidFilename(filename)) {
            Image image = ImageIO.read(new File(ROOT + "/" + folder + "/" + originalFilename));
            BufferedImage originalImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            originalImage.getGraphics().drawImage(image, 0, 0, null);
            float widthRatio = (float)originalImage.getWidth() / ImageUtil.THUMBNAIL_WIDTH;
            float heightRatio =(float)originalImage.getHeight() / ImageUtil.THUMBNAIL_HEIGHT;

            BufferedImage resizedImage;
            int resizedWidth;
            int resizedHeight;
            if(widthRatio > heightRatio){ //shrink to fixed height
                resizedWidth = Math.round(originalImage.getWidth() / heightRatio);
                resizedHeight = ImageUtil.THUMBNAIL_HEIGHT;
            }  else{ //shrink to fixed width
                resizedWidth = ImageUtil.THUMBNAIL_WIDTH;
                resizedHeight = Math.round(originalImage.getHeight() / widthRatio);
            }
            resizedImage = resizeImage(originalImage, originalImage.getType(), resizedWidth, resizedHeight);

            int startX = resizedWidth/2 - ImageUtil.THUMBNAIL_WIDTH/2;
            int startY = resizedHeight/2 - ImageUtil.THUMBNAIL_HEIGHT/2;
            BufferedImage subImage = resizedImage.getSubimage(startX, startY, ImageUtil.THUMBNAIL_WIDTH, ImageUtil.THUMBNAIL_HEIGHT);
            filename = getAvailableFilename(folder, filename);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(subImage, filename.substring(filename.lastIndexOf(".") + 1), stream);
            Path path = getRoot(folder, filename);
            Files.write(path, stream.toByteArray());
            return getResourcePath(path.toString());
        }
        else if (filename == null || filename.isEmpty())
            return null;
        else
            return ROOT;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type,
                                             Integer imgWidth, Integer imgHeight) {
        BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
        g.dispose();

        return resizedImage;
    }

    private static String getAvailableFilename(String folder, String filename) {
        if (Files.exists(Paths.get(ROOT + "/" + folder + "/" + filename))) {
            int position = filename.indexOf('.');
            String extension = filename.substring(position + 1);
            String name = filename.substring(0, position);
            int index = 0;
            while (Files.exists(Paths.get(ROOT + "/" + folder + "/" + filename))) {
                index += 1;
                filename = name + "_" + index + "." + extension;
            }
        }
        return filename;
    }

    private static boolean isValidFilename(String filename) {
        return filename != null && (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".webp") || filename.endsWith(".jpeg2000"));
    }

    private static Path getRoot(String folder, String filename) {
        return Paths.get(ROOT + "/" + folder + "/" + filename);
    }

    private static String getResourcePath(String path) {
        int index = path.indexOf("resources");
        return path.substring(index + 10).replace('\\', '/');
    }
}

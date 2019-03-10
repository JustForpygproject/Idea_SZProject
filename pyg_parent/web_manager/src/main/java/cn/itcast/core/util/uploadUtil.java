package cn.itcast.core.util;

import org.apache.poi.util.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class uploadUtil {
    public static void upLoadFile(MultipartFile uploadFile) throws IOException {
        String uploadPath = "D:\\XIAZAIZILIAO\\code\\Idea_SZProject\\shizhan\\pyg_parent";
        File uploadFlie = new File(uploadPath);
        if (!uploadFlie.exists()) {
            uploadFlie.mkdir();
        }
        MultipartFile file = uploadFile;
        String uploadFileName = file.getOriginalFilename();
        InputStream isRef = file.getInputStream();
        File targetFile = new File(uploadPath, uploadFileName);
        FileOutputStream fosRef = new FileOutputStream(targetFile);
        IOUtils.copy(isRef, fosRef);
        fosRef.close();
        isRef.close();
    }

}
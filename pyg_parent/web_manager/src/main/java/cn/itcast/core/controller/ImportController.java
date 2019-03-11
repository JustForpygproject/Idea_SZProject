package cn.itcast.core.controller;


import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.service.ExcelService;
import cn.itcast.core.util.uploadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("upload")
public class ImportController {

    @Reference
    private ExcelService excelService;

    @RequestMapping("importExcel")
    public Result importExcel(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        try {
            uploadUtil.upLoadFile(file);
            excelService.importExcel();
            return new Result(true,"导入成功!");
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false,"导入失败!");
        }

    }

}

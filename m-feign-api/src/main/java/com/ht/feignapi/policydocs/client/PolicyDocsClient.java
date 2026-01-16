package com.ht.feignapi.policydocs.client;

import com.ht.feignapi.policydocs.vo.SubItemCategoryFileVo;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@FeignClient(value = "${custom.client.policydocs.name}")
public interface PolicyDocsClient {

    @PostMapping("/es/categoryFile/save")
    Result saveESCategoryFile(@RequestParam("file") File file);


    @RequestMapping(value="/subItemCategoryFile/save",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    Result saveSubItemCategoryFile(@RequestBody SubItemCategoryFileVo subItemCategoryFileVo);


    @GetMapping("/subItemCategoryFile/list/by/esFlag")
    Result listByEsFlag();

    @RequestMapping(value = "/subItemCategoryFile/task/write/es/file/content",method = RequestMethod.POST)
    Result taskWriteEsFileContent(@RequestBody SubItemCategoryFileVo subItemCategoryFileVo);

}

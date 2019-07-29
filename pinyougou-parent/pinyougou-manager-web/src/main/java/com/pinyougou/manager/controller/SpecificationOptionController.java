package com.pinyougou.manager.controller;
import java.io.IOException;
import java.util.List;

import com.pinyougou.common.util.POIUtils;
import com.pinyougou.pojo.TbSpecification;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationOptionService;

import com.github.pagehelper.PageInfo;
import entity.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/specificationOption")
public class SpecificationOptionController {

	@Reference
	private SpecificationOptionService specificationOptionService;




	/**
	 * Excel文件上传，并解析文件内容保存到数据库
	 * @param optionFile
	 * @return
	 */
	@RequestMapping("/uploadSpecificationOption")
	public Result upload(MultipartFile optionFile){
		try {
			//读取Excel文件数据
			List<String[]> list = POIUtils.readExcel(optionFile);
			if(list != null && list.size() > 0){

				for (String[] strings : list) {
					TbSpecificationOption specification = new TbSpecificationOption((strings[0]),Long.parseLong(strings[1]),Integer.parseInt(strings[2]));
					specificationOptionService.add(specification);

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			return new Result(false,"失败");
		}
		return new Result(true,"成功");
	}



	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSpecificationOption> findAll(){			
		return specificationOptionService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbSpecificationOption> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return specificationOptionService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param specificationOption
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSpecificationOption specificationOption){
		try {
			specificationOptionService.add(specificationOption);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param specificationOption
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSpecificationOption specificationOption){
		try {
			specificationOptionService.update(specificationOption);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbSpecificationOption findOne(@PathVariable(value = "id") Long id){
		return specificationOptionService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			specificationOptionService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbSpecificationOption> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbSpecificationOption specificationOption) {
        return specificationOptionService.findPage(pageNo, pageSize, specificationOption);
    }
	
}

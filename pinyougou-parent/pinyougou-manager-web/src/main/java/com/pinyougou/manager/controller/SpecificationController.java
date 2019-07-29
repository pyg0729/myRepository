package com.pinyougou.manager.controller;
import java.io.IOException;
import java.util.List;

import com.pinyougou.common.util.POIUtils;
import com.pinyougou.pojo.TbBrand;
import entity.Specification;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;

import com.github.pagehelper.PageInfo;
import entity.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

	@Reference
	private SpecificationService specificationService;



	/**
	 * Excel文件上传，并解析文件内容保存到数据库
	 * @param file
	 * @return
	 */
	@RequestMapping("/uploadSpecification")
	public Result upload(MultipartFile file){
		try {
			//读取Excel文件数据
			List<String[]> list = POIUtils.readExcel(file);
			if(list != null && list.size() > 0){

				for (String[] strings : list) {
					TbSpecification specification = new TbSpecification((strings[0]),(strings[1]));
					specificationService.add(specification);

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
	public List<TbSpecification> findAll(){			
		return specificationService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbSpecification> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return specificationService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param specification
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Specification specification){
		try {
			specificationService.add(specification);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param specification
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSpecification specification){
		try {
			specificationService.update(specification);
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
	public Specification findOne(@PathVariable(value = "id") Long id){
		return specificationService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			specificationService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	
	

	@RequestMapping("/search")
    public PageInfo<TbSpecification> findPage(
    		@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
            @RequestBody TbSpecification specification) {
        return specificationService.findPage(pageNo, pageSize, specification);
    }

	/**
	 * 修改规格状态
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(@RequestParam(value = "status") String status,
							   @RequestParam(value = "Id") String Id) {
		try {
			specificationService.updateStatus(status,Long.valueOf(Id));
			return new Result(true,"更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"更新失败");
		}
	}
	
}

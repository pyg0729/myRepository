package com.pinyougou.manager.controller;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.UserService;

import com.github.pagehelper.PageInfo;
import entity.Result;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;



	@RequestMapping(value = "/exportUser")
	public Result exportUser(HttpServletRequest request, HttpServletResponse response){
		try {
			List<TbUser>users=userService.findAll();
			// 生成excel，查找模板excel的位置
			// poi报表的核心api：XSSFWorkBook、XSSFSheet、XSSFRow、XSSFCell
			String xmlFile = request.getSession().getServletContext().getRealPath("template")+ File.separator+"user_template.xlsx";
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(new File(xmlFile)));

			// 获取Sheet（第一个工作表）
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

			for (int i = 0; i <users.size() ; i++) {
				TbUser user=users.get(i);
				XSSFRow row=sheet.createRow(i+1);
				row.createCell(0).setCellValue(user.getId());
				row.createCell(1).setCellValue(user.getUsername());
				row.createCell(2).setCellValue(user.getPassword());
				row.createCell(3).setCellValue(user.getPhone());
				row.createCell(4).setCellValue(user.getEmail());
				row.createCell(5).setCellValue(user.getCreated());
				row.createCell(6).setCellValue(user.getUpdated());
				row.createCell(7).setCellValue(user.getSourceType());
				row.createCell(8).setCellValue(user.getNickName());
				row.createCell(9).setCellValue(user.getName());
				row.createCell(10).setCellValue(user.getStatus());
				row.createCell(11).setCellValue(user.getHeadPic());
				row.createCell(12).setCellValue(user.getQq());
				if (user.getAccountBalance()==null){
					row.createCell(13).setCellValue(" ");
				}else {
					row.createCell(13).setCellValue(user.getAccountBalance());
				}
				row.createCell(14).setCellValue(user.getIsMobileCheck());
				row.createCell(15).setCellValue(user.getIsEmailCheck());
				row.createCell(16).setCellValue(user.getSex());
				if(user.getUserLevel()==null){
					row.createCell(17).setCellValue(" ");
				}else {
					row.createCell(17).setCellValue(user.getUserLevel());
				}
				if(user.getPoints()==null){
					row.createCell(18).setCellValue(" ");
				}else {

					row.createCell(18).setCellValue(user.getPoints());
				}
				if(user.getExperienceValue()==null){
					row.createCell(19).setCellValue(" ");
				}else {
					row.createCell(19).setCellValue(user.getExperienceValue());
				}
				if(user.getBirthday()==null){
					row.createCell(20).setCellValue(" ");
				}else {
					row.createCell(20).setCellValue(user.getBirthday());
				}
				if(user.getLastLoginTime()==null){
					row.createCell(21).setCellValue(" ");
				}else {
					row.createCell(21).setCellValue(user.getLastLoginTime());
				}

			}
			// 通过输出流输出
			ServletOutputStream outputStream = response.getOutputStream();
			// 设置响应的内容类型
			response.setContentType("application/vnd.ms-excel");
			// 设置以附件的形式下载（默认是内连inline，相当于在浏览器中直接打开）
			response.setHeader("content-Disposition","attachment;filename=user_template.xlsx");

			// 将当前的workbook写到输出流中
			xssfWorkbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
			xssfWorkbook.close();
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


	}
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user){
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	public TbUser findOne(@PathVariable(value = "id") Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }
	
}

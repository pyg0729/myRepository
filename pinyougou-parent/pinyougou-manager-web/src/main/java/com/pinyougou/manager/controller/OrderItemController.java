package com.pinyougou.manager.controller;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.pinyougou.pojo.TbUser;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.sellergoods.service.OrderItemService;

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
@RequestMapping("/orderItem")
public class OrderItemController {

	@Reference
	private OrderItemService orderItemService;


	@RequestMapping(value = "/exportOrderItem")
	public Result exportUser(HttpServletRequest request, HttpServletResponse response){
		try {
			List<TbOrderItem>orderItems=orderItemService.findAll();
			// 生成excel，查找模板excel的位置
			// poi报表的核心api：XSSFWorkBook、XSSFSheet、XSSFRow、XSSFCell
			String xmlFile = request.getSession().getServletContext().getRealPath("template")+ File.separator+"order_template.xlsx";
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(new File(xmlFile)));

			// 获取Sheet（第一个工作表）
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

			for (int i = 0; i <orderItems.size() ; i++) {
				TbOrderItem tbOrderItem=orderItems.get(i);
				XSSFRow row=sheet.createRow(i+1);
				row.createCell(0).setCellValue(tbOrderItem.getId());
				row.createCell(1).setCellValue(tbOrderItem.getItemId());
				row.createCell(2).setCellValue(tbOrderItem.getGoodsId());
				row.createCell(3).setCellValue(tbOrderItem.getOrderId());
				row.createCell(4).setCellValue(tbOrderItem.getTitle());
				row.createCell(5).setCellValue(tbOrderItem.getPrice());
				row.createCell(6).setCellValue(tbOrderItem.getNum());
				row.createCell(7).setCellValue(tbOrderItem.getTotalFee());
				row.createCell(8).setCellValue(tbOrderItem.getTotalFee());
				row.createCell(9).setCellValue(tbOrderItem.getPicPath());
				row.createCell(10).setCellValue(tbOrderItem.getSellerId());


			}
			// 通过输出流输出
			ServletOutputStream outputStream = response.getOutputStream();
			// 设置响应的内容类型
			response.setContentType("application/vnd.ms-excel");
			// 设置以附件的形式下载（默认是内连inline，相当于在浏览器中直接打开）
			response.setHeader("content-Disposition","attachment;filename=order_template.xlsx");

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
	public List<TbOrderItem> findAll(){			
		return orderItemService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbOrderItem> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return orderItemService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param orderItem
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrderItem orderItem){
		try {
			orderItemService.add(orderItem);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param orderItem
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrderItem orderItem){
		try {
			orderItemService.update(orderItem);
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
	public TbOrderItem findOne(@PathVariable(value = "id") Long id){
		return orderItemService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			orderItemService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbOrderItem> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbOrderItem orderItem) {
        return orderItemService.findPage(pageNo, pageSize, orderItem);
    }
	
}

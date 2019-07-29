package com.pinyougou.manager.controller;



import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemSearchService itemSearchService;

	@Reference
	private ItemPageService pageService;


	@RequestMapping(value = "/exportGoods")
	public Result exportUser(HttpServletRequest request, HttpServletResponse response){
		try {
			List<TbGoods>goodsList=goodsService.findAll();
			// 生成excel，查找模板excel的位置
			// poi报表的核心api：XSSFWorkBook、XSSFSheet、XSSFRow、XSSFCell
			String xmlFile = request.getSession().getServletContext().getRealPath("template")+ File.separator+"goods_template.xlsx";
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(new File(xmlFile)));

			// 获取Sheet（第一个工作表）
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

			for (int i = 0; i <goodsList.size() ; i++) {
				TbGoods tbGoods=goodsList.get(i);
				XSSFRow row=sheet.createRow(i+1);
				row.createCell(0).setCellValue(tbGoods.getId());
				row.createCell(1).setCellValue(tbGoods.getSellerId());
				row.createCell(2).setCellValue(tbGoods.getGoodsName());
				if(tbGoods.getDefaultItemId()==null){
					row.createCell(3).setCellValue(" ");
				}else {
					row.createCell(3).setCellValue(tbGoods.getDefaultItemId());
				}
				row.createCell(4).setCellValue(tbGoods.getAuditStatus());
				if(tbGoods.getIsMarketable()==null){
					row.createCell(5).setCellValue(" ");
				}else {
					row.createCell(5).setCellValue(tbGoods.getIsMarketable());
				}

				if(tbGoods.getBrandId()==null){
					row.createCell(6).setCellValue(" ");
				}else {
					row.createCell(6).setCellValue(tbGoods.getBrandId());
				}

				row.createCell(7).setCellValue(tbGoods.getCaption());
				if(tbGoods.getCategory1Id()==null){
					row.createCell(8).setCellValue(" ");
				}else {
					row.createCell(8).setCellValue(tbGoods.getCategory1Id());
				}
				if(tbGoods.getCategory2Id()==null){
					row.createCell(9).setCellValue(" ");
				}else {
					row.createCell(9).setCellValue(tbGoods.getCategory2Id());
				}
				if(tbGoods.getCategory3Id()==null){
					row.createCell(10).setCellValue(" ");
				}else {
					row.createCell(10).setCellValue(tbGoods.getCategory3Id());
				}


				if(tbGoods.getSmallPic()==null){
					row.createCell(11).setCellValue(" ");
				}else {
					row.createCell(11).setCellValue(tbGoods.getSmallPic());
				}
				if(tbGoods.getPrice()==null){
					row.createCell(12).setCellValue(" ");
				}else {
					row.createCell(12).setCellValue(tbGoods.getPrice());
				}

				if(tbGoods.getTypeTemplateId()==null){
					row.createCell(13).setCellValue(" ");
				}else {
					row.createCell(13).setCellValue(tbGoods.getTypeTemplateId());
				}

				if(tbGoods.getIsEnableSpec()==null){
					row.createCell(14).setCellValue(" ");
				}else {
					row.createCell(14).setCellValue(tbGoods.getIsEnableSpec());
				}
				row.createCell(15).setCellValue(tbGoods.getIsDelete());


			}
			// 通过输出流输出
			ServletOutputStream outputStream = response.getOutputStream();
			// 设置响应的内容类型
			response.setContentType("application/vnd.ms-excel");
			// 设置以附件的形式下载（默认是内连inline，相当于在浏览器中直接打开）
			response.setHeader("content-Disposition","attachment;filename=goods_template.xlsx");

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
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
									  @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取商家ID
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
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
	public Goods findOne(@PathVariable(value = "id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			goodsService.delete(ids);


			//发送消息

			//消息分装到pojo
			MessageInfo info = new MessageInfo("Goods_Topic", "goods_update_tag", "updateStatus", ids, MessageInfo.METHOD_DELETE);
			//将数据做为消息体 发送mq服务器上
			Message message = new Message(info.getTopic(), info.getTags(), info.getKeys(), JSON.toJSONString(info).getBytes());
			SendResult send = producer.send(message);

			//itemSearchService.deleteByIds(ids);


			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {
        return goodsService.findPage(pageNo, pageSize, goods);
    }


    @Autowired
	private DefaultMQProducer producer;

    @RequestMapping("/updateStatus/{status}")
	public Result updateStatus (@RequestBody Long ids[],@PathVariable("status") String status) {
		try {
			goodsService.updateStatus(ids,status);

			if ("1".equals(status)) {

				//发送消息


				//1.根据审核的SPU的ID 获取SKU的列表数据
				List<TbItem> tbItemList = goodsService.findTbItemListByIds(ids);
				//消息分装到pojo
				MessageInfo info = new MessageInfo("Goods_Topic", "goods_update_tag", "updateStatus", tbItemList, MessageInfo.METHOD_UPDATE);
				//将数据做为消息体 发送mq服务器上
				Message message = new Message(info.getTopic(), info.getTags(), info.getKeys(), JSON.toJSONString(info).getBytes());
				SendResult send = producer.send(message);



				/*//2.调用搜索服务的方法 （传递SKU的列表数据过去）内部执行更新的动作。
				itemSearchService.updataIndex(tbItemList);


				//3.生成静态页面
				//调用静态化服务的方法 根据 商品的ID(spu的ID) 直接从数据库查询 并生成静态页面（服务方法的内部的）

				for (Long id : ids) {
					pageService.genItemHtml(id);
				}*/
			}


			return new Result(true,"操作成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"操作失败!");
		}
	}
	
}

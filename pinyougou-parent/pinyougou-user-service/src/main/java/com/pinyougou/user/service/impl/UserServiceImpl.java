package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.impl.CoreServiceImpl;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser>  implements UserService {

	
	private TbUserMapper userMapper;

	@Autowired
	public UserServiceImpl(TbUserMapper userMapper) {
		super(userMapper, TbUser.class);
		this.userMapper=userMapper;
	}

	
	

	
	@Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if(user!=null){			
						if(StringUtils.isNotBlank(user.getUsername())){
				criteria.andLike("username","%"+user.getUsername()+"%");
				//criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(StringUtils.isNotBlank(user.getPassword())){
				criteria.andLike("password","%"+user.getPassword()+"%");
				//criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(StringUtils.isNotBlank(user.getPhone())){
				criteria.andLike("phone","%"+user.getPhone()+"%");
				//criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(StringUtils.isNotBlank(user.getEmail())){
				criteria.andLike("email","%"+user.getEmail()+"%");
				//criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(StringUtils.isNotBlank(user.getSourceType())){
				criteria.andLike("sourceType","%"+user.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(user.getNickName())){
				criteria.andLike("nickName","%"+user.getNickName()+"%");
				//criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(StringUtils.isNotBlank(user.getName())){
				criteria.andLike("name","%"+user.getName()+"%");
				//criteria.andNameLike("%"+user.getName()+"%");
			}
			if(StringUtils.isNotBlank(user.getStatus())){
				criteria.andLike("status","%"+user.getStatus()+"%");
				//criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(user.getHeadPic())){
				criteria.andLike("headPic","%"+user.getHeadPic()+"%");
				//criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(StringUtils.isNotBlank(user.getQq())){
				criteria.andLike("qq","%"+user.getQq()+"%");
				//criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsMobileCheck())){
				criteria.andLike("isMobileCheck","%"+user.getIsMobileCheck()+"%");
				//criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsEmailCheck())){
				criteria.andLike("isEmailCheck","%"+user.getIsEmailCheck()+"%");
				//criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getSex())){
				criteria.andLike("sex","%"+user.getSex()+"%");
				//criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	/**
	 * 校验验证码是否正确
	 * @param phone 手机号
	 * @param smscode 验证码
	 * @return
	 */
	@Override
	public boolean checkSmsCode(String phone, String smscode) {

		//如果为空就直接返回false
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(smscode)) {
			return false;
		}

		//从Redis中获取 到值
		String code = (String) redisTemplate.boundValueOps("ZHUCE_" + phone).get();
		//判断验证码是否正确
		if (!smscode.equals(code)) {
			return false;
		}
		return true;
	}




	@Autowired
    private RedisTemplate redisTemplate;

	@Autowired
	private DefaultMQProducer producer;

	@Value("${sign_name}")
	private String sign_name;

	@Value("${template_code}")
	private String template_code;


	/**
	 * 生成验证码
	 * @param phone  手机号  根据手机号放入Redis中
	 */
	@Override
	public void createSmsCode(String phone) {
		//生成6位数字
		// (Math.random() * 9 + 1) <10
		// 100000 *10
		String code = (long)((Math.random() * 9 + 1)*100000)+"";
		//存储到Redis 中  集成Redis  依赖   配置文件   注入

		redisTemplate.boundValueOps("ZHUCE_"+phone).set(code);//key 手机号   value  验证码
		redisTemplate.boundValueOps("ZHUCE_"+phone).expire(30L, TimeUnit.MINUTES);

		//组装消息对象  手机号   签名   模板  验证码
		Map<String, String> map = new HashMap<>();
		map.put("mobile",phone);
		map.put("sign_name",sign_name);
		map.put("template_code",template_code);
		map.put("param","{\"code\":\""+code+"\"}");

		try {
			//发送消息  依赖  配置文件   注入
			Message message = new Message("SMS_TOPIC",
					"SEND_MESSAGE_TAG",
					"createSmsCode",
					JSON.toJSONString(map).getBytes());
			producer.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}






	/**
	 *
	 * @param record
	 */
	@Override
	public void add(TbUser record) {
		//使用MD5进行密码加密
		String password = record.getPassword();
		String passwordencode = DigestUtils.md5DigestAsHex(password.getBytes());
		record.setPassword(passwordencode);

		//补充必要字段
		record.setCreated(new Date());
		record.setUpdated(record.getCreated());

		//添加数据到数据库中
		userMapper.insert(record);
	}
}

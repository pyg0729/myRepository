package com.pinyougou.shop.security;
/*
 *
 *    @苑帅
 *    @时间为：2019-06-27-09-58
 *
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //从数据库获取用户信息
        //根据页面传入用户名查询用户对象
        TbSeller one = sellerService.findOne(username);
        //判断用户是否存在
        if (one == null) {
            return null;
        }
        //判断用户是否 已审核通过
        if (!"1".equals(one.getStatus())) {
            return null;
        }
        //如果用户存在获取密码 进行区配
        String password = one.getPassword();

        //3.给用户设置权限
        /*List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));//授权角色
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));//授权角色*/

        //2.交给springsecurity框架 自动的匹配

        return new User(username,password, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}

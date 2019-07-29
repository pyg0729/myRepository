package com.pinyougou.cart.service;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-14-10-07
 *
 */

import entity.Cart;

import java.util.List;

public interface CartService {

    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    List<Cart> getCartListFromRedis(String name);

    void saveToRedis(String name, List<Cart> newestList);

    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisList);
}

package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Override
    public AddSuccessVo addToCart(Long skuId, Integer num) {
        AddSuccessVo successVo = new AddSuccessVo();

        //1、决定使用哪个购物车
        String cartKey = determinCartKey();
        //2、添加；原来购物车有没有这个商品，如果没有就是新增，有就是数量叠加
        //3、尝试从购物车中获取到这个商品

        CartInfo item = getCartItem(cartKey, skuId);
        if (item == null) {
            CartInfo info = getCartInfoFromRpc(skuId);
            info.setSkuNum(num);
            saveItemToCart(info, cartKey);

            successVo.setSkuDefaultImg(info.getImgUrl());
            successVo.setSkuName(info.getSkuName());
            successVo.setId(info.getSkuId());
        } else {
            item.setSkuNum(item.getSkuNum() + num);
            saveItemToCart(item, cartKey);

            successVo.setSkuDefaultImg(item.getImgUrl());
            successVo.setSkuName(item.getSkuName());
            successVo.setId(item.getSkuId());
        }

        //设置过期时间；
        setTempCartExpire();
        return successVo;
    }

    @Override
    public String determinCartKey() {
        //1、拿到用户信息
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        String cartKey = "";
        if (userAuth.getUserId() != null) {
            //用户登录了
            cartKey = RedisConst.CART_INFO_PREFIX + userAuth.getUserId();
        } else {
            //如果没有临时id。前端会传过来
            cartKey = RedisConst.CART_INFO_PREFIX + userAuth.getTempId();
        }
        return cartKey;
    }

    @Override
    public CartInfo getCartItem(String cartKey, Long skuId) {
        //1、拿到一个能操作hash的对象
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        //2、获取cartKey购物车指定的skuId商品
        String json = ops.get(cartKey, skuId.toString());
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        CartInfo info = JSONs.toObj(json, CartInfo.class);
        return info;
    }

    @Override
    public void saveItemToCart(CartInfo item, String cartKey) {
        //1、拿到一个能操作hash的对象
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Long skuId = item.getSkuId();
        ops.put(cartKey, skuId.toString(), JSONs.toStr(item));

    }

    @Override
    public CartInfo getCartInfoFromRpc(Long skuId) {
        Result<CartInfo> info = skuFeignClient.getCartInfoBySkuId(skuId);
        CartInfo data = info.getData();
        return data;
    }

    @Override
    public List<CartInfo> getCartAllItem() {
        //0、是否需要合并： 只要tempId对应的购物车有东西，并且还有userId；合并操作
        UserAuth auth = AuthContextHolder.getUserAuth();
        if (auth.getUserId() != null && !StringUtils.isEmpty(auth.getTempId())) {
            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + auth.getTempId());
            if (hasKey) {
                List<CartInfo> cartInfos = getCartAllItem(RedisConst.CART_INFO_PREFIX + auth.getTempId());
                cartInfos.forEach(tempItem -> {
                    addToCart(tempItem.getSkuId(), tempItem.getSkuNum());
                });
                redisTemplate.delete(RedisConst.CART_INFO_PREFIX + auth.getTempId());
            }
        }

        String cartKey = determinCartKey();
        return getCartAllItem(cartKey);
//        List<CartInfo> allItem = getCartAllItem(cartKey);
//        RequestAttributes oldRequest = RequestContextHolder.getRequestAttributes();

    }

    @Override
    public List<CartInfo> getCartAllItem(String cartKey) {

        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        List<CartInfo> collect = ops.values(cartKey)
                .stream()
                .map(jsonStr -> JSONs.toObj(jsonStr, CartInfo.class))
                .sorted((pre, next) -> (int) (next.getCreateTime().getTime() - pre.getCreateTime().getTime()))
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public void updateCartItemStatus(Long skuId, Integer status) {

        String cartKey = determinCartKey();
        CartInfo cartItem = getCartItem(cartKey, skuId);
        cartItem.setIsChecked(status);

        //同步redis
        saveItemToCart(cartItem, cartKey);
    }

    @Override
    public void deleteChecked() {
        String cartKey = determinCartKey();
        List<CartInfo> cartInfos = getAllCheckedItem(cartKey);

        Object[] ids = cartInfos
                .stream()
                .map(info -> info.getSkuId().toString())
                .toArray();

        //2、删除他们
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        ops.delete(cartKey,ids);
    }

    @Override
    public void deleteCartItem(Long skuId) {
        String cartKey = determinCartKey();
        //Long(序列化) == String(序列化)
        redisTemplate.opsForHash().delete(cartKey, skuId.toString());
    }

    @Override
    public void setTempCartExpire() {
        UserAuth auth = AuthContextHolder.getUserAuth();

        //用户只操作临时购物车
        if (!StringUtils.isEmpty(auth.getTempId()) && auth.getUserId() == null) {
            //用户带了临时token；
            //1、如果有临时购物车就设置过期时间。
            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + auth.getTempId());
            if(hasKey){
                //临时购物车有一年的时间。
                redisTemplate.expire(RedisConst.CART_INFO_PREFIX + auth.getTempId(),365, TimeUnit.DAYS);
            }
        }
    }

    @Override
    public List<CartInfo> getAllCheckedItem(String cartKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        List<CartInfo> cartInfos = ops.values(cartKey)
                .stream()
                .map(jsonStr -> JSONs.toObj(jsonStr, CartInfo.class))
                .filter(info -> info.getIsChecked() == 1)
                .collect(Collectors.toList());
        return cartInfos;
    }
}

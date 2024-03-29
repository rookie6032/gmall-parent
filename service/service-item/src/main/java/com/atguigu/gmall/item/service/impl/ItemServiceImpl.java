package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class ItemServiceImpl implements ItemService {


    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    CacheService cacheService;


    /**
     *
     * key：支持表达式动态计算； 使用SpEL功能，甚至于表达式内的java代码都能运行出来。
     * #{} 代表需要动态取值
     * 1)、#params：取出当前方法的所有参数中的某个值;   #params[n]： 取出第n+1个参数
     * 2)、#currentDate：获取当天日期
     * 3)、#redis：从redis中读取
     *
     * 示例：
     *   sku:info:#{#params[0]}    计算得到： sku:info:47
     *   hello:xx:#{#redis.num}    计算得到： hello:xx:1
     *   categorys                 计算得到： categorys
     *
     *
     *
     * @param skuId
     * @return
     */
    @Cache(
            key = RedisConst.SKU_INFO_CACHE_KEY_PREFIX+"#{#params[0]}",
            bloomName = RedisConst.SKU_BLOOM_FILTER_NAME,
            bloomIf = "#{#params[0]}",
            ttl = RedisConst.SKU_INFO_CACHE_TIMEOUT
    )  //提升通用性。那些问题？  //sku:info:skuid的值
    @Override
    public SkuDetailVo getItemDetail(Long skuId) {

        return getItemDetailFromRpc(skuId);
    }






    /**
     * 事务：
     *   1、获取链接
     *   2、设置非自动提交
     *   3、准备“sql”、设置参数【service的目标方法】
     *   4、执行sql
     *   5、返回结果
     *   6、提交/回滚
     *   7、关闭链接
     *    @Transactional： 利用AOP
     *     1)、目标方法执行之前： 1、2
     *     2)、目标方法执行
     *     3)、目标方法执行之后-正常结束： 5，6提交
     *     4)、目标方法执行之后-异常结束： 5，6回滚
     *     5)、目标方法无论怎样结束：      7关闭链接
     *
     *     前置通知、后置通知、返回通知、异常通知
     *     try{
     *         //1、前置通知
     *         业务代码  service.Hello();
     *         //4、返回通知
     *     }catch(Exception e){
     *         //3、异常通知
     *     }finally{
     *         //2、后置通知
     *     }
     *
     *
     * 缓存：
     *   1、先查缓存
     *   2、缓存没有，先问布隆
     *   3、布隆说有，获取分布式锁
     *   4、【执行业务回源查询】
     *   5、查到的数据放缓存
     *   6、解锁
     * AOP编写一个切面来实现功能+自定义注解@Cache
     *
     *
     * @param skuId
     * @return
     */

    public SkuDetailVo getItemDetailRedissonLockBloom(Long skuId) {
        String cacheKey = RedisConst.SKU_INFO_CACHE_KEY_PREFIX + skuId;
        //1、先查缓存。
        SkuDetailVo data = cacheService.getData(cacheKey, SkuDetailVo.class);
        //2、缓存中是否存在
        if(data == null){
            log.info("sku:{} 详情- 缓存不命中，准备回源..正在检索布隆是否存在这个商品");
            RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
            if (!filter.contains(skuId)) { //布隆说没有就一定没有
                log.info("sku:{} 布隆觉得没有，请求无法穿透...");
                return null;
            }
            //2.1、 缓存中没有。准备回源。
            //2.2、 加分布式锁  lock:sku:info:49
            RLock lock = redissonClient.getLock(RedisConst.SKU_INFO_LOCK_PREFIX + skuId);
            //2.3、加锁
            boolean tryLock = lock.tryLock();
            //2.4、获得锁
            if(tryLock){
                //2.7、准备回源
                SkuDetailVo detail = getItemDetailFromRpc(skuId);
                //2.8、缓存一份
                cacheService.saveData(cacheKey,detail,RedisConst.SKU_INFO_CACHE_TIMEOUT,TimeUnit.MILLISECONDS);
                lock.unlock();
                return detail;
            }else {
                //2.5、没获得锁
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    //2.6、直接查缓存即可
//                    return  getItemDetail(skuId); //递归调法
                    return cacheService.getData(cacheKey,SkuDetailVo.class);
                } catch (InterruptedException e) {

                }
            }
        }

        //3、缓存中有这个数据，直接返回
        return data;
    }

    //回源查数据的方法
    @SneakyThrows
    public SkuDetailVo getItemDetailFromRpc(Long skuId) {
        SkuDetailVo vo = new SkuDetailVo();

        //2、sku的info
        CompletableFuture<SkuInfo> baseInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> skuInfo = skuFeignClient.getSkuInfo(skuId);
            SkuInfo info = skuInfo.getData();
            vo.setSkuInfo(info);
            return info;
        });




        //1、sku所在的分类。
        CompletableFuture<Void> categoryFuture=baseInfoFuture.thenAcceptAsync(info -> {
            Long category3Id = info.getCategory3Id();
            //按照三级分类id查出所在的完整分类信息
            Result<CategoryView> categoryView = skuFeignClient.getCategoryView(category3Id);
            vo.setCategoryView(categoryView.getData());
        });




        //3、sku的价格
        CompletableFuture<Void> priceFuture=baseInfoFuture.thenAcceptAsync(info -> {
            vo.setPrice(info.getPrice());
        });


        //4、sku的销售属性列表
        CompletableFuture<Void> saleAttrFuture=baseInfoFuture.thenAcceptAsync(info -> {
            Long spuId = info.getSpuId();
            Result<List<SpuSaleAttr>> saleAttr = skuFeignClient.getSaleAttr(skuId, spuId);
            if (saleAttr.isOk()) {
                vo.setSpuSaleAttrList(saleAttr.getData());
            }
        });



        //5、得到一个sku对应的spu的所有sku的组合关系
        CompletableFuture<Void> skuOtherFuture=baseInfoFuture.thenAcceptAsync(info -> {
            Result<String> value = skuFeignClient.getSpudeAllSkuSaleAttrAndValue(info.getSpuId());
            vo.setValuesSkuJson(value.getData());
        });
        //6、编排 - 等所有任务运行完成
        CompletableFuture.allOf(categoryFuture,priceFuture,saleAttrFuture,skuOtherFuture)
                .get(); //阻塞等待所有人完成

        return vo;
    }


}

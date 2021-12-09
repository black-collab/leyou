package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mappers.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 查询spu商品信息
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        //用分页工具的话，返回的list实际是pageList，里面存有分页信息，会执行一条count的sql
        //用pageInfo类接受这个List会给他转成PageList来读取分页信息，比如total总条数等
        PageHelper.startPage(page,rows);
        //根据搜索栏key和是否上架saleable查询sup
        List<Spu> spuList =  this.spuMapper.querySpuByKey(key,saleable);
        //到这一步查询已经结束了，下面是查询spu中cid对应的分类名
        //通过steam流遍历所有spu保存到信的spuBo中存到新的list集合
        List<SpuBo> spuBoList = spuList.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            //复制bean中的属性到另一个bean，相同属性才能被复制
            BeanUtils.copyProperties(spu, spuBo);
            //把查询到的spu中的cid1、2、3分别去查询对应的分类名
            List<String> cIds = this.categoryMapper.queryCategoryByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())
            );
            //拼接3个分类名
            spuBo.setCname(StringUtils.join(cIds, "-"));
            //查询spu所属的brand品牌名
            Brand brand = this.brandMapper.findBrandById(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //返回覆盖原有数据
            return spuBo;
            //把所有数据转存到另一个集合
        }).collect(Collectors.toList());//这里会把pageList转换成arraylist，注意
        //因为spuBoList实际上是ArrayList，没有分页信息了，所以pageInfo要接受转换钱的spuList
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spuList);
        return new PageResult<>(spuPageInfo.getTotal(),spuPageInfo.getPages(),spuBoList);
    }

    /**
     * 添加spu和和对应的sku
     * @param spuBo
     */
    @Transactional
    public void saveSpu(SpuBo spuBo) {
        //添加spu
        //添加创建时间和修改时间
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.saveSpu(spuBo);
        //添加spuDetail
        //获取当前线程的connection数据库连接最后添加的数据的ID
        spuBo.setId(brandMapper.findLastInsertId());
        //给spuDetail设置spuId
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        this.spuDetailMapper.saveSpuDetail(spuBo.getSpuDetail());
        spuBo.getSkus().forEach(sku -> {
            //添加sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.saveSku(sku);
            //获取当前线程的connection数据库连接最后添加的数据的ID并设置
            sku.setId(brandMapper.findLastInsertId());
            //添加库存stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.saveStock(stock);
        });
        //发送spuId给rabbitMQ，没有指定交换机名称，发送给yml里面配置的默认交换机,try是为了保证发送消息失败不影响原有方法执行
        this.sendMessage(spuBo.getId(),"leyou.item.insert");
    }

    /**
     * 发送spuId到指定的routingKey和交换机
     * @param spuId
     * @param routingKey
     */
    public void sendMessage(Long spuId,String routingKey) {
        try {
            this.amqpTemplate.convertAndSend(routingKey,spuId);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    public SpuDetail querySpuDetailBySpuId(Long sid) {
        return this.spuDetailMapper.querySpuDetailBySpuId(sid);
    }

    /**
     * 通过spuId查询sku
     * @param sid
     * @return
     */
    public List<Sku> querySkuBySpuId(Long sid) {
        return this.skuMapper.querySkuBySpuId(sid);
    }

    /**
     * 修改spu和sku
     * @param spuBo
     */
    @Transactional
    public void updateSpu(SpuBo spuBo) {
        //创建修改时间
        Date lastUpdateTime = new Date();
        //根据spuId删除sku和stock库存，不修改，直接删除再添加
        //查询spu对应的sku集合
        List<Sku> skus = this.skuMapper.querySkuBySpuId(spuBo.getId());
        //获取sku集合的id集合
        List<Long> skuIds = skus.stream().map(Sku::getId).collect(Collectors.toList());
        //通过skuId集合删除sku和stock
        this.stockMapper.deleteStockBySkuIds(skuIds);
        this.skuMapper.deleteSkuBySpuId(spuBo.getId());
        //添加新的sku和stock库存
        //前端传来的sku没有spuId，给他设置spuId
        spuBo.getSkus().forEach(sku -> {
            //设置sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(lastUpdateTime);
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.saveSku(sku);
            //获取刚刚添加的skuId
            Long skuId = this.brandMapper.findLastInsertId();
            //设置stock
            Stock stock = new Stock();
            stock.setSkuId(skuId);
            stock.setStock(sku.getStock());
            this.stockMapper.saveStock(stock);
        });
        //修改spu和spuDetail详情
        spuBo.setLastUpdateTime(lastUpdateTime);
        this.spuMapper.updateSpu(spuBo);
        this.spuDetailMapper.updateSpuDetail(spuBo.getSpuDetail());
        //发送消息到MQ
        this.sendMessage(spuBo.getId(),"leyou.item.update");
    }

    public Spu queryOneSpuById(Long id) {
        return this.spuMapper.queryOneSpuById(id);
    }
}

package cn.itcast.core.service;

import cn.itcast.core.common.Constants;
import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService{

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


//    @Override
////    public Map<String, Object> search(Map paramMap) {
////        //1. 从paramMap参数对象中获取查询关键字
////        String keywords = String.valueOf(paramMap.get("keywords"));
////        //获取当前页
////        Integer pageNo = Integer.parseInt(String.valueOf(paramMap.get("pageNo")));
////        //获取每页查询条数
////        Integer pageSize = Integer.parseInt(String.valueOf(paramMap.get("pageSize")));
////        //2. 创建查询对象
////        Query query = new SimpleQuery();
////        //3. 创建条件对象, 根据关键字查询
////        /**
////         * contains和is方法区别?
////         * contains包含: 查询的时候有些类似于使用sql语句中的like关键字, 进行模糊查询, 但是关键字不进行切分词
////         * is精确查询: 将查询的关键字使用分词器进行切分词, 将切分好的词再去一个一个查询索引.
////         */
////        Criteria criteria = new Criteria("item_keywords").is(keywords);
////        query.addCriteria(criteria);
////        //4. 设置从第几条开始查询
////        if (pageNo == null || pageNo <= 0) {
////            pageNo = 1;
////        }
////        if (pageSize == null || pageSize <= 0) {
////            pageSize = 40;
////        }
////        Integer start = (pageNo - 1) * pageSize;
////        query.setOffset(start);
////        //5. 设置每页查询多少条数据
////        query.setRows(pageSize);
////        //6. 根据查询对象查询并返回结果
////        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);
////
////        //7. 封装返回的结果对象
////        Map<String, Object> resultMap = new HashMap<>();
////        //查询到的结果集
////        resultMap.put("rows", items.getContent());
////        //查询到的总条数
////        resultMap.put("total", items.getTotalElements());
////        //查询到的总页数
////        resultMap.put("totalPages", items.getTotalPages());
////        return resultMap;
////    }

    @Override
    public Map<String, Object> search(Map paramMap) {
        //获取条件, 这里获取的是分类条件
        String category = String.valueOf(paramMap.get("category"));

        //1. 高亮分页查询
        Map<String, Object> resultMap = hightSearch(paramMap);

        //2. 根据关键字分组查询, 主要是获取分类名称, 分组的目的是去除重复的分类名称
        List<String> categoryNameList = findCategoryNameListFromSolr(paramMap);
        resultMap.put("categoryList", categoryNameList);


        //3. 根据分类名称, 到redis中获取对应的模板id, 根据模板id再到redis中获取对应的品牌集合和规格集合
        if (category == null || "".equals(category)) {
            //如果页面没有选择根据分类查询, 默认根据分类列表中的第一个分类查询
            if (categoryNameList != null && categoryNameList.size() > 0) {
                category = categoryNameList.get(0);
                Map<String, Object> brandListAndSpecListMap = findBrandListAndSpecListByCategoryName(category);
                resultMap.putAll(brandListAndSpecListMap);
            }
        } else {
            //如果页面传入了根据具体的分类查询, 则根据具体分类查询
            Map<String, Object> brandListAndSpecListMap = findBrandListAndSpecListByCategoryName(category);
            resultMap.putAll(brandListAndSpecListMap);
        }

        return resultMap;
    }

    /**
     * 高亮查询
     * 业务: 根据关键字, 分页, 高亮查询
     * @param paramMap  查询条件对象
     */
    public Map<String, Object> hightSearch(Map paramMap) {
        /**
         * 获取查询参数
         */
        //从paramMap参数对象中获取查询关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        if (keywords != null) {
            keywords = keywords.replaceAll(" ", "");
        }
        //获取当前页
        Integer pageNo = Integer.parseInt(String.valueOf(paramMap.get("pageNo")));
        //获取每页查询条数
        Integer pageSize = Integer.parseInt(String.valueOf(paramMap.get("pageSize")));
        //获取分类查询条件
        String category = String.valueOf(paramMap.get("category"));
        //获取品牌查询条件
        String brand = String.valueOf(paramMap.get("brand"));
        //获取传入的规格查询条件
        Map<String, String> specMap= (Map<String, String>)paramMap.get("spec");
        //获取页面传入的价格区间字符串
        String priceStr = String.valueOf(paramMap.get("price"));
        //获取排序字段
        String sortField = String.valueOf(paramMap.get("sortField"));
        //获取排序方式
        String sortType = String.valueOf(paramMap.get("sort"));

        /**
         * 创建查询对象
         */
        HighlightQuery query = new SimpleHighlightQuery();

        /**
         * 创建条件对象, 设置查询条件, 根据关键字查询
         */
        /**
         * contains和is方法区别?
         * contains包含: 查询的时候有些类似于使用sql语句中的like关键字, 进行模糊查询, 但是关键字不进行切分词
         * is精确查询: 将查询的关键字使用分词器进行切分词, 将切分好的词再去一个一个查询索引.
         */
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);

        /**
         * 设置分页
         */
        //设置从第几条开始查询
        if (pageNo == null || pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 40;
        }
        Integer start = (pageNo - 1) * pageSize;
        query.setOffset(start);
        //设置每页查询多少条数据
        query.setRows(pageSize);


        /**
         * 设置高亮
         */
        //创建高亮选项对象
        HighlightOptions options = new HighlightOptions();
        //设置需要高亮显示的域名
        options.addField("item_title");
        //前缀
        options.setSimplePrefix("<em style=\"color:red\">");
        //后缀
        options.setSimplePostfix("</em>");
        //将高亮选项对象放入查询对象中
        query.setHighlightOptions(options);

        /**
         * 过滤查询
         */
        if (brand != null && !"".equals(brand)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria filterCriteria = new Criteria("item_brand").is(brand);
            //将过滤查询条件, 放入过滤查询对象中
            filterQuery.addCriteria(filterCriteria);
            //将过滤查询对象放入查询对象中
            query.addFilterQuery(filterQuery);
        }

        if (category != null && !"".equals(category)) {
            //创建过滤查询对象
            FilterQuery filterQuery = new SimpleFilterQuery();
            //创建条件对象
            Criteria filterCriteria = new Criteria("item_category").is(category);
            //将过滤查询条件, 放入过滤查询对象中
            filterQuery.addCriteria(filterCriteria);
            //将过滤查询对象放入查询对象中
            query.addFilterQuery(filterQuery);
        }

        //根据规格过滤查询
        if (specMap != null && specMap.size() > 0) {
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //创建条件对象
                Criteria filterCriteria = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                //将过滤查询条件, 放入过滤查询对象中
                filterQuery.addCriteria(filterCriteria);
                //将过滤查询对象放入查询对象中
                query.addFilterQuery(filterQuery);
            }
        }

        //按照价格过滤
        if (priceStr != null && !"".equals(priceStr)) {
            String[] split = priceStr.split("-");
            //判断最小值不是0
            if (!"0".equals(split[0])) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //创建条件对象
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
                //将过滤查询条件, 放入过滤查询对象中
                filterQuery.addCriteria(filterCriteria);
                //将过滤查询对象放入查询对象中
                query.addFilterQuery(filterQuery);
            }

            //判断最大值不是*
            if (!"*".equals(split[1])) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                //创建条件对象
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(split[1]);
                //将过滤查询条件, 放入过滤查询对象中
                filterQuery.addCriteria(filterCriteria);
                //将过滤查询对象放入查询对象中
                query.addFilterQuery(filterQuery);
            }
        }

        /**
         * 排序
         */
        if (sortField != null && !"".equals(sortField)) {
            //升序
            if ("ASC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            //降序
            if ("DESC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        /**
         * 根据查询对象查询并返回结果
         */
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        //获取高亮结果集
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();

        List<Item> itemList = new ArrayList<>();
        if (highlighted != null) {
            for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                //获取Item对象, 里面的title标题是不带高亮的原有标题
                Item item = itemHighlightEntry.getEntity();
                //获取带高亮的标题
                List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
                if (highlights != null && highlights.size() > 0) {
                    if (highlights.get(0).getSnipplets() != null && highlights.get(0).getSnipplets().size() > 0) {
                        String hightTitle = highlights.get(0).getSnipplets().get(0);
                        item.setTitle(hightTitle);
                    }
                }
                itemList.add(item);
            }
        }


        //7. 封装返回的结果对象
        Map<String, Object> resultMap = new HashMap<>();
        //查询到的结果集
        resultMap.put("rows", itemList);
        //查询到的总条数
        resultMap.put("total", items.getTotalElements());
        //查询到的总页数
        resultMap.put("totalPages", items.getTotalPages());
        return resultMap;
    }

    /**
     * 根据查询关键字, 分组查询, 主要是获取对应的分类名称, 分组的目的是去重
     * @param paramMap
     * @return
     */
    public List<String> findCategoryNameListFromSolr(Map paramMap) {
        /**
         * 获取查询的关键字
         */
        String keywords = String.valueOf(paramMap.get("keywords"));
        if (keywords != null) {
            keywords = keywords.replaceAll(" ", "");
        }
        /**
         * 创建查询对象
         */
        Query query = new SimpleQuery();

        /**
         * 根据关键字查询
         */
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);

        /**
         * 设置分组
         */
        GroupOptions options = new GroupOptions();
        //设置根据分类域进行分组
        options.addGroupByField("item_category");
        query.setGroupOptions(options);

        /**
         * 查询并返回结果
         */
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获取分类域的结果
        GroupResult<Item> item_category = items.getGroupResult("item_category");

        List<String> categoryList = new ArrayList<>();
        if (item_category != null) {
            Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
            List<GroupEntry<Item>> content = groupEntries.getContent();
            if (content != null && content.size() > 0) {
                for (GroupEntry<Item> itemGroupEntry : content) {
                    String categoryName = itemGroupEntry.getGroupValue();
                    categoryList.add(categoryName);
                }
            }
        }
        return categoryList;
    }

    /**
     * 根据分类名称到redis中查询对应的品牌集合和规格集合数据
     * @param categoryName  分类名称
     * @return
     */
    public Map<String, Object> findBrandListAndSpecListByCategoryName(String categoryName) {
        //1. 根据分类名称到redis中查询对应的模板id
        Long templateId =  (Long)redisTemplate.boundHashOps(Constants.REDIS_CATEGORY).get(categoryName);
        //2. 根据模板id到redis中查询对应的品牌集合数据
        List<Map> brandList = (List<Map>)redisTemplate.boundHashOps(Constants.REDIS_BRADND_LIST).get(templateId);
        //3. 根据模板id到redis中查询对应的规格集合数据
        List<Map> specList = (List<Map>)redisTemplate.boundHashOps(Constants.REDIS_SPEC_LIST).get(templateId);
        //4. 将品牌集合和规格集合数据封装并返回
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("brandList", brandList);
        resultMap.put("specList", specList);
        return resultMap;
    }
}

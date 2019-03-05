package cn.itcast.core.pojo.entity;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义封装规格实体类, 里面包含规格对象, 规格选项集合对象
 */
public class SpecEntity implements Serializable {
    //规格对象
    private Specification specification;
    //规格选项集合对象
    private List<SpecificationOption> specificationOptionList;


    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}

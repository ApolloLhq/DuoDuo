package org.qiunet.function.test.attr.equip;

import org.qiunet.function.attr.tree.IAttrNodeType;

/***
 *
 *
 * @author qiunet
 * 2020-11-20 17:0
 */
public enum EquipAttrNode implements IAttrNodeType {
	EQUIP_ROOT(null, "装备根节点"),
	BASE(EquipPostion.class, "装备基础属性"),
	GEM(EquipPostion.class, "装备部位镶嵌宝石属性")
	;

	private Class<?> keyClass;
	private String desc;

	EquipAttrNode(Class<?> keyClass, String desc) {
		this.keyClass = keyClass;
		this.desc = desc;
	}

	@Override
	public Class<?> keyClass() {
		return keyClass;
	}

	@Override
	public String desc() {
		return desc;
	}
}

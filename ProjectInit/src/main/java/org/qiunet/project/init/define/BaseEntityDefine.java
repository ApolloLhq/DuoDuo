package org.qiunet.project.init.define;

import org.qiunet.data.core.entity.IEntity;
import org.qiunet.project.init.enums.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/***
 *
 *
 * qiunet
 * 2019-08-14 22:31
 ***/
public abstract class BaseEntityDefine implements IEntityDefine {
	/***
	 * 对象的类名
	 */
	private String name;
	/***
	 * 主键
	 */
	private String key;
	/***
	 * 包名相对于userDir的路径
	 */
	private String baseDir;
	/***
	 * 包名 路径
	 */
	private String packageName;
	/***
	 * 所有的字段定义
	 */
	private List<FieldDefine> fieldDefines = new ArrayList<>();
	/***
	 * 所有的构造函数定义
	 */
	private List<ConstructorDefine> constructorDefines = new ArrayList<>();

	private EntityType entityType;
	private Class<? extends IEntity> entityClass;

	protected BaseEntityDefine(EntityType entityType, Class<? extends IEntity> entityClass) {
		this.entityType = entityType;
		this.entityClass = entityClass;
	}

	public Class<? extends IEntity> getEntityClass() {
		return entityClass;
	}

	@Override
	public EntityType getType() {
		return entityType;
	}

	@Override
	public String getDoName() {
		return name;
	}

	@Override
	public String getBoName() {
		return name.replace("Do", "Bo");
	}

	@Override
	public String getEntityPackage() {
		return packageName+".entity";
	}

	@Override
	public List<ConstructorDefine> getConstructorDefines() {
		return constructorDefines;
	}

	@Override
	public List<FieldDefine> getFieldDefines() {
		return fieldDefines;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void addField(FieldDefine fieldDefine) {
		this.fieldDefines.add(fieldDefine);
	}

	public void addConstructor(ConstructorDefine constructorDefine){
		this.constructorDefines.add(constructorDefine);
		constructorDefine.init(this);
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String getKeyName() {
		Optional<FieldDefine> keyField = fieldDefines.stream().filter(f -> f.getName().equals(key)).findFirst();
		FieldDefine fieldDefine = keyField.orElseThrow(() -> new NullPointerException("DoName ["+name+"] have not a field named ["+key+"]"));
		return fieldDefine.getName();
	}

	@Override
	public String getKeyType() {
		Optional<FieldDefine> keyField = fieldDefines.stream().filter(f -> f.getName().equals(key)).findFirst();
		FieldDefine fieldDefine = keyField.orElseThrow(() -> new NullPointerException("DoName ["+name+"] have not a field named ["+key+"]"));

		switch (fieldDefine.getType()) {
			case "int":
				return "Integer";
			case "long":
				return "Long";
			case "String":
				return "String";
			default:
				throw new IllegalArgumentException("not support key type "+fieldDefine.getType());
		}
	}
}

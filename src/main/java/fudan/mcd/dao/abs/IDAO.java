package fudan.mcd.dao.abs;

/**
 * 数据访问对象的抽象接口。
 *
 * @param <PK>
 *            数据的主键类型
 * @param <VO>
 *            数据的值对象类型
 */
public interface IDAO<PK, VO> {
	/**
	 * 将值对象插入到数据表中。无论值对象是否包含主键参数，总是向数据表中插入一条新的记录。
	 * 
	 * @param vo
	 *            插入的值对象
	 * @return 如果插入成功，返回新增记录的主键；否则，返回负值（默认值为-1）。
	 */
	PK insert(VO vo);

	/**
	 * 删除主键对应的值对象。如果数据表中不存在该值对象，则不执行任何操作。
	 * 
	 * @param pk
	 *            删除的值对象的主键
	 * @return 如果删除成功，返回删除的值对象。如果删除失败，返回null。
	 */
	VO delete(PK pk);

	/**
	 * 更新值对象。如果数据表中先前不存在该值对象，不执行任何操作。
	 * 
	 * @param vo
	 *            更新的值对象
	 * @return 如果更新成功，返回正值（默认值为1）；否则，返回负值（默认值为-1）。
	 */
	int update(VO vo);

	/**
	 * 查找主键对应的值对象。
	 * 
	 * @param pk
	 *            查找的值对象的主键
	 * @return 如果主键对应的值对象存在数据表中，返回该值对象；否则，返回null。
	 */
	VO query(PK pk);
}

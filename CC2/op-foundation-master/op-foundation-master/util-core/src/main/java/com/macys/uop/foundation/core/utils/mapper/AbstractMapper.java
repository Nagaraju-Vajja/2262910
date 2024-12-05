package com.macys.uop.foundation.core.utils.mapper;

/**
 * MapStruct utility interface to be used for data mapping between Resource model class and Entity class.
 *
 * @param <R> Resource Class Type
 * @param <E> Entity Class Type
 */
public interface AbstractMapper<R, E> {
	
	/**
	 * Copy Entity class Object to Resource Class Object
	 * 
	 * @param Entity class Object
	 * 
	 * @return Resource class Object
	 */
	R convertToResource(E entity);
	
	/**
	 * Copy Resource class Object to Entity class Object
	 * 
	 * @param Resource class Object
	 * 
	 * @return Entity class Object
	 */
	E convertToEntity(R resource);
}

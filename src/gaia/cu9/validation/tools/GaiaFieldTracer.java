/*
 * CU9 WP940 Validation
 * Copyright (C) 2006-2014 Gaia Data Processing and Analysis Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package gaia.cu9.validation.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gaia.cu1.tools.dal.ObjectFactory;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;

/**
 * A simple tool to analysis the fields in a Gaia data model
 *
 * @author I-Chun Shih (I-Chun.Shih@obspm.fr)
 * @version $Id: FieldNameMerger.java 530489 2016-09-30 14:20:23Z ishih $
 */
public class GaiaFieldTracer {
	
//	Class<? extends GaiaRoot> dmClaz;
    List<Field> fieldList = null;
    Map<String, String> mapping = null;
//  Map<String, String[]> fieldValueBoundary;
//  static volatile GaiaFieldTracer instance = null;
    
    private static final Logger LOG = LoggerFactory.getLogger(GaiaFieldTracer.class);
    
    /**
     * Class constructor.
     * @param dmClaz the subclass of GaiaRoot
     */
    public GaiaFieldTracer(Class<? extends GaiaRoot> dmClaz) {
    	try {
			recurseSuperClasses(dmClaz);
			mappingFieldType();
		} catch (GaiaException e) {
			LOG.error("Unable to visit the {}, see ", dmClaz.getName(), e);
		}
    }
    
	void recurseSuperClasses(Class<? extends GaiaRoot> dmClaz) throws GaiaException {
		fieldList = new ArrayList<>();
		Class<?> claz = ObjectFactory.getImplementingClass(dmClaz);
		Class<?> superClaz = null;
		do {
			fieldList.addAll(Stream.of(claz.getDeclaredFields())
				.filter(f -> f.getModifiers() == 4)
				.collect(Collectors.toList()));
			superClaz = claz.getSuperclass();
			LOG.debug("{} <- {}", claz.getName(), superClaz.getName());
			claz = superClaz;
		} while (!superClaz.equals(Object.class));
	}

	void mappingFieldType() {
		mapping = new HashMap<>();
		if (fieldList != null && !fieldList.isEmpty()) {
			for (Field f : fieldList)
				mapping.put(f.getName().toUpperCase(), f.getType().getSimpleName());
		}
	}
	
	/**
     * 
     * @param fieldName
     * @return 
     */
    public String getFieldType(String fieldName) {
    	String ft = null;
    	if (mapping != null && !mapping.isEmpty())
    		ft = mapping.get(fieldName.toUpperCase());
    	return ft;
    }
}

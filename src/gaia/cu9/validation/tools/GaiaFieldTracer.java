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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gaia.cu1.tools.dal.ObjectFactory;
import gaia.cu1.tools.dm.GaiaRoot;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu9.archive.architecture.core.dm.GaiaSource;

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
    
    public GaiaFieldTracer() {
    }
    
    /**
     * Class constructor.
     * @param dmClaz the subclass of GaiaRoot
     */
    public GaiaFieldTracer(Class<? extends GaiaRoot> dmClaz) {
    	try {
    		fieldList = recurseSuperClasses(dmClaz);
			mappingFieldType();
		} catch (GaiaException e) {
			LOG.error("Unable to visit the {}, see ", dmClaz.getName(), e);
		}
    }
    
    List<Field> recurseSuperClasses(String dmClaz) {
    	List<Field> fieldList = new ArrayList<>();
		try {
			fieldList = recurseSuperClasses(Class.forName(dmClaz));
		} catch (ClassNotFoundException e) {
			LOG.error("Cannot obtain the class, see ", e);
		} catch (GaiaException e) {
			LOG.error("Cannot obtain the class implmentation, see ", e);
		}
		return fieldList;
    }
    
    List<Field> recurseSuperClasses(Class<?> dmClaz) throws GaiaException {
    	List<Field> fieldList = new ArrayList<>();
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
		return fieldList;
	}

	void mappingFieldType() {
		mapping = new HashMap<>();
		if (fieldList != null && !fieldList.isEmpty()) {
			for (Field f : fieldList)
				mapping.put(f.getName().toUpperCase(), f.getType().getSimpleName());
		}
	}
	
	List<String> getNode(Class<?> RootDmClaz) {
		List<String> nodes = new ArrayList<>();
		try {
//			Class<?> claz = ObjectFactory.getImplementingClass(RootDmClaz);
			nodes.addAll(
					recurseSuperClasses(RootDmClaz).stream()
//					Stream.of(claz.getDeclaredFields())
			.filter(f -> f.getModifiers() == 4)
			.filter(f -> !(f.getType().isPrimitive() || f.getType().getName().contains("java.lang")))
			.filter(f -> !f.getType().isEnum())
			.map(f -> matchGaiaDm(f.getGenericType().getTypeName()))
			.filter(f -> f != null)
			.collect(Collectors.toList())
			);
		} catch (GaiaException e) {
			LOG.error("Cannot obtain the class implementation, see ", e);
		}
		return nodes;
	}

	@Deprecated
	Map<Integer, List<String>> getNodes(Class<?> RootDmClaz) {
		Map<Integer, List<String>> totalNodes = new HashMap<>();
		int level = 0;
		int numNodes = 1;
		List<String> levelNodes = new ArrayList<>();
		levelNodes.add(RootDmClaz.getName());
		totalNodes.put(level, levelNodes);
		while (numNodes != 0) {
			numNodes = 0;
			List<String> temp = new ArrayList<>();
			for (String s : levelNodes) {
				try {
					List<String> thisNode = getNode(Class.forName(s));
					numNodes = numNodes + thisNode.size();
					temp.addAll(thisNode);
				} catch (ClassNotFoundException e) {
					LOG.error("Cannot obtain the class, see ", e);
				}
			}
			level++;
			if (numNodes != 0) {
				totalNodes.put(level, temp);
				levelNodes = new ArrayList<>();
				levelNodes.addAll(temp);
			}
		}
		return totalNodes;
	}
	
	Map<String, Integer> getNodeMap(Class<?> RootDmClaz) {
		Map<String, Integer> nodeMap = new HashMap<>();
		int level = 0;
		int numNodes = 1;
		List<String> levelNodes = new ArrayList<>();
		levelNodes.add(RootDmClaz.getName());
		nodeMap.put(RootDmClaz.getName(), level);
		while (numNodes != 0) {
			numNodes = 0;
			List<String> temp = new ArrayList<>();
			for (String s : levelNodes) {
				try {
					List<String> thisNode = getNode(Class.forName(s));
					LOG.debug("Number of nodes in " + s + ": " + thisNode.size());
					numNodes = numNodes + thisNode.size();
					temp.addAll(thisNode);
				} catch (ClassNotFoundException e) {
					LOG.error("Cannot obtain the class, see ", e);
				}
			}
			level++;
			levelNodes = new ArrayList<>();
			levelNodes.addAll(temp);
			if (numNodes != 0) {
				for (String n : temp) {
					nodeMap.put(n, level);
				}
			}
		}
		return nodeMap;
	}
	
	String matchGaiaDm(String string) {
		Pattern pattern = Pattern.compile("gaia.*\\w");
		Matcher matcher = pattern.matcher(string);
		if (matcher.find())
			return string.substring(matcher.start(),matcher.end());
		else
			return null;
	}

	void getProperties(Class<?> dmClaz) {
		String template = "<property name=\"%s\" type=\"%s\"/>";
		Set<String> nodes = getNodeMap(dmClaz).keySet();
		try {
			recurseSuperClasses(dmClaz).stream()
				.filter(f -> !nodes.contains(matchGaiaDm(f.getGenericType().getTypeName())))
				.forEach(f -> System.out.println(String.format(template, f.getName(), f.getType().getName())));
		} catch (GaiaException e) {
			LOG.error("Cannot obtain the class implementation, see ", e);
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

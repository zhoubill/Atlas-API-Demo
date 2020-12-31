package com.cmcc;

import java.util.List;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.typesystem.TypesDef;
import org.apache.atlas.typesystem.json.TypesSerialization;
import org.apache.atlas.typesystem.types.AttributeDefinition;
import org.apache.atlas.typesystem.types.ClassType;
import org.apache.atlas.typesystem.types.DataTypes;
import org.apache.atlas.typesystem.types.EnumTypeDefinition;
import org.apache.atlas.typesystem.types.HierarchicalTypeDefinition;
import org.apache.atlas.typesystem.types.Multiplicity;
import org.apache.atlas.typesystem.types.StructTypeDefinition;
import org.apache.atlas.typesystem.types.utils.TypesUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;


public class AtlasTest {
	
	final AtlasClient atlasClient = new AtlasClient
            (new String[]{"http://10.136.106.76:21000"},
                    new String[]{"admin",
                            "admin"});

    static final String DATABASE_TYPE = "DB_Sync";
    static final String COLUMN_TYPE = "Column_Sync";
    static final String TABLE_TYPE = "Table_Sync";
    static final String VIEW_TYPE = "View_Sync";
    public static final String DB_ATTRIBUTE = "db";
    static final String STORAGE_DESC_TYPE = "StorageDesc";
    public static final String COLUMNS_ATTRIBUTE = "columns";
    public static final String INPUT_TABLES_ATTRIBUTE = "inputTables";
    private static final String[] TYPES =
            {DATABASE_TYPE, TABLE_TYPE, STORAGE_DESC_TYPE, COLUMN_TYPE, VIEW_TYPE, "JdbcAccess",
                    "ETL", "Metric", "PII", "Fact", "Dimension", "Log Data"};

	public static void main(String[] args) {
		AtlasTest atlasTest = new AtlasTest();
		try {
			atlasTest.createTypes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	 /**
     * 组织定义types
     * @return
     */
    TypesDef createTypeDefinitions() {
        HierarchicalTypeDefinition<ClassType> dbClsDef = TypesUtil
                .createClassTypeDef(DATABASE_TYPE, DATABASE_TYPE, null,
                        TypesUtil.createUniqueRequiredAttrDef("name", DataTypes.STRING_TYPE),
                        attrDef("description", DataTypes.STRING_TYPE.getName()), attrDef("locationUri", DataTypes.STRING_TYPE.getName()),
                        attrDef("owner", DataTypes.STRING_TYPE.getName()), attrDef("createTime", DataTypes.LONG_TYPE.getName()));

        HierarchicalTypeDefinition<ClassType> columnClsDef = TypesUtil
                .createClassTypeDef(COLUMN_TYPE, COLUMN_TYPE, null, attrDef("name", DataTypes.STRING_TYPE.getName()),
                        attrDef("dataType", DataTypes.STRING_TYPE.getName()), attrDef("comment", DataTypes.STRING_TYPE.getName()));

        HierarchicalTypeDefinition<ClassType> tblClsDef = TypesUtil
                .createClassTypeDef(TABLE_TYPE, TABLE_TYPE, ImmutableSet.of("DataSet"),
                        new AttributeDefinition(DB_ATTRIBUTE, DATABASE_TYPE, Multiplicity.REQUIRED, false, null),
                        new AttributeDefinition("sd", STORAGE_DESC_TYPE, Multiplicity.REQUIRED, true, null),
                        attrDef("owner", DataTypes.STRING_TYPE.getName()), attrDef("createTime", DataTypes.LONG_TYPE.getName()),
                        attrDef("lastAccessTime", DataTypes.LONG_TYPE.getName()), attrDef("retention", DataTypes.LONG_TYPE.getName()),
                        attrDef("viewOriginalText", DataTypes.STRING_TYPE.getName()),
                        attrDef("viewExpandedText", DataTypes.STRING_TYPE.getName()), attrDef("tableType", DataTypes.STRING_TYPE.getName()),
                        attrDef("temporary", DataTypes.BOOLEAN_TYPE.getName()),
                        new AttributeDefinition(COLUMNS_ATTRIBUTE, DataTypes.arrayTypeName(COLUMN_TYPE),
                                Multiplicity.COLLECTION, true, null));

        HierarchicalTypeDefinition<ClassType> viewClsDef = TypesUtil
                .createClassTypeDef(VIEW_TYPE, VIEW_TYPE, ImmutableSet.of("DataSet"),
                        new AttributeDefinition("db", DATABASE_TYPE, Multiplicity.REQUIRED, false, null),
                        new AttributeDefinition("inputTables", DataTypes.arrayTypeName(TABLE_TYPE),
                                Multiplicity.COLLECTION, false, null));

        return TypesUtil.getTypesDef(ImmutableList.<EnumTypeDefinition>of(), ImmutableList.<StructTypeDefinition>of(),
                ImmutableList.of(),
                ImmutableList.of(dbClsDef, columnClsDef, tblClsDef, viewClsDef));
    }
    
    private void createTypes() throws Exception {
        TypesDef typesDef = createTypeDefinitions();
        String typesAsJSON =  TypesSerialization.toJson(typesDef);
        System.out.println("typesAsJSON = " + typesAsJSON);
        atlasClient.createType(typesAsJSON);
        verifyTypesCreated();
    }

    private void verifyTypesCreated() throws Exception {
        List<String> types = atlasClient.listTypes();
        for (String type : TYPES) {
            assert types.contains(type);
        }
    }

    AttributeDefinition attrDef(String name, String dT) {
        return attrDef(name, dT, Multiplicity.OPTIONAL, false, null);
    }

    AttributeDefinition attrDef(String name, String dT, Multiplicity m, boolean isComposite,
                                String reverseAttributeName) {
        return new AttributeDefinition(name, dT, m, isComposite, reverseAttributeName);
    }


}
